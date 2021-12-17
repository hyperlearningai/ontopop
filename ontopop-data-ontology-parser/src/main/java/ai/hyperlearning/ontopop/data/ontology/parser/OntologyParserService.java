package ai.hyperlearning.ontopop.data.ontology.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineParserSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.owl.OWLAPI;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Parsing Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@EnableBinding(DataPipelineParserSource.class)
public class OntologyParserService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyParserService.class);
	
	@Autowired
	private ObjectStorageServiceFactory objectStorageServiceFactory;
	
	@Autowired
	private DataPipelineParserSource dataPipelineParserSource;
	
	@Value("${storage.object.service}")
	private String storageObjectService;
	
	@Value("${storage.object.local.baseUri}")
	private String storageLocalBaseUri;
	
	@Value("${storage.object.dirNames.validated}")
	private String validatedDirectoryName;
	
	@Value("${storage.object.dirNames.parsed}")
	private String parsedDirectoryName;
	
	@Value("${storage.object.patterns.fileNameIdsSeparator}")
	private String filenameIdsSeparator;
	
	private OntologyMessage ontologyMessage;
	private ObjectStorageService objectStorageService;
	private String readObjectUri;
	private String writeDirectoryUri;
	private String downloadedFileUri;
	private SimpleOntology simpleOntology;
	
	/**
	 * Run the Ontology Parsing service end-to-end pipeline
	 */
	
	public void run(OntologyMessage ontologyMessage) {
		
		LOGGER.info("Ontology Parsing Service started.");
		this.ontologyMessage = ontologyMessage;
		
		try {
			
			// 1. Environment setup
			setup();
			
			// 2. Download the validated ontology from persistent storage
			download();
			
			// 3. Parse the ontology into its constituent components
			parse();
			
			// 4. Persist the parsed ontology components
			persist();
			
			// 5. Publish a message to the shared messaging system
			publish();
			
			// 6. Cleanup resources
			cleanup();
			
		} catch (Exception e) {
			LOGGER.error("Ontology Parsing Service "
					+ "encountered an error.", e);
		}
		
		LOGGER.info("Ontology Parsing Service finished.");
		
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
		// target parsed directory
		switch ( objectStorageServiceType ) {
			
			case LOCAL:
				
				// Create (if required) the local target parsed directory
				readObjectUri = storageLocalBaseUri 
					+ File.separator + validatedDirectoryName 
					+ File.separator + ontologyMessage.getProcessedFilename();
				writeDirectoryUri = storageLocalBaseUri + 
					File.separator + parsedDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
				break;
				
			default:
				
				// Create (if required) the Azure Storage container 
				// or AWS S3 bucket
				readObjectUri = validatedDirectoryName + "/" 
						+ ontologyMessage.getProcessedFilename();
				writeDirectoryUri = parsedDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
			
		}
		
	}
	
	/**
	 * Download the validated ontology from persistent storage
	 * to a temporary file in local storage
	 * @throws IOException
	 */
	
	private void download() throws IOException {
		
		LOGGER.info("Ontology Parsing Service - "
				+ "Started downloading the validated resource.");
		downloadedFileUri = objectStorageService.downloadObject(readObjectUri, 
				filenameIdsSeparator + ontologyMessage.getProcessedFilename());
		LOGGER.debug("Downloaded validated resource to '{}'.", 
				downloadedFileUri);
		LOGGER.info("Ontology Parsing Service - "
				+ "Finished downloading the validated resource.");
		
	}
	
	/**
	 * Parse the ontology into its constituent components
	 * @throws IOException
	 * @throws OWLOntologyCreationException 
	 */
	
	private void parse() throws OWLOntologyCreationException {
		
		LOGGER.info("Ontology Parsing Service - "
				+ "Started parsing the validated resource.");
		
		// Load the OWL ontology
		OWLOntology ontology = OWLAPI.loadOntology(new File(downloadedFileUri));
		
		// Get all annotation properties
		Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap = 
				OWLAPI.parseAnnotationProperties(ontology);
		
		// Get all object properties
		Map<String, SimpleObjectProperty> simpleObjectPropertyMap = 
				OWLAPI.parseObjectProperties(ontology);
		
		// Get all classes
		Map<String, SimpleClass> simpleClassMap = 
				OWLAPI.parseClasses(ontology);
		
		// Create a Simple Ontology object as a container for the 
		// parsed ontological components
		simpleOntology = new SimpleOntology(
				ontologyMessage.getOntologyId(), 
				ontologyMessage.getWebhookEventId(), 
				simpleAnnotationPropertyMap, 
				simpleObjectPropertyMap, 
				simpleClassMap);
		
		LOGGER.debug("Parsed ontology: '{}'.", simpleOntology);
		LOGGER.info("Ontology Parsing Service - "
				+ "Finished parsing the validated resource.");
		
	}
	
	/**
	 * Persist the parsed ontology components
	 * @throws IOException
	 */
	
	private void persist() throws IOException {
		
		LOGGER.info("Ontology Parsing Service - "
				+ "Started the persistence of the parsed resource.");
		
		// Serialize the Simple Ontology object to a temporary file in 
		// the local file system
		String jsonFilename = ontologyMessage.getProcessedFilename() + ".json";
		Path temporaryFile = Files.createTempFile("", jsonFilename);
		File file = new File(temporaryFile.toAbsolutePath().toString());
		ObjectMapper mapper = new ObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT);;
		mapper.writeValue(file, simpleOntology);
		
		// Upload the serialized JSON file to persistent object storage
		String targetFilepath = writeDirectoryUri + "/" + 
				ontologyMessage.getProcessedFilename() + ".json";
		objectStorageService.uploadObject(
				temporaryFile.toAbsolutePath().toString(), 
				targetFilepath);
		
		LOGGER.debug("Successfully persisted parsed ontology "
				+ "resource to '{}'.", targetFilepath);
		LOGGER.info("Ontology Parsing Service - "
				+ "Finished the persistence of the parsed resource.");
		
	}
	
	/**
	 * Publish a message to the shared messaging system
	 */
	
	private void publish() {
		
		LOGGER.info("Ontology Parsing Service - "
				+ "Started publishing message.");
		dataPipelineParserSource.parsedPublicationChannel()
			.send(MessageBuilder.withPayload(ontologyMessage).build());
		LOGGER.info("Ontology Parsing Service - "
				+ "Finished publishing message.");
		
	}
	
	/**
	 * Cleanup any open resources
	 */
	
	private void cleanup() throws IOException {
		
		// Close any storage service clients
		objectStorageService.cleanup();
		
	}

}
