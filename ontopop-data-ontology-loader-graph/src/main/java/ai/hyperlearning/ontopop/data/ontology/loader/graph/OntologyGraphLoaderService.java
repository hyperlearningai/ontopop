package ai.hyperlearning.ontopop.data.ontology.loader.graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceFactory;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceType;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphEdge;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphVertex;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineModelledLoaderSource;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyEdge;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyVertex;
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
	
	private static final String ONTOLOGY_ID_PROPERTY_KEY = "ontologyId";
	private static final String KEY_PROPERTY_KEY = "key";
	
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
		
		// 4. Open and create the graph database schema if required
		graphDatabaseService.openGraph();
		graphDatabaseService.createSchema();
		
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
		
		// Deserialize the Simple Ontology Property Graph object from JSON
		ObjectMapper mapper = new ObjectMapper();
		SimpleOntologyPropertyGraph simpleOntologyPropertyGraph = 
				mapper.readValue(new File(downloadedFileUri), 
						SimpleOntologyPropertyGraph.class);
		
		// Delete all existing vertices with this ontology ID
		graphDatabaseService.deleteVertices(
				ONTOLOGY_ID_PROPERTY_KEY, ontologyMessage.getOntologyId());
		graphDatabaseService.commit();
		
		// Generate a set of SimpleGraphVertex objects
		Set<SimpleGraphVertex> vertices = new LinkedHashSet<>();
		for (var entry : simpleOntologyPropertyGraph.getVertices().entrySet()) {
			SimpleOntologyVertex vertex = entry.getValue();
			vertex.preparePropertiesForLoading();
			vertices.add(new SimpleGraphVertex(
					vertex.getVertexId(), 
					SimpleOntologyVertex.LABEL, 
					vertex.getProperties()));	
		}
		
		// Bulk load the vertices/classes
		graphDatabaseService.addVertices(SimpleOntologyVertex.LABEL, vertices);
		graphDatabaseService.commit();
		LOGGER.debug("Loaded {} vertices.", vertices.size());
		
		// Delete all existing edges with this ontology ID
		graphDatabaseService.deleteEdges(
				ONTOLOGY_ID_PROPERTY_KEY, ontologyMessage.getOntologyId());
		graphDatabaseService.commit();
		
		// Generate a set of SimpleGraphEdge objects
		List<SimpleGraphEdge> edges = new ArrayList<>();
		for (SimpleOntologyEdge edge : simpleOntologyPropertyGraph.getEdges()) {
			edge.preparePropertiesForLoading();
			Vertex sourceVertex = graphDatabaseService.getVertex(
					SimpleOntologyVertex.LABEL, 
					KEY_PROPERTY_KEY, 
					edge.getSourceVertexKey());
			Vertex targetVertex = graphDatabaseService.getVertex(
					SimpleOntologyVertex.LABEL, 
					KEY_PROPERTY_KEY, 
					edge.getTargetVertexKey());
			if ( sourceVertex != null && targetVertex != null ) {
				edges.add(new SimpleGraphEdge(
						SimpleOntologyEdge.LABEL, 
						sourceVertex, 
						targetVertex, 
						edge.getProperties()));
			}
		}
		
		// Bulk load the edges/subClassOf relationships
		graphDatabaseService.addEdges(edges);
		graphDatabaseService.commit();
		LOGGER.debug("Loaded {} edges.", edges.size());
		
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
