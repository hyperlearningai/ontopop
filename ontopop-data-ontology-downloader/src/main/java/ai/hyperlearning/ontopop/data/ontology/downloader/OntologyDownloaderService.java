package ai.hyperlearning.ontopop.data.ontology.downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.data.jpa.repositories.WebhookEventRepository;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Downloader Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class OntologyDownloaderService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyDownloaderService.class);
    
    @Autowired
    private WebhookEventRepository webhookEventRepository;
    
    @Autowired
    private ObjectStorageServiceFactory objectStorageServiceFactory;
    
    @Value("${storage.object.service}")
    private String storageObjectService;

    @Value("${storage.object.local.baseUri}")
    private String storageLocalBaseUri;

    @Value("${storage.object.containers.ingested}")
    private String ingestedDirectoryName;
    
    private ObjectStorageServiceType objectStorageServiceType;
    private ObjectStorageService objectStorageService;
    private Map<String, String> downloadedFiles = new HashMap<>();
    
    @PostConstruct
    private void postConstruct() {
        
        // Instantiate the storage service
        objectStorageServiceType = ObjectStorageServiceType
                .valueOfLabel(storageObjectService.toUpperCase());
        objectStorageService = objectStorageServiceFactory
                .getObjectStorageService(objectStorageServiceType);
        LOGGER.debug("Using the {} object storage service.",
                objectStorageServiceType);
        
    }
    
    /**
     * Identify and return the latest webhook event object
     * for a given ontology ID.
     * @param ontologyId
     * @return
     */
    
    public WebhookEvent getLatestWebhookEvent(int ontologyId) {
        List<WebhookEvent> webhookEvents = webhookEventRepository
                .findByOntologyId(ontologyId);
        if ( !webhookEvents.isEmpty() ) {
            Optional<WebhookEvent> webhookEvent = webhookEvents.stream()
                    .collect(Collectors.maxBy(
                            Comparator.comparingLong(
                                    WebhookEvent::getId)));
            if (webhookEvent.isPresent())
                return webhookEvent.get();
        }
        return null;
    }

    /**
     * Retrieve an OWL file from the object storage ingested container
     * given a webhook event object, and return the locally downloaded
     * absolute file path.
     * @param webhookEvent
     * @return
     * @throws IOException
     */
    
    public String retrieveOwlFile(WebhookEvent webhookEvent) 
            throws IOException {
        
        String key = webhookEvent.getOntology().getId() 
                + "_" + webhookEvent.getId();
        String processedFilename = webhookEvent.getOntology()
                .generateFilenameForPersistence(webhookEvent.getId());
        String readObjectUri = (objectStorageServiceType.equals(
                ObjectStorageServiceType.LOCAL)) ? 
                        storageLocalBaseUri + File.separator 
                        + ingestedDirectoryName + File.separator 
                        + processedFilename : 
                            ingestedDirectoryName + "/" + processedFilename;
        
        // Check whether the OWL file has been downloaded recently.
        // If so, and if the file still exists, then return the path to it
        if ( downloadedFiles != null ) {
            if ( downloadedFiles.containsKey(key) ) {
                String previouslyDownloadedUri = downloadedFiles.get(key);
                Path path = Paths.get(previouslyDownloadedUri);
                if ( Files.exists(path) )
                    return previouslyDownloadedUri;
                else
                    downloadedFiles.remove(key);
            }
        } else {
            downloadedFiles = new HashMap<>();
        } 
        
        // If not, download the OWL file from object storage
        String downloadedUri = objectStorageService
                .downloadObject(readObjectUri, processedFilename);
        downloadedFiles.put(key, downloadedUri);
        return downloadedUri;
        
    }
    
}
