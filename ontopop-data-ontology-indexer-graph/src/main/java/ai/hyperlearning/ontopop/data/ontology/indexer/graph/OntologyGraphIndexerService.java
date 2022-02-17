package ai.hyperlearning.ontopop.data.ontology.indexer.graph;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineProcessingException;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineIndexerGraphSource;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyVertex;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.search.SearchService;
import ai.hyperlearning.ontopop.search.SearchServiceFactory;
import ai.hyperlearning.ontopop.search.SearchServiceType;
import ai.hyperlearning.ontopop.search.model.SimpleIndexVertex;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Graph Indexing Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@EnableBinding(DataPipelineIndexerGraphSource.class)
public class OntologyGraphIndexerService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphIndexerService.class);

    @Autowired
    private ObjectStorageServiceFactory objectStorageServiceFactory;

    @Autowired
    private SearchServiceFactory searchServiceFactory;

    @Autowired
    private DataPipelineIndexerGraphSource dataPipelineIndexerGraphSource;

    @Value("${storage.object.service}")
    private String storageObjectService;

    @Value("${storage.object.local.baseUri}")
    private String storageLocalBaseUri;

    @Value("${storage.object.containers.modelled}")
    private String modelledDirectoryName;

    @Value("${storage.object.containers.indexed}")
    private String indexedDirectoryName;

    @Value("${storage.search.service}")
    private String storageSearchService;

    @Value("${storage.search.indexNamePrefix}")
    private String searchIndexNamePrefix;
    
    @Value("${storage.search.elasticsearch.shards}")
    private Integer searchNumberOfShards;
    
    @Value("${storage.search.elasticsearch.replicas}")
    private Integer searchNumberOfReplicas;

    private OntologyMessage ontologyMessage;
    private ObjectStorageService objectStorageService;
    private SearchService searchService;
    private String readObjectUri;
    private String writeDirectoryUri;
    private String downloadedFileUri;
    private String indexName;

    /**
     * Run the Ontology Indexing service end-to-end pipeline
     */

    public void run(OntologyMessage ontologyMessage) 
            throws OntologyDataPipelineProcessingException {

        LOGGER.info("Ontology Indexing Service started.");
        this.ontologyMessage = ontologyMessage;

        try {

            // 1. Environment setup
            setup();

            // 2. Download the modelled ontology from persistent storage
            download();

            // 3. Index the ontology into a search index
            index();

            // 4. Copy the modelled ontology to the indexed directory
            // in persistent storage
            copy();

            // 5. Publish a message to the shared messaging system
            publish();

            // 6. Cleanup resources
            cleanup();

        } catch (Exception e) {
            LOGGER.error("Ontology Graph Indexing Service encountered "
                    + "an error.", e);
            throw new OntologyDataPipelineProcessingException(
                    "Ontology Graph Indexing Service encountered "
                    + "an error: " + e);
        }

        LOGGER.info("Ontology Indexing Service finished.");

    }

    /**
     * Instantiate the relevant object storage service
     * 
     * @throws IOException
     */

    private void setup() throws IOException {

        // 1. Select the relevant persistent storage service and
        // create the target directory if it does not already exist
        ObjectStorageServiceType objectStorageServiceType =
                ObjectStorageServiceType
                        .valueOfLabel(storageObjectService.toUpperCase());
        objectStorageService = objectStorageServiceFactory
                .getObjectStorageService(objectStorageServiceType);
        LOGGER.debug("Using the {} object storage service.",
                objectStorageServiceType);

        // 2. Define and create (if required) the relevant
        // target indexed directory
        switch (objectStorageServiceType) {

            case LOCAL:

                // Create (if required) the local target indexed directory
                readObjectUri = storageLocalBaseUri + File.separator
                        + modelledDirectoryName + File.separator
                        + ontologyMessage.getJsonProcessedFilename();
                writeDirectoryUri = storageLocalBaseUri + File.separator
                        + indexedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);
                break;

            default:

                // Create (if required) the Azure Storage container
                // or AWS S3 bucket
                readObjectUri = modelledDirectoryName + "/"
                        + ontologyMessage.getJsonProcessedFilename();
                writeDirectoryUri = indexedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);

        }

        // 3. Select the relevant search service
        SearchServiceType searchServiceType = SearchServiceType
                .valueOfLabel(storageSearchService.toUpperCase());
        searchService =
                searchServiceFactory.getSearchService(searchServiceType);
        LOGGER.debug("Using the {} search service.", searchServiceType);

        // 4. Create the search index if required
        indexName = searchIndexNamePrefix + ontologyMessage.getOntologyId();
        LOGGER.debug("Creating index: {}", indexName);
        if ( searchServiceType.equals(SearchServiceType.ELASTICSEARCH) )
            searchService.createIndex(indexName, 
                    searchNumberOfShards, searchNumberOfReplicas);
        else
            searchService.createIndex(indexName);

    }

    /**
     * Download the modelled ontology from persistent storage to a temporary
     * file in local storage
     * 
     * @throws IOException
     */

    private void download() throws IOException {

        LOGGER.info("Ontology Indexing Service - "
                + "Started downloading the modelled resource.");
        downloadedFileUri = objectStorageService.downloadObject(readObjectUri,
                "_" + ontologyMessage.getJsonProcessedFilename());
        LOGGER.debug("Downloaded modelled resource to '{}'.",
                downloadedFileUri);
        LOGGER.info("Ontology Indexing Service - "
                + "Finished downloading the modelled resource.");

    }

    /**
     * Index the modelled ontology into the relevant search index
     * 
     * @throws IOException
     */

    private void index() throws IOException {

        LOGGER.info("Ontology Indexing Service - "
                + "Started indexing the modelled resource into "
                + "the search index.");

        // Deserialize the Simple Ontology Property Graph object from JSON
        ObjectMapper mapper = new ObjectMapper();
        SimpleOntologyPropertyGraph simpleOntologyPropertyGraph =
                mapper.readValue(new File(downloadedFileUri),
                        SimpleOntologyPropertyGraph.class);

        // Delete all documents in this index
        LOGGER.debug("Deleting all documents in index: {}", indexName);
        searchService.deleteAllDocuments(indexName);

        // Generate a set of SimpleIndexVertex objects
        Set<SimpleIndexVertex> vertices = new LinkedHashSet<>();
        for (var entry : simpleOntologyPropertyGraph.getVertices().entrySet()) {
            SimpleOntologyVertex vertex = entry.getValue();
            vertex.preparePropertiesForLoading();
            SimpleIndexVertex simpleIndexVertex = new SimpleIndexVertex(
                    vertex.getVertexId(), SimpleOntologyVertex.LABEL, 
                    vertex.getProperties());
            vertices.add(simpleIndexVertex);
        }

        // Bulk index the vertices/classes
        searchService.indexDocuments(indexName, vertices);
        LOGGER.debug("Indexed {} vertices.", vertices.size());

        LOGGER.info("Ontology Indexing Service - "
                + "Finished indexing the modelled resource into "
                + "the search index.");

    }

    /**
     * Copy the modelled ontology to the indexed directory in persistent storage
     * 
     * @throws IOException
     */

    private void copy() throws IOException {

        LOGGER.info("Ontology Indexing Service - "
                + "Started the persistence of the loaded resource.");
        String targetFilepath = writeDirectoryUri + "/"
                + ontologyMessage.getJsonProcessedFilename();
        objectStorageService.uploadObject(downloadedFileUri, targetFilepath);
        LOGGER.debug(
                "Successfully persisted loaded ontology " + "resource to '{}'.",
                targetFilepath);
        LOGGER.info("Ontology Indexing Service - "
                + "Finished the persistence of the loaded resource.");

    }

    /**
     * Publish a message to the shared messaging system
     * 
     * @throws JsonProcessingException
     */

    private void publish() throws JsonProcessingException {

        LOGGER.info(
                "Ontology Indexing Service - " + "Started publishing message.");
        ObjectMapper mapper = new ObjectMapper();
        dataPipelineIndexerGraphSource.graphIndexedPublicationChannel()
                .send(MessageBuilder
                        .withPayload(mapper.writeValueAsString(ontologyMessage))
                        .build());
        LOGGER.info("Ontology Indexing Service - "
                + "Finished publishing message.");

    }

    /**
     * Cleanup any open resources
     * 
     * @throws Exception
     */

    private void cleanup() throws Exception {

        // Close any storage service clients
        objectStorageService.cleanup();

        // Close any search service clients
        searchService.cleanup();

    }

}
