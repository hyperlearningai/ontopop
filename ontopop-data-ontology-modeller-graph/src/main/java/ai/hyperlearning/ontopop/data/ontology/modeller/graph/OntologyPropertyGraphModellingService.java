package ai.hyperlearning.ontopop.data.ontology.modeller.graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
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

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineModellerSource;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.rdf.DCMI;
import ai.hyperlearning.ontopop.rdf.RDFSchema;
import ai.hyperlearning.ontopop.rdf.SKOSVocabulary;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Property Graph Modelling Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@EnableBinding(DataPipelineModellerSource.class)
public class OntologyPropertyGraphModellingService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyPropertyGraphModellingService.class);
	
	@Autowired
	private ObjectStorageServiceFactory objectStorageServiceFactory;
	
	@Autowired
	private DataPipelineModellerSource dataPipelineModellerSource;
	
	@Value("${storage.object.service}")
	private String storageObjectService;
	
	@Value("${storage.object.local.baseUri}")
	private String storageLocalBaseUri;
	
	@Value("${storage.object.containers.parsed}")
	private String parsedDirectoryName;
	
	@Value("${storage.object.containers.modelled}")
	private String modelledDirectoryName;
	
	@Value("${storage.object.patterns.fileNameIdsSeparator}")
	private String filenameIdsSeparator;
	
	private OntologyMessage ontologyMessage;
	private ObjectStorageService objectStorageService;
	private String readObjectUri;
	private String writeDirectoryUri;
	private String downloadedFileUri;
	private SimpleOntology simpleOntology;
	private SimpleOntologyPropertyGraph simpleOntologyPropertyGraph;
	
	/**
	 * Run the Ontology Parsing service end-to-end pipeline
	 */
	
	public void run(OntologyMessage ontologyMessage) {
		
		LOGGER.info("Ontology Property Graph Modelling Service started.");
		this.ontologyMessage = ontologyMessage;
		
		try {
			
			// 1. Environment setup
			setup();
			
			// 2. Download the parsed ontology from persistent storage
			download();
			
			// 3. Model the ontology as a directed property graph
			model();
			
			// 4. Persist the modelled ontology
			persist();
			
			// 5. Publish a message to the shared messaging system
			publish();
			
			// 6. Cleanup resources
			cleanup();
			
		} catch (Exception e) {
			LOGGER.error("Ontology Property Graph Modelling Service "
					+ "encountered an error.", e);
		}
		
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
				
				// Create (if required) the local target modelled directory
				readObjectUri = storageLocalBaseUri 
					+ File.separator + parsedDirectoryName 
					+ File.separator + ontologyMessage.getJsonProcessedFilename();
				writeDirectoryUri = storageLocalBaseUri + 
					File.separator + modelledDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
				break;
				
			default:
				
				// Create (if required) the Azure Storage container 
				// or AWS S3 bucket
				readObjectUri = parsedDirectoryName + "/" 
						+ ontologyMessage.getJsonProcessedFilename();
				writeDirectoryUri = modelledDirectoryName;
				if ( !objectStorageService.doesContainerExist(writeDirectoryUri) )
					objectStorageService.createContainer(writeDirectoryUri);
			
		}
		
	}
	
	/**
	 * Download the parsed ontology from persistent storage
	 * to a temporary file in local storage
	 * @throws IOException
	 */
	
	private void download() throws IOException {
		
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Started downloading the parsed resource.");
		downloadedFileUri = objectStorageService.downloadObject(readObjectUri, 
				filenameIdsSeparator + ontologyMessage.getJsonProcessedFilename());
		LOGGER.debug("Downloaded parsed resource to '{}'.", 
				downloadedFileUri);
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Finished downloading the parsed resource.");
		
	}
	
	/**
	 * Model the ontology as a directed property graph
	 * @throws IOException
	 * @throws OWLOntologyCreationException 
	 */
	
	private void model() throws IOException, OWLOntologyCreationException {
		
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Started modelling the parsed resource.");
		
		// Deserialize the Simple Ontology object from JSON
		ObjectMapper mapper = new ObjectMapper();
		simpleOntology = mapper.readValue(
				new File(downloadedFileUri), SimpleOntology.class);
		
		// Load the SKOS Vocabulary and parse its annotation properties
		OWLOntology skos = SKOSVocabulary.loadSKOSRDF();
		Map<String, SimpleAnnotationProperty> skosAnnotationProperties = 
				SKOSVocabulary.parseAnnotationProperties(skos);
		
		// Load the RDF Schema and parse its annotation properties
		OWLOntology rdf = RDFSchema.loadRdfSchema();
		Map<String, SimpleAnnotationProperty> rdfSchemaAnnotationProperties = 
				RDFSchema.parseAnnotationProperties(rdf);
		
		// Load the DCMI RDF Schema and parse its annotation properties
		Map<String, SimpleAnnotationProperty> dcmiSchemaAnnotationProperties = 
				DCMI.parseAnnotationProperties();
		
		// Aggregate the standard schema annotation properties
		Map<String, SimpleAnnotationProperty> standardSchemaAnnotationProperties = 
				new LinkedHashMap<>(skosAnnotationProperties);
		standardSchemaAnnotationProperties.putAll(rdfSchemaAnnotationProperties);
		standardSchemaAnnotationProperties.putAll(dcmiSchemaAnnotationProperties);
		
		// Transform the Simple Ontology object into a 
		// Simple Ontology Property Graph object
		simpleOntologyPropertyGraph = new SimpleOntologyPropertyGraph(
				ontologyMessage.getOntologyId(), 
				ontologyMessage.getWebhookEventId(), 
				simpleOntology, 
				standardSchemaAnnotationProperties);
		
		LOGGER.debug("Modelled ontology: '{}'.", simpleOntologyPropertyGraph);
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Finished modelling the parsed resource.");
		
	}
	
	/**
	 * Persist the modelled ontology
	 * @throws IOException
	 */
	
	private void persist() throws IOException {
		
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Started the persistence of the modelled resource.");
		
		// Serialize the Simple Ontology Property Graph object 
		// to a temporary file in the local file system
		String jsonFilename = ontologyMessage.getJsonProcessedFilename();
		Path temporaryFile = Files.createTempFile("", jsonFilename);
		File file = new File(temporaryFile.toAbsolutePath().toString());
		ObjectMapper mapper = new ObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(file, simpleOntologyPropertyGraph);
		
		// Upload the serialized JSON file to persistent object storage
		String targetFilepath = writeDirectoryUri + "/" + 
				ontologyMessage.getJsonProcessedFilename();
		objectStorageService.uploadObject(
				temporaryFile.toAbsolutePath().toString(), 
				targetFilepath);
		
		LOGGER.debug("Successfully persisted modelled ontology "
				+ "resource to '{}'.", targetFilepath);
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Finished the persistence of the modelled resource.");
		
	}
	
	/**
	 * Publish a message to the shared messaging system
	 */
	
	private void publish() {
		
		LOGGER.info("Ontology Property Graph Modelling Service - "
				+ "Started publishing message.");
		dataPipelineModellerSource.modelledPublicationChannel()
			.send(MessageBuilder.withPayload(ontologyMessage).build());
		LOGGER.info("Ontology Property Graph Modelling Service - "
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
