package ai.hyperlearning.ontopop.data.ontology.validator;

import java.io.File;
import java.io.IOException;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineSource;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Validation Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@EnableBinding(DataPipelineSource.class)
public class OntologyValidatorService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyValidatorService.class);
	
	@Autowired
	private ObjectStorageServiceFactory objectStorageServiceFactory;
	
	@Autowired
	private DataPipelineSource dataPipelineSource;
	
	@Value("${storage.object.service}")
	private String storageObjectService;
	
	@Value("${storage.object.local.baseUri}")
	private String storageLocalBaseUri;
	
	@Value("${storage.object.dirNames.ingested}")
	private String ingestedDirectoryName;
	
	@Value("${storage.object.dirNames.validated}")
	private String validatedDirectoryName;
	
	@Value("${storage.object.patterns.fileNameIdsSeparator}")
	private String filenameIdsSeparator;
	
	private WebhookEvent webhookEvent;
	private ObjectStorageService objectStorageService;
	private String readObjectUri;
	private String writeDirectoryUri;
	private String filename;
	private String downloadedFileUri;
	private boolean semanticallyValid = false;
	
	/**
	 * Run the Ontology Validation service end-to-end pipeline
	 */
	
	public void run(WebhookEvent webhookEvent) {
		
		LOGGER.info("Ontology Validation Service started.");
		this.webhookEvent = webhookEvent;
		
		try {
			
			// 1. Environment setup
			setup();
			
			// 2. Download the ingested ontology from persistent storage
			download();
			
			// 3. Semantically validate the ingested ontology
			validate();
			
			// 4. Publish a message containing the semantic validation result
			publish();
			
			// 5. Copy the ingested ontology to the validated directory 
			// in persistent storage if it is semantically valid
			save();
			
			// 6. Cleanup resources
			cleanup();
			
		} catch (Exception e) {
			LOGGER.error("Ontology Validation Service encountered an error.", e);
		}
		
		LOGGER.info("Ontology Validation Service finished.");
		
	}
	
	/**
	 * Instantiate the relevant object storage service
	 * @throws IOException
	 */
	
	private void setup() throws IOException {
		
		// 1. Select the relevant persistent storage service and 
		// create the target directory if it does not already exist
		ObjectStorageServiceType objectStorageServiceType = 
				ObjectStorageServiceType.valueOfLabel(
						storageObjectService.toUpperCase());
		objectStorageService = objectStorageServiceFactory
				.getObjectStorageService(objectStorageServiceType);
		LOGGER.debug("Using the {} object storage service.", 
				objectStorageServiceType);
		
		// 2. Reconstruct the persisted resource filename
		filename = webhookEvent.getOntology().generateFilenameForPersistence(
				webhookEvent.getId(), filenameIdsSeparator);
		
		// 3. Define and create (if required) the relevant 
		// target validation directory
		switch ( objectStorageServiceType ) {
			
			case LOCAL:
				
				// Create (if required) the local target validation directory
				readObjectUri = storageLocalBaseUri 
					+ File.separator + ingestedDirectoryName 
					+ File.separator + filename;
				writeDirectoryUri = storageLocalBaseUri + 
					File.separator + validatedDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
				break;
				
			default:
				
				// Create (if required) the Azure Storage container 
				// or AWS S3 bucket
				readObjectUri = ingestedDirectoryName + "/" + filename;
				writeDirectoryUri = validatedDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
			
		}
		
	}
	
	/**
	 * Download the ingested ontology from persistent storage
	 * to a temporary file in local storage
	 * @throws IOException
	 */
	
	private void download() throws IOException {
		
		LOGGER.info("Ontology Validation Service - "
				+ "Started downloading the ingested resource.");
		downloadedFileUri = objectStorageService.downloadObject(readObjectUri, 
				filenameIdsSeparator + filename);
		LOGGER.debug("Downloaded ingested resource to '{}'.", 
				downloadedFileUri);
		LOGGER.info("Ontology Validation Service - "
				+ "Finished downloading the ingested resource.");
	}
	
	/**
	 * Semantically validate the ingested ontology using the HermiT reasoner
	 * @throws IOException
	 * @throws OWLOntologyCreationException 
	 */
	
	private void validate() throws IOException, OWLOntologyCreationException {
		
		LOGGER.info("Ontology Validation Service - "
				+ "Started the semantic validation of the ingested resource.");
		
		// Load the OWL file into memory using the OWL API
		OWLOntologyManager manager = 
				OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager
				.loadOntologyFromOntologyDocument(
						new File(downloadedFileUri));
		
		// Validate the consistency of the ontology
		Configuration configuration = new Configuration();
		OWLReasoner reasoner = new Reasoner(configuration, ontology);
		semanticallyValid = reasoner.isConsistent();
		
		LOGGER.debug("Semantic validation of '{}' result: {}", 
				downloadedFileUri, semanticallyValid);
		LOGGER.info("Ontology Validation Service - "
				+ "Finished the semantic validation of the ingested resource.");
		
	}
	
	/**
	 *  Publish a message containing the semantic validation result
	 */
	
	private void publish() {
		
		LOGGER.info("Ontology Validation Service - "
				+ "Started publishing message.");
		webhookEvent.setRepoResourceSemanticallyValid(semanticallyValid);
		dataPipelineSource.validatedPublicationChannel()
			.send(MessageBuilder.withPayload(webhookEvent).build());
		LOGGER.info("Ontology Validation Service - "
				+ "Finished publishing message.");
		
	}
	
	/**
	 * Copy the ingested ontology to the validated directory 
	 * in persistent storage if it is semantically valid
	 * @throws IOException
	 */
	
	private void save() throws IOException {
		
		if (semanticallyValid) {
			
			LOGGER.info("Ontology Validation Service - "
					+ "Started the persistence of the validated resource.");
			String targetFilepath = writeDirectoryUri + "/" + filename;
			objectStorageService.uploadObject(
					downloadedFileUri, 
					targetFilepath);
			LOGGER.debug("Successfully persisted validated ontology "
					+ "resource '{}' to '{}'.", 
					webhookEvent.getOntology().getRepoUrl() + "/" 
							+ webhookEvent.getOntology()
								.getRepoResourcePath(), 
					targetFilepath);
			LOGGER.info("Ontology Validation Service - "
					+ "Finished the persistence of the validated resource.");
			
		}
		
	}
	
	/**
	 * Cleanup any open resources
	 */
	
	private void cleanup() throws IOException {
		
		// Close any storage service clients
		objectStorageService.cleanup();
		
	}

}
