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

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
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
    private GitWebhookRepository gitWebhookRepository;
    
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
     * Identify and return the latest Git webhook object
     * for a given ontology ID.
     * @param ontologyId
     * @return
     */
    
    public GitWebhook getLatestGitWebhook(int ontologyId) {
        List<GitWebhook> gitWebhooks = gitWebhookRepository
                .findByOntologyId(ontologyId);
        if ( !gitWebhooks.isEmpty() ) {
            Optional<GitWebhook> gitWebhook = gitWebhooks.stream()
                    .collect(Collectors.maxBy(
                            Comparator.comparingLong(
                                    GitWebhook::getId)));
            if (gitWebhook.isPresent())
                return gitWebhook.get();
        }
        return null;
    }

    /**
     * Retrieve an OWL file from the object storage ingested container
     * given a Git webhook object, and return the locally downloaded
     * absolute file path.
     * @param gitWebhook
     * @return
     * @throws IOException
     */
    
    public String retrieveOwlFile(GitWebhook gitWebhook) 
            throws IOException {
        
        String key = gitWebhook.getOntology().getId() 
                + "_" + gitWebhook.getId();
        String processedFilename = gitWebhook.getOntology()
                .generateFilenameForPersistence(gitWebhook.getId());
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
