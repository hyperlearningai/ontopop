package ai.hyperlearning.ontopop.data.ingestors.ontology;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.WebhookEventRepository;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.utils.git.GitService;
import ai.hyperlearning.ontopop.utils.git.GitServiceFactory;

/**
 * Ontology Ingestion Service - Pipeline
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class OntologyIngestorPipeline {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyIngestorPipeline.class);
	
	@Autowired
	private GitServiceFactory gitServiceFactory;
	
	@Autowired
	private OntologyRepository ontologyRepository;
	
	@Autowired
	private WebhookEventRepository webhookEventRepository;
	
	private Map<String, String> headers;
	private String payload;
	private GitService gitService;
	private Set<WebhookEvent> webhookEvents = new HashSet<>();
	public OntologyIngestorPipeline() {
		
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
	 * Download the modified resources to the local filesystem, and then
	 * upload these resources to the relevant persistent storage service
	 * @throws IOException
	 */
	
	private void getModifiedResources() throws IOException {
		
		LOGGER.info("Ontology Ingestion Service - "
				+ "Started downloading modified resources locally.");
		for ( WebhookEvent webhookEvent : webhookEvents ) {
			
			// Get the resource as a string encapsulated within a 
			// HTTP response entity
			ResponseEntity<String> response = webhookEvent
					.getOntology().isRepoPrivate() ?
							gitService.getFile(
									webhookEvent.getOntology().getRepoToken(), 
									webhookEvent.getRepoOwner(), 
									webhookEvent.getRepoName(), 
									webhookEvent.getOntology().getRepoResourcePath(), 
									webhookEvent.getRepoBranch()) : 
							gitService.getFile(
									webhookEvent.getRepoOwner(), 
									webhookEvent.getRepoName(), 
									webhookEvent.getOntology().getRepoResourcePath(), 
									webhookEvent.getRepoBranch());
			
			// Check the response is OK
			if ( response.getStatusCode().equals(HttpStatus.OK) 
					&& !Strings.isNullOrEmpty(response.getBody()) ) {
				
				// Write the string contents to a file in the local filesystem
				String modifiedResource = response.getBody();
				// PENDING
				
				// Upload the file to the relevant persistent storage service
				// PENDING
				
			}
			
		}
		
		LOGGER.info("Ontology Ingestion Service - "
				+ "Finished downloading modified resources locally.");
		
	}
	
}
