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
    
    @Value("${storage.object.containers.parsed}")
    private String parsedDirectoryName;
    
    @Value("${storage.object.containers.loaded.graph}")
    private String loadedDirectoryName;
    
    private ObjectStorageServiceType objectStorageServiceType;
    private ObjectStorageService objectStorageService;
    private Map<String, String> downloadedIngestedOwlFiles = new HashMap<>();
    private Map<String, String> downloadedParsedSimpleOntologyFiles = new HashMap<>();
    private Map<String, String> downloadedModelledPropetryGraphFiles = new HashMap<>();
    
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
    
    private String generateKey(GitWebhook gitWebhook) {
        return gitWebhook.getOntology().getId() + "_" + gitWebhook.getId();
    }
    
    private String generateProcessedFilename(GitWebhook gitWebhook) {
        return gitWebhook.getOntology()
                .generateFilenameForPersistence(gitWebhook.getId());
    }
    
    private String getReadObjectUri(String stageDirectoryName, 
            String processedFilename) {
        return (objectStorageServiceType.equals(
                ObjectStorageServiceType.LOCAL)) ? 
                        storageLocalBaseUri + File.separator 
                        + stageDirectoryName + File.separator 
                        + processedFilename : 
                            stageDirectoryName + "/" + processedFilename;
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
        
        String key = generateKey(gitWebhook);
        String processedFilename = generateProcessedFilename(gitWebhook);
        String readObjectUri = getReadObjectUri(ingestedDirectoryName, 
                processedFilename);
        
        // Check whether the OWL file has been downloaded recently.
        // If so, and if the file still exists, then return the path to it
        if ( downloadedIngestedOwlFiles != null ) {
            if ( downloadedIngestedOwlFiles.containsKey(key) ) {
                String previouslyDownloadedUri = 
                        downloadedIngestedOwlFiles.get(key);
                Path path = Paths.get(previouslyDownloadedUri);
                if ( Files.exists(path) )
                    return previouslyDownloadedUri;
                else
                    downloadedIngestedOwlFiles.remove(key);
            }
        } else {
            downloadedIngestedOwlFiles = new HashMap<>();
        } 
        
        // If not, download the OWL file from object storage
        String downloadedUri = objectStorageService
                .downloadObject(readObjectUri, processedFilename);
        downloadedIngestedOwlFiles.put(key, downloadedUri);
        return downloadedUri;
        
    }
    
    /**
     * Retrieve a parsed simple ontology file from the object storage 
     * parsed container given a Git webhook object, and return the locally 
     * downloaded absolute file path.
     * @param gitWebhook
     * @return
     * @throws IOException
     */
    
    public String retrieveParsedSimpleOntologyFile(GitWebhook gitWebhook) 
            throws IOException {
        
        String key = generateKey(gitWebhook);
        String processedFilename = generateProcessedFilename(gitWebhook) 
                + ".json";
        String readObjectUri = getReadObjectUri(parsedDirectoryName, 
                processedFilename);
        
        // Check whether the JSON file has been downloaded recently.
        // If so, and if the file still exists, then return the path to it
        if ( downloadedParsedSimpleOntologyFiles != null ) {
            if ( downloadedParsedSimpleOntologyFiles.containsKey(key) ) {
                String previouslyDownloadedUri = 
                        downloadedParsedSimpleOntologyFiles.get(key);
                Path path = Paths.get(previouslyDownloadedUri);
                if ( Files.exists(path) )
                    return previouslyDownloadedUri;
                else
                    downloadedParsedSimpleOntologyFiles.remove(key);
            }
        } else {
            downloadedParsedSimpleOntologyFiles = new HashMap<>();
        }
        
        // If not, download the JSON file from object storage
        String downloadedUri = objectStorageService
                .downloadObject(readObjectUri, processedFilename);
        downloadedParsedSimpleOntologyFiles.put(key, downloadedUri);
        return downloadedUri;
        
    }
    
    /**
     * Retrieve a modelled property graph file from the object storage 
     * loaded container given a Git webhook object, and return the locally 
     * downloaded absolute file path.
     * @param gitWebhook
     * @return
     * @throws IOException
     */
    
    public String retrieveModelledPropertyGraphFile(GitWebhook gitWebhook) 
            throws IOException {
        
        String key = generateKey(gitWebhook);
        String processedFilename = generateProcessedFilename(gitWebhook) 
                + ".json";
        String readObjectUri = getReadObjectUri(loadedDirectoryName, 
                processedFilename);
        
        // Check whether the JSON file has been downloaded recently.
        // If so, and if the file still exists, then return the path to it
        if ( downloadedModelledPropetryGraphFiles != null ) {
            if ( downloadedModelledPropetryGraphFiles.containsKey(key) ) {
                String previouslyDownloadedUri = 
                        downloadedModelledPropetryGraphFiles.get(key);
                Path path = Paths.get(previouslyDownloadedUri);
                if ( Files.exists(path) )
                    return previouslyDownloadedUri;
                else
                    downloadedModelledPropetryGraphFiles.remove(key);
            }
        } else {
            downloadedModelledPropetryGraphFiles = new HashMap<>();
        } 
        
        // If not, download the JSON file from object storage
        String downloadedUri = objectStorageService
                .downloadObject(readObjectUri, processedFilename);
        downloadedModelledPropetryGraphFiles.put(key, downloadedUri);
        return downloadedUri;
        
    }
    
}
