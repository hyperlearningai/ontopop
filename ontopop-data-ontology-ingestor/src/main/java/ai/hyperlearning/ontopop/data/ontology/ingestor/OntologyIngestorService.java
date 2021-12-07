package ai.hyperlearning.ontopop.data.ontology.ingestor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;

import com.google.common.base.Strings;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.WebhookEventRepository;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologySecretData;
import ai.hyperlearning.ontopop.security.vault.Vault;
import ai.hyperlearning.ontopop.utils.git.GitService;
import ai.hyperlearning.ontopop.utils.git.GitServiceFactory;
import ai.hyperlearning.ontopop.utils.storage.FileStorageService;
import ai.hyperlearning.ontopop.utils.storage.FileStorageServiceFactory;

/**
 * Ontology Ingestion Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@Transactional
public class OntologyIngestorService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyIngestorService.class);
	
	@Autowired
	private GitServiceFactory gitServiceFactory;
	
	@Autowired
	private OntologyRepository ontologyRepository;
	
	@Autowired
	private WebhookEventRepository webhookEventRepository;
	
	@Autowired
	private FileStorageServiceFactory fileStorageServiceFactory;
	
	@Autowired
	private VaultTemplate vaultTemplate;
	
	@Value("${spring.cloud.vault.kv.backend}")
	String springCloudVaultKvBackend;
	
	@Value("${spring.cloud.vault.kv.default-context}")
	String springCloudVaultKvDefaultContext;
	
	@Value("${storage.file.service}")
	private String storageFileService;
	
	@Value("${storage.file.baseUri}")
	private String storageFileBaseUri;
	
	@Value("${storage.file.dirNames.ingested}")
	private String ingestedDirectoryName;
	
	@Value("${storage.file.patterns.fileNameIdsSeparator}")
	private String filenameIdsSeparator;
	
	@Value("${security.vault.paths.subpaths.ontologies}")
	private String vaultSubpathOntologies;
	
	private Map<String, String> headers;
	private String payload;
	private GitService gitService;
	private Set<WebhookEvent> webhookEvents = new HashSet<>();
	private FileStorageService fileStorageService;
	private String writeDirectoryUri;
	
	public OntologyIngestorService() {
		
	}
	
	/**
	 * Run the Ontology Ingestion service end-to-end pipeline
	 */
	
	public void run(Map<String, String> headers, String payload) {
		
		LOGGER.info("Ontology Ingestion Service started.");
		this.headers = headers;
		this.payload = payload;
		
		try {
			
			// 1. Environment setup
			setup();
			
			// 2. Parse the webhook event payload into WebhookEvent objects
			parseWebhookEvents();
			
			// 3. Save the relevant modified resources to persistent storage
			getModifiedResources();
			
		} catch (Exception e) {
			LOGGER.error("Ontology Ingestion Service encountered an error.", e);
		}
		
		LOGGER.info("Ontology Ingestion Service finished.");
		
	}
	
	/**
	 * Instantiate the relevant Git and file storage services
	 * @throws IOException
	 */
	
	private void setup() throws IOException {
		
		// 1. Select the relevant Git service based on the HTTP headers
		// of the webhook event request
		gitService = gitServiceFactory.getGitService(headers);
		
		// 2. Select the relevant storage service and 
		// create the target directory if it does not already exist
		fileStorageService = fileStorageServiceFactory
				.getFileStorageService(storageFileService);
		writeDirectoryUri = storageFileBaseUri + 
				File.separator + ingestedDirectoryName;
		if ( !fileStorageService.doesDirectoryExist(writeDirectoryUri) )
			fileStorageService.createDirectory(writeDirectoryUri);
		
	}
	
	/**
	 * A single webhook event may describe multiple resources belonging
	 * to the same repository that have been modified. In this method, 
	 * we create separate WebhookEvent objects representing each
	 * separate but relevant resource path. We know if a resource path is
	 * relevant if there exists an Ontology object that defines that
	 * resource path to watch.
	 * @throws IOException
	 */
	
	private void parseWebhookEvents() throws IOException {
		
		LOGGER.info("Ontology Ingestion Service - "
				+ "Started parsing of webhook events.");
		
		// 1. Parse the webhook payload
		WebhookEvent webhookEvent = gitService.parseWebhookPayload(
				headers, payload, null);
		LOGGER.debug("Original webhook event object: {} ", webhookEvent);
		
		// 2. Get all ontology objects that match the webhook event request
		// i.e. the payload may describe more than one resource path
		// in the same repository that have been modified
		Set<Ontology> ontoglogies = new HashSet<>();
		for ( String modifiedResourcePath : webhookEvent
				.getCommitsModifiedResourcePaths() ) {
			ontoglogies.addAll(
					ontologyRepository.findByRepoUrlOwnerPathBranch(
							webhookEvent.getRepoUrl(), 
							webhookEvent.getRepoOwner(), 
							modifiedResourcePath, 
							webhookEvent.getRepoBranch()));
		}
		
		// 3. For each ontology, validate the webhook payload
		for (Ontology ontology : ontoglogies) {
			
			// 3.1. Get the ontology secret data
			OntologySecretData ontologySecretData = Vault.get(
					vaultTemplate, 
					springCloudVaultKvBackend, 
					KeyValueBackend.KV_2, 
					springCloudVaultKvDefaultContext 
						+ vaultSubpathOntologies 
							+ ontology.getId(), 
					OntologySecretData.class).getData();
			ontology.setRepoToken(
					ontologySecretData.getRepoToken());
			ontology.setRepoWebhookSecret(
					ontologySecretData.getRepoWebhookSecret());
			
			// 3.2. Recreate the webhook payload object 
			// with the resource path
			WebhookEvent currentWebhookEvent = gitService
					.parseWebhookPayload(headers, payload, 
							ontology.getRepoResourcePath());
			currentWebhookEvent.setOntology(ontology);
			
			// 3.3. Validate the webhook payload
			boolean validWebhookPayload = gitService.isValidWebhookPayload(
					headers, payload, 
					ontology.getRepoResourcePath(), 
					ontology.getRepoWebhookSecret(), 
					ontology.getRepoName(), 
					ontology.getRepoOwner(),
					ontology.getRepoBranch());
			
			// 3.4. If valid, then persist the webhook event to storage
			if ( validWebhookPayload ) {
				webhookEvents.add(
						webhookEventRepository.save(currentWebhookEvent));
				LOGGER.debug("Parsed and persisted webhook event object: {}", 
						currentWebhookEvent);
			}
			
		}
		
		LOGGER.info("Ontology Ingestion Service - "
				+ "Finished parsing of webhook events.");
		
	}
	
	/**
	 * Download the modified resources to the local file system in a temporary
	 * location, and then upload these resources to the relevant 
	 * persistent storage service.
	 * @throws IOException
	 */
	
	private void getModifiedResources() throws IOException {
		
		LOGGER.info("Ontology Ingestion Service - "
				+ "Started downloading modified resources.");
		
		// 1. Download and write the modified resources to persistent storage
		for ( WebhookEvent webhookEvent : webhookEvents ) {
			
			// 1.1. Get the resource as a string encapsulated within a 
			// HTTP response entity
			ResponseEntity<String> response = webhookEvent
					.getOntology().isRepoPrivate() ?
							gitService.getFile(
									webhookEvent.getOntology()
										.getRepoToken(), 
									webhookEvent.getRepoOwner(), 
									webhookEvent.getRepoName(), 
									webhookEvent.getOntology()
										.getRepoResourcePath(), 
									webhookEvent.getRepoBranch()) : 
							gitService.getFile(
									webhookEvent.getRepoOwner(), 
									webhookEvent.getRepoName(), 
									webhookEvent.getOntology()
										.getRepoResourcePath(), 
									webhookEvent.getRepoBranch());
			
			// 1.2. Check the response is OK
			if ( response.getStatusCode().equals(HttpStatus.OK) 
					&& !Strings.isNullOrEmpty(response.getBody()) ) {
				
				// Write the string contents to a temporary file 
				// in the local file system
				String resourcePathFilename = Paths.get(
						webhookEvent.getOntology().getRepoResourcePath())
							.getFileName()
							.toString()
							.replaceAll(" ", "-");
				Path temporaryFile = Files.createTempFile("", 
						filenameIdsSeparator 
						+ webhookEvent.getOntology().getId() 
						+ filenameIdsSeparator 
						+ webhookEvent.getId()
						+ filenameIdsSeparator
						+ resourcePathFilename);
				Files.write(temporaryFile, 
						response.getBody().getBytes(StandardCharsets.UTF_8));
				
				// 1.3. Upload the file to the relevant persistent storage service
				String temporaryFilename = temporaryFile
						.getFileName().toString();
				String targetFilename = temporaryFilename
						.substring(temporaryFilename.indexOf("_") + 1);
				String targetFilepath = writeDirectoryUri +
						File.separator + targetFilename;
				fileStorageService.uploadFile(
						temporaryFile.toAbsolutePath().toString(), 
						targetFilepath);
				LOGGER.debug("Successfully persisted ontology "
						+ "resource '{}' to '{}'.", 
						webhookEvent.getOntology().getRepoResourcePath(),
						targetFilepath);
				
			}
			
		}
		
		LOGGER.info("Ontology Ingestion Service - "
				+ "Finished downloading modified resources.");
		
	}
	
}
