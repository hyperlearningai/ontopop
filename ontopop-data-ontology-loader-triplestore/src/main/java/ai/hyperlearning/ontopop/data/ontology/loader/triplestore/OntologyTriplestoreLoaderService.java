package ai.hyperlearning.ontopop.data.ontology.loader.triplestore;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineValidatedLoaderSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;
import ai.hyperlearning.ontopop.triplestore.TriplestoreService;
import ai.hyperlearning.ontopop.triplestore.TriplestoreServiceFactory;
import ai.hyperlearning.ontopop.triplestore.TriplestoreServiceType;

/**
 * Ontology Triplestore Loading Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@EnableBinding(DataPipelineValidatedLoaderSource.class)
public class OntologyTriplestoreLoaderService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyTriplestoreLoaderService.class);

    @Autowired
    private ObjectStorageServiceFactory objectStorageServiceFactory;

    @Autowired
    private TriplestoreServiceFactory triplestoreServiceFactory;

    @Autowired
    private DataPipelineValidatedLoaderSource dataPipelineValidatedLoaderSource;

    @Value("${storage.object.service}")
    private String storageObjectService;

    @Value("${storage.object.local.baseUri}")
    private String storageLocalBaseUri;

    @Value("${storage.object.containers.validated}")
    private String validatedDirectoryName;

    @Value("${storage.object.containers.loaded.triplestore}")
    private String loadedDirectoryName;

    @Value("${storage.object.patterns.fileNameIdsSeparator}")
    private String filenameIdsSeparator;

    @Value("${storage.triplestore.service}")
    private String storageTriplestoreService;

    private OntologyMessage ontologyMessage;
    private ObjectStorageService objectStorageService;
    private TriplestoreService triplestoreService;
    private String readObjectUri;
    private String writeDirectoryUri;
    private String downloadedFileUri;

    /**
     * Run the Ontology Triplestore Loading service end-to-end pipeline
     */

    public void run(OntologyMessage ontologyMessage) {

        LOGGER.info("Ontology Triplestore Loading Service started.");
        this.ontologyMessage = ontologyMessage;

        try {

            // 1. Environment setup
            setup();

            // 2. Download the validated ontology from persistent storage
            download();

            // 3. Load the ontology into a triplestore
            load();

            // 4. Copy the validated ontology to the loaded directory
            // in persistent storage
            copy();

            // 5. Publish a message to the shared messaging system
            publish();

            // 6. Cleanup resources
            cleanup();

        } catch (Exception e) {
            LOGGER.error("Ontology Triplestore Loading Service "
                    + "encountered an error.", e);
        }

        LOGGER.info("Ontology Triplestore Loading Service finished.");

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
        // target loaded directory
        switch (objectStorageServiceType) {

            case LOCAL:

                // Create (if required) the local target loaded directory
                readObjectUri = storageLocalBaseUri + File.separator
                        + validatedDirectoryName + File.separator
                        + ontologyMessage.getProcessedFilename();
                writeDirectoryUri = storageLocalBaseUri + File.separator
                        + loadedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);
                break;

            default:

                // Create (if required) the Azure Storage container
                // or AWS S3 bucket
                readObjectUri = validatedDirectoryName + "/"
                        + ontologyMessage.getProcessedFilename();
                writeDirectoryUri = loadedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);

        }

        // 3. Select the relevant triplestore service
        TriplestoreServiceType triplestoreServiceType = TriplestoreServiceType
                .valueOfLabel(storageTriplestoreService.toUpperCase());
        triplestoreService = triplestoreServiceFactory
                .getTriplestoreService(triplestoreServiceType);
        LOGGER.debug("Using the {} triplestore service.",
                triplestoreServiceType);

    }

    /**
     * Download the validated ontology from persistent storage to a temporary
     * file in local storage
     * 
     * @throws IOException
     */

    private void download() throws IOException {

        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Started downloading the validated resource.");
        downloadedFileUri = objectStorageService.downloadObject(readObjectUri,
                filenameIdsSeparator + ontologyMessage.getProcessedFilename());
        LOGGER.debug("Downloaded validated resource to '{}'.",
                downloadedFileUri);
        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Finished downloading the validated resource.");

    }

    /**
     * Load the validated ontology into the relevant triplestore
     * 
     * @throws IOException
     */

    private void load() throws IOException {

        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Started loading the validated resource into "
                + "the triplestore.");
        triplestoreService.loadOntologyOwlRdfXml(
                ontologyMessage.getOntologyId(), downloadedFileUri);
        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Finished loading the validated resource into "
                + "the triplestore.");

    }

    /**
     * Copy the validated ontology to the loaded directory in persistent storage
     * 
     * @throws IOException
     */

    private void copy() throws IOException {

        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Started the persistence of the loaded resource.");
        String targetFilepath = writeDirectoryUri + "/"
                + ontologyMessage.getProcessedFilename();
        objectStorageService.uploadObject(downloadedFileUri, targetFilepath);
        LOGGER.debug(
                "Successfully persisted loaded ontology " + "resource to '{}'.",
                targetFilepath);
        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Finished the persistence of the loaded resource.");

    }

    /**
     * Publish a message to the shared messaging system
     * 
     * @throws JsonProcessingException
     */

    private void publish() throws JsonProcessingException {

        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Started publishing message.");
        ObjectMapper mapper = new ObjectMapper();
        dataPipelineValidatedLoaderSource.validatedLoadedPublicationChannel()
                .send(MessageBuilder
                        .withPayload(mapper.writeValueAsString(ontologyMessage))
                        .build());
        LOGGER.info("Ontology Triplestore Loading Service - "
                + "Finished publishing message.");

    }

    /**
     * Cleanup any open resources
     */

    private void cleanup() throws IOException {

        // Close any storage service clients
        objectStorageService.cleanup();

        // Close any triplestore service clients
        triplestoreService.cleanup();

    }

}
