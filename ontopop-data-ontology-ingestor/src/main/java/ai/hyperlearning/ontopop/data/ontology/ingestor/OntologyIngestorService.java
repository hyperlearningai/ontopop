package ai.hyperlearning.ontopop.data.ontology.ingestor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.WebhookEventRepository;
import ai.hyperlearning.ontopop.git.GitService;
import ai.hyperlearning.ontopop.git.GitServiceFactory;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineIngestorSource;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;
import ai.hyperlearning.ontopop.security.secrets.managers.OntologySecretDataManager;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Ingestion Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Service
@Transactional
@EnableBinding(DataPipelineIngestorSource.class)
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
    private ObjectStorageServiceFactory objectStorageServiceFactory;

    @Autowired
    private DataPipelineIngestorSource dataPipelineIngestorSource;

    @Autowired
    private OntologySecretDataManager ontologySecretDataManager;

    @Value("${storage.object.service}")
    private String storageObjectService;

    @Value("${storage.object.local.baseUri}")
    private String storageLocalBaseUri;

    @Value("${storage.object.containers.ingested}")
    private String ingestedDirectoryName;

    private Map<String, String> headers;
    private String payload;
    private GitService gitService;
    private Set<WebhookEvent> webhookEvents = new HashSet<>();
    private ObjectStorageService objectStorageService;
    private String writeDirectoryUri;

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

            // 2. Parse the webhook event payload into individual
            // WebhookEvent objects
            parse();

            // 3. Save the relevant modified resources to persistent storage
            save();

            // 4. Publish messages for each valid webhook event
            publish();

            // 5. Cleanup resources
            cleanup();

        } catch (Exception e) {
            LOGGER.error("Ontology Ingestion Service encountered an error.", e);
        }

        LOGGER.info("Ontology Ingestion Service finished.");

    }

    /**
     * Instantiate the relevant Git and object storage services
     * 
     * @throws IOException
     */

    private void setup() throws IOException {

        // 1. Select the relevant Git service based on the HTTP headers
        // of the webhook event request
        gitService = gitServiceFactory.getGitService(headers);

        // 2. Select the relevant persistent storage service and
        // create the target directory if it does not already exist
        ObjectStorageServiceType objectStorageServiceType =
                ObjectStorageServiceType
                        .valueOfLabel(storageObjectService.toUpperCase());
        objectStorageService = objectStorageServiceFactory
                .getObjectStorageService(objectStorageServiceType);
        LOGGER.debug("Using the {} object storage service.",
                objectStorageServiceType);

        // 3. Define and create (if required) the relevant
        // target ingestion directory
        switch (objectStorageServiceType) {

            case LOCAL:

                // Create (if required) the local target ingestion directory
                writeDirectoryUri = storageLocalBaseUri + File.separator
                        + ingestedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);
                break;

            default:

                // Create (if required) the Azure Storage container
                // or AWS S3 bucket
                writeDirectoryUri = ingestedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);

        }

    }

    /**
     * A single webhook event may describe multiple resources belonging to the
     * same repository that have been modified. In this method, we create
     * separate WebhookEvent objects representing each separate but relevant
     * resource path. We know if a resource path is relevant if there exists an
     * Ontology object that defines that resource path to watch.
     * 
     * @throws Exception
     */

    private void parse() throws Exception {

        LOGGER.info("Ontology Ingestion Service - "
                + "Started parsing of webhook events.");

        // 1. Parse the webhook payload
        WebhookEvent webhookEvent =
                gitService.parseWebhookPayload(headers, payload, null);
        LOGGER.debug("Parsed webhook event object without resource path: {} ",
                webhookEvent);

        // 2. Get all ontology objects that match the webhook event request
        // i.e. the payload may describe more than one resource path
        // in the same repository that have been modified
        Set<Ontology> ontoglogies = new HashSet<>();
        for (String modifiedResourcePath : webhookEvent
                .getCommitsModifiedResourcePaths()) {

            ontoglogies.addAll(ontologyRepository.findByRepoUrlOwnerPathBranch(
                    webhookEvent.getRepoUrl(), webhookEvent.getRepoOwner(),
                    modifiedResourcePath, webhookEvent.getRepoBranch()));

        }

        // 3. For each ontology, validate the webhook payload
        LOGGER.debug(
                "Found {} ontologies matching the " + "webhook event request.",
                ontoglogies.size());
        webhookEvents.clear();
        int ontologyCounter = 0;
        for (Ontology ontology : ontoglogies) {
            ontologyCounter++;

            // 3.1. Get the ontology secret data
            OntologySecretData ontologySecretData =
                    ontologySecretDataManager.get(ontology.getId());
            ontology.setRepoToken(ontologySecretData.getRepoToken());
            ontology.setRepoWebhookSecret(
                    ontologySecretData.getRepoWebhookSecret());

            // 3.2. Recreate the webhook payload object
            // with the resource path
            WebhookEvent currentWebhookEvent = gitService.parseWebhookPayload(
                    headers, payload, ontology.getRepoResourcePath());
            currentWebhookEvent.setOntology(ontology);
            LOGGER.debug(
                    "Parsed webhook event object with resource path "
                            + "({} of {}): {}",
                    ontologyCounter, ontoglogies.size(), currentWebhookEvent);

            // 3.3. Validate the webhook payload
            boolean validWebhookPayload = gitService.isValidWebhookPayload(
                    headers, payload, ontology.getRepoResourcePath(),
                    ontology.getRepoWebhookSecret(), ontology.getRepoName(),
                    ontology.getRepoOwner(), ontology.getRepoBranch());

            // 3.4. If valid, then persist the webhook event to storage
            LOGGER.debug(
                    "Validity of parsed webhook event object "
                            + "with resource path " + "({} of {}): {}",
                    ontologyCounter, ontoglogies.size(), validWebhookPayload);
            if (validWebhookPayload) {
                webhookEvents
                        .add(webhookEventRepository.save(currentWebhookEvent));
                LOGGER.debug(
                        "Successfully parsed and persisted "
                                + "webhook event object: {}",
                        currentWebhookEvent);
            }

        }

        LOGGER.info("Ontology Ingestion Service - "
                + "Finished parsing of webhook events.");

    }

    /**
     * Download the modified resources to the local file system in a temporary
     * location, and then upload these resources to the relevant persistent
     * storage service.
     * 
     * @throws IOException
     */

    private void save() throws IOException {

        LOGGER.info("Ontology Ingestion Service - "
                + "Started downloading modified resources.");

        // 1. Download and write the modified resources to persistent storage
        for (WebhookEvent webhookEvent : webhookEvents) {

            // 1.1. Get the resource as a string encapsulated within a
            // HTTP response entity
            ResponseEntity<String> response =
                    webhookEvent.getOntology().isRepoPrivate()
                            ? gitService.getFile(
                                    webhookEvent.getOntology().getRepoToken(),
                                    webhookEvent.getRepoOwner(),
                                    webhookEvent.getRepoName(),
                                    webhookEvent.getOntology()
                                            .getRepoResourcePath(),
                                    webhookEvent.getRepoBranch())
                            : gitService.getFile(webhookEvent.getRepoOwner(),
                                    webhookEvent.getRepoName(),
                                    webhookEvent.getOntology()
                                            .getRepoResourcePath(),
                                    webhookEvent.getRepoBranch());

            // 1.2. Check the response is OK
            if (response.getStatusCode().equals(HttpStatus.OK)
                    && !Strings.isNullOrEmpty(response.getBody())) {

                // Write the string contents to a temporary file
                // in the local file system
                Path temporaryFile = Files.createTempFile("",
                        "_" + webhookEvent.getOntology()
                                .generateFilenameForPersistence(
                                        webhookEvent.getId()));
                Files.write(temporaryFile,
                        response.getBody().getBytes(StandardCharsets.UTF_8));
                LOGGER.debug(
                        "Successfully downloaded ontology resource '{}' "
                                + "to a local temporary file at '{}'.",
                        webhookEvent.getOntology().getRepoUrl() + "/"
                                + webhookEvent.getOntology()
                                        .getRepoResourcePath(),
                        temporaryFile.toAbsolutePath().toString());

                // 1.3. Upload the file to the relevant persistent storage
                // service
                String temporaryFilename =
                        temporaryFile.getFileName().toString();
                String targetFilename = temporaryFilename
                        .substring(temporaryFilename.indexOf("_") + 1);
                String targetFilepath =
                        writeDirectoryUri + "/" + targetFilename;
                objectStorageService.uploadObject(
                        temporaryFile.toAbsolutePath().toString(),
                        targetFilepath);
                LOGGER.debug(
                        "Successfully persisted ontology "
                                + "resource '{}' to '{}'.",
                        webhookEvent.getOntology().getRepoUrl() + "/"
                                + webhookEvent.getOntology()
                                        .getRepoResourcePath(),
                        targetFilepath);

            }

        }

        LOGGER.info("Ontology Ingestion Service - "
                + "Finished downloading modified resources.");

    }

    /**
     * Publish messages to the shared messaging system for each valid webhook
     * event indicating successful ingestion of an updated ontology.
     * 
     * @throws JsonProcessingException
     * @throws IOException
     */

    private void publish() throws JsonProcessingException {

        LOGGER.info("Ontology Ingestion Service - "
                + "Started publishing messages.");

        // Iterate over each valid webhook event, create an OntologyMessage
        // object and publish it to the shared messaging system
        for (WebhookEvent webhookEvent : webhookEvents) {

            // Create an ontology message
            OntologyMessage ontologyMessage = new OntologyMessage();
            ontologyMessage.setOntologyId(webhookEvent.getOntology().getId());
            ontologyMessage.setWebhookEventId(webhookEvent.getId());
            ontologyMessage.setProcessedFilename(
                    webhookEvent.getOntology().generateFilenameForPersistence(
                            webhookEvent.getId()));

            // Publish the message to the shared messaging system
            ObjectMapper mapper = new ObjectMapper();
            dataPipelineIngestorSource.ingestedPublicationChannel()
                    .send(MessageBuilder
                            .withPayload(
                                    mapper.writeValueAsString(ontologyMessage))
                            .build());

        }

        LOGGER.info("Ontology Ingestion Service - "
                + "Finished publishing messages.");

    }

    /**
     * Cleanup any open resources
     */

    private void cleanup() throws IOException {

        // Close any storage service clients
        objectStorageService.cleanup();

    }

}
