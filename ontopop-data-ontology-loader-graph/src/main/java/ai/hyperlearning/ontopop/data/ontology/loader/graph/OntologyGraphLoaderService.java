package ai.hyperlearning.ontopop.data.ontology.loader.graph;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceFactory;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceType;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineModelledLoaderSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Graph Loading Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@EnableBinding(DataPipelineModelledLoaderSource.class)
public class OntologyGraphLoaderService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyGraphLoaderService.class);
	
	@Autowired
	private ObjectStorageServiceFactory objectStorageServiceFactory;
	
	@Autowired
	private GraphDatabaseServiceFactory graphDatabaseServiceFactory;
	
	@Autowired
	private DataPipelineModelledLoaderSource dataPipelineModelledLoaderSource;
	
	@Value("${storage.object.service}")
	private String storageObjectService;
	
	@Value("${storage.object.local.baseUri}")
	private String storageLocalBaseUri;
	
	@Value("${storage.object.containers.modelled}")
	private String modelledDirectoryName;
	
	@Value("${storage.object.containers.loaded.graph}")
	private String loadedDirectoryName;
	
	@Value("${storage.object.patterns.fileNameIdsSeparator}")
	private String filenameIdsSeparator;
	
	@Value("${storage.graph.service}")
	private String storageGraphService;
	
	private OntologyMessage ontologyMessage;
	private ObjectStorageService objectStorageService;
	private GraphDatabaseService graphDatabaseService;
	private String readObjectUri;
	private String writeDirectoryUri;
	private String downloadedFileUri;
	
	/**
	 * Run the Ontology Graph Loading service end-to-end pipeline
	 */
	
	public void run(OntologyMessage ontologyMessage) {
		
		LOGGER.info("Ontology Graph Loading Service started.");
		this.ontologyMessage = ontologyMessage;
		
		try {
			
			// 1. Environment setup
			setup();
			
			// 2. Download the modelled ontology from persistent storage
			download();
			
			// 3. Load the ontology into a graph
			load();
			
			// 4. Copy the modelled ontology to the loaded directory 
			// in persistent storage 
			copy();
			
			// 5. Publish a message to the shared messaging system
			publish();
			
			// 6. Cleanup resources
			cleanup();
			
		} catch (Exception e) {
			LOGGER.error("Ontology Graph Loading Service "
					+ "encountered an error.", e);
		}
		
		LOGGER.info("Ontology Graph Loading Service finished.");
		
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
		
		// 2. Define and create (if required) the relevant 
		// target loaded directory
		switch ( objectStorageServiceType ) {
			
			case LOCAL:
				
				// Create (if required) the local target loaded directory
				readObjectUri = storageLocalBaseUri 
					+ File.separator + modelledDirectoryName 
					+ File.separator + ontologyMessage.getJsonProcessedFilename();
				writeDirectoryUri = storageLocalBaseUri + 
					File.separator + loadedDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
				break;
				
			default:
				
				// Create (if required) the Azure Storage container 
				// or AWS S3 bucket
				readObjectUri = modelledDirectoryName + "/" 
						+ ontologyMessage.getJsonProcessedFilename();
				writeDirectoryUri = loadedDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
			
		}
		
		// 3. Select the relevant graph database service
		GraphDatabaseServiceType graphDatabaseServiceType = 
				GraphDatabaseServiceType.valueOfLabel(
						storageGraphService.toUpperCase());
		graphDatabaseService = graphDatabaseServiceFactory
				.getGraphDatabaseService(graphDatabaseServiceType);
		LOGGER.debug("Using the {} graph database service.", 
				graphDatabaseServiceType);
		
	}
	
	/**
	 * Download the modelled ontology from persistent storage
	 * to a temporary file in local storage
	 * @throws IOException
	 */
	
	private void download() throws IOException {
		
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Started downloading the modelled resource.");
		downloadedFileUri = objectStorageService.downloadObject(readObjectUri, 
				filenameIdsSeparator + ontologyMessage.getJsonProcessedFilename());
		LOGGER.debug("Downloaded modelled resource to '{}'.", 
				downloadedFileUri);
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Finished downloading the modelled resource.");
		
	}
	
	/**
	 * Load the modelled ontology into the relevant graph database
	 * @throws IOException
	 */
	
	private void load() throws IOException {
		
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Started loading the modelled resource into "
				+ "the graph database.");
		
		// TODO pending
		
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Finished loading the modelled resource into "
				+ "the graph database.");
		
	}
	
	/**
	 * Copy the modelled ontology to the loaded directory 
	 * in persistent storage 
	 * @throws IOException
	 */
	
	private void copy() throws IOException {
		
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Started the persistence of the loaded resource.");
		String targetFilepath = writeDirectoryUri + "/" + 
				ontologyMessage.getJsonProcessedFilename();
		objectStorageService.uploadObject(
				downloadedFileUri, 
				targetFilepath);
		LOGGER.debug("Successfully persisted loaded ontology "
				+ "resource to '{}'.", targetFilepath);
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Finished the persistence of the loaded resource.");
		
	}
	
	/**
	 * Publish a message to the shared messaging system
	 */
	
	private void publish() {
		
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Started publishing message.");
		dataPipelineModelledLoaderSource.modelledLoadedPublicationChannel()
			.send(MessageBuilder.withPayload(ontologyMessage).build());
		LOGGER.info("Ontology Graph Loading Service - "
				+ "Finished publishing message.");
		
	}
	
	/**
	 * Cleanup any open resources
	 * @throws Exception 
	 */
	
	private void cleanup() throws Exception {
		
		// Close any storage service clients
		objectStorageService.cleanup();
		
		// Close any graph service clients
		graphDatabaseService.cleanup();
		
	}

}
