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
	private static final String INGESTED_DIRECTORY_NAME = "ingested";
	private static final String ONTOLOGY_FILENAME_SEPARATOR = "_";
	
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
	
	@Value("${security.vault.paths.subpaths.ontologies}")
	private String vaultSubpathOntologies;
	
	private Map<String, String> headers;
	private String payload;
	private GitService gitService;
	private Set<WebhookEvent> webhookEvents = new HashSet<>();
	private FileStorageService fileStorageService;
	
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
			
			// 1. Parse the webhook event payload into WebhookEvent objects
			parseWebhookEvents();
			
			// 2. Save the relevant modified resources to persistent storage
			getModifiedResources();
			
		} catch (Exception e) {
			LOGGER.error("Ontology Ingestion Service encountered an error.", e);
		}
		
		LOGGER.info("Ontology Ingestion Service finished.");
		
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
			
		// 1. Select the relevant Git service based on the HTTP headers
		// of the webhook event request
		gitService = gitServiceFactory.getGitService(headers);
		
		// 2. Parse the webhook payload
		WebhookEvent webhookEvent = gitService.parseWebhookPayload(
				headers, payload, null);
		LOGGER.debug("Original webhook event object: {} ", webhookEvent);
		
		// 3. Get all ontology objects that match the webhook event request
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
		
		// 4. For each ontology, validate the webhook payload
		for (Ontology ontology : ontoglogies) {
			
			// 4.1. Get the ontology secret data
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
			
			// 4.1. Recreate the webhook payload object 
			// with the resource path
			WebhookEvent currentWebhookEvent = gitService
					.parseWebhookPayload(headers, payload, 
							ontology.getRepoResourcePath());
			currentWebhookEvent.setOntology(ontology);
			
			// 4.2. Validate the webhook payload
			boolean validWebhookPayload = gitService.isValidWebhookPayload(
					headers, payload, 
					ontology.getRepoResourcePath(), 
					ontology.getRepoWebhookSecret(), 
					ontology.getRepoName(), 
					ontology.getRepoOwner(),
					ontology.getRepoBranch());
			
			// 4.3. If valid, then persist the webhook event to storage
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
		
		// 1. Select the relevant storage service and 
		// create the target directory if it does not already exist
		fileStorageService = fileStorageServiceFactory
				.getFileStorageService(storageFileService);
		String writeDirectoryUri = storageFileBaseUri + 
				File.separator + INGESTED_DIRECTORY_NAME;
		if ( !fileStorageService.doesDirectoryExist(writeDirectoryUri) )
			fileStorageService.createDirectory(writeDirectoryUri);
		
		// 2. Download and write the modified resources to persistent storage
		for ( WebhookEvent webhookEvent : webhookEvents ) {
			
			// Get the resource as a string encapsulated within a 
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
			
			// Check the response is OK
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
						ONTOLOGY_FILENAME_SEPARATOR 
						+ webhookEvent.getOntology().getId() 
						+ ONTOLOGY_FILENAME_SEPARATOR 
						+ webhookEvent.getId()
						+ ONTOLOGY_FILENAME_SEPARATOR
						+ resourcePathFilename);
				Files.write(temporaryFile, 
						response.getBody().getBytes(StandardCharsets.UTF_8));
				
				// Upload the file to the relevant persistent storage service
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
