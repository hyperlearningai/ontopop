package ai.hyperlearning.ontopop.data.ontology.diff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.exceptions.git.GitWebhookNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyLeftRightDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyTimestampDiff;
import ai.hyperlearning.ontopop.owl.OWLAPI;
import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * Ontology Diff Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@Transactional
public class OntologyDiffService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyDiffService.class);
    
    @Autowired
    private OntologyRepository ontologyRepository;
    
    @Autowired
    private GitWebhookRepository gitWebhookRepository;
    
    @Autowired
    private ObjectStorageServiceFactory objectStorageServiceFactory;
    
    @Value("${storage.object.service}")
    private String storageObjectService;

    @Value("${storage.object.local.baseUri}")
    private String storageLocalBaseUri;

    @Value("${storage.object.containers.loaded.triplestore}")
    private String loadedDirectoryName;
    
    private ObjectStorageService objectStorageService;
    private String readContainerUri;
    private Map<String, String> downloadedFilesMap = new HashMap<>();
    private String beforeDownloadedFileUri;
    private String afterDownloadedFileUri;
    private Ontology ontology;
    private SimpleOntologyTimestampDiff simpleOntologyTimestampDiff = 
            new SimpleOntologyTimestampDiff();
    private SimpleOntologyLeftRightDiff simpleOntologyLeftRightDiff = 
            new SimpleOntologyLeftRightDiff();
    
    /**
     * Ontological temproal diff given an ontology ID and timestamp
     * @param ontologyId
     * @param requestedTimestamp
     * @return
     * @throws OntologyDiffException
     */
    
    public SimpleOntologyTimestampDiff run(
            int ontologyId, LocalDateTime requestedTimestamp) {
        
        LOGGER.info("Ontology Diff Service started.");
        LOGGER.info("Ontology Diff Service - Ontology ID: {}", 
                ontologyId);
        LOGGER.info("Ontology Diff Service - Requested Timestamp: {}", 
                requestedTimestamp);
        simpleOntologyTimestampDiff = new SimpleOntologyTimestampDiff();
        simpleOntologyTimestampDiff.setId(ontologyId);
        simpleOntologyTimestampDiff.setRequestedTimestamp(requestedTimestamp);
        
        try {
            
            // 1. Environment setup
            setup(true);
            
            // 2. Resolve the latest Git webhooks
            resolve(false);
            
            // 3. Download the relevant ontology OWL files from object storage
            download(true);
            
            // 4. Process the ontological diff
            diff(true);
            
            // 5. Return the ontological diff
            return simpleOntologyTimestampDiff;
            
        } catch (OntologyNotFoundException e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyNotFoundException();
        } catch (Exception e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyDiffException();
        }
        
    }
    
    /**
     * Ontological temporal diff given an ontology ID and Git webhook ID
     * @param ontologyId
     * @param gitWebhookId
     * @return
     */
    
    public SimpleOntologyTimestampDiff run(int ontologyId, long gitWebhookId) {
        
        LOGGER.info("Ontology Diff Service started.");
        LOGGER.info("Ontology Diff Service - Ontology ID: {}", 
                ontologyId);
        LOGGER.info("Ontology Diff Service - Git Webhook ID: {}", 
                gitWebhookId);
        simpleOntologyTimestampDiff = new SimpleOntologyTimestampDiff();
        simpleOntologyTimestampDiff.setId(ontologyId);
        simpleOntologyTimestampDiff
            .setLatestGitWebhookIdBeforeRequestedTimestamp(gitWebhookId);
        
        try {
            
            // 1. Environment setup
            setup(true);
            
            // 2. Resolve the latest Git webhooks
            resolve(true);
            
            // 3. Download the relevant ontology OWL files from object storage
            download(true);
            
            // 4. Process the ontological diff
            diff(true);
            
            // 5. Return the ontological diff
            return simpleOntologyTimestampDiff;
            
        } catch (OntologyNotFoundException e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyNotFoundException();
        } catch (GitWebhookNotFoundException e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new GitWebhookNotFoundException();
        } catch (Exception e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyDiffException();
        }
        
    }
    
    /**
     * Ontological diff given two Git webhook IDs
     * @param ontologyId
     * @param leftGitWebhookId
     * @param rightGitWebhookId
     * @return
     */
    
    public SimpleOntologyLeftRightDiff run(
            int ontologyId, long leftGitWebhookId, long rightGitWebhookId) {
        
        LOGGER.info("Ontology Diff Service started.");
        LOGGER.info("Ontology Diff Service - Ontology ID: {}", 
                ontologyId);
        LOGGER.info("Ontology Diff Service - Left Git Webhook ID: {}", 
                leftGitWebhookId);
        LOGGER.info("Ontology Diff Service - Right Git Webhook ID: {}", 
                rightGitWebhookId);
        simpleOntologyLeftRightDiff = new SimpleOntologyLeftRightDiff();
        simpleOntologyLeftRightDiff.setId(ontologyId);
        simpleOntologyLeftRightDiff.setLeftGitWebhookId(leftGitWebhookId);
        simpleOntologyLeftRightDiff.setRightGitWebhookId(rightGitWebhookId);
        
        try {
            
            // 1. Environment setup
            setup(false);
            
            // 2. Download the relevant ontology OWL files from object storage
            download(false);
            
            // 3. Process the ontological diff
            diff(false);
            
            // 4. Return the ontological diff
            return simpleOntologyLeftRightDiff;
            
        } catch (OntologyNotFoundException e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyNotFoundException();
        } catch (GitWebhookNotFoundException e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new GitWebhookNotFoundException();
        } catch (Exception e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyDiffException();
        }
        
    }
    
    /**
     * Instantiate the relevant object storage service
     * 
     * @throws IOException
     */

    private void setup(boolean timestampDiff) 
            throws OntologyNotFoundException, GitWebhookNotFoundException {

        // Instantiate the relevant object storage service
        ObjectStorageServiceType objectStorageServiceType =
                ObjectStorageServiceType
                        .valueOfLabel(storageObjectService.toUpperCase());
        objectStorageService = objectStorageServiceFactory
                .getObjectStorageService(objectStorageServiceType);
        LOGGER.debug("Using the {} object storage service.",
                objectStorageServiceType);
        readContainerUri = objectStorageServiceType
                .equals(ObjectStorageServiceType.LOCAL) ? 
                        storageLocalBaseUri + File.separator 
                            + loadedDirectoryName + File.separator : 
                                loadedDirectoryName + "/";
        
        // Get the requested ontology
        int ontologyId = timestampDiff ? 
                simpleOntologyTimestampDiff.getId() : 
                    simpleOntologyLeftRightDiff.getId();
        ontology = ontologyRepository
                .findById(ontologyId)
                .orElseThrow(OntologyNotFoundException::new);
        
        if ( !timestampDiff ) {
            GitWebhook gitWebhookLeft = gitWebhookRepository
                    .findByOntologyIdAndGitWebhookId(
                            ontologyId, 
                            simpleOntologyLeftRightDiff.getLeftGitWebhookId())
                    .orElseThrow(GitWebhookNotFoundException::new);
            LOGGER.debug("Found left Git webhook: {}", gitWebhookLeft);
            GitWebhook gitWebhookRight = gitWebhookRepository
                    .findByOntologyIdAndGitWebhookId(
                            ontologyId, 
                            simpleOntologyLeftRightDiff.getRightGitWebhookId())
                    .orElseThrow(GitWebhookNotFoundException::new);
            LOGGER.debug("Found right Git webhook: {}", gitWebhookRight);
        }

    }
    
    /**
     * Resolve the latest Git webhook and the latest Git webhook
     * before the requested timestamp.
     * @throws OntologyNotFoundException
     */
    
    private void resolve(boolean timestampDiffWithGitWebhookId) {
        
        // Get the latest Git webhook associated with this ontology
        Set<GitWebhook> gitWebhooks = ontology.getGitWebhooks();
        if ( !gitWebhooks.isEmpty() ) {
            
            // If only one Git webhook has been consumed
            if ( gitWebhooks.size() == 1 ) {
                
                // Get the only Git webhook
                GitWebhook latestGitWebhook = gitWebhooks.iterator().next();
                
                // Timestamp diff with Git webhook ID
                if ( timestampDiffWithGitWebhookId ) {
                    
                    // Get the requested Git webhooks ID(s)
                    GitWebhook requestedGitWebhook = gitWebhookRepository
                        .findByOntologyIdAndGitWebhookId(
                            simpleOntologyTimestampDiff.getId(), 
                            simpleOntologyTimestampDiff
                                .getLatestGitWebhookIdBeforeRequestedTimestamp())
                    .orElseThrow(GitWebhookNotFoundException::new);
                    if ( latestGitWebhook.getId() != requestedGitWebhook.getId() )
                        throw new GitWebhookNotFoundException();
                    else
                        simpleOntologyTimestampDiff.setRequestedTimestamp(
                                requestedGitWebhook.getDateCreated());
                    
                }
                
                // Update the diff object
                simpleOntologyTimestampDiff
                    .setLatestGitWebhookIdAfterRequestedTimestamp(
                        latestGitWebhook.getId());
                simpleOntologyTimestampDiff
                    .setLatestGitWebhookTimestampAfterRequestedTimestamp(
                        latestGitWebhook.getDateCreated());
                simpleOntologyTimestampDiff
                    .setLatestGitWebhookIdBeforeRequestedTimestamp(
                        latestGitWebhook.getId());
                simpleOntologyTimestampDiff
                    .setLatestGitWebhookTimestampBeforeRequestedTimestamp(
                        latestGitWebhook.getDateCreated());
                simpleOntologyTimestampDiff.setChangesExist(false);
                LOGGER.info("Ontology Diff Service - Only one Git Webhook "
                        + "has been consumed for this ontology.");
                
            }
            
            // If more than one Git webhook has been consumed
            else {
            
                // Get the latest Git webhook object
                GitWebhook latestGitWebhook = gitWebhooks.stream()
                        .max(Comparator.comparing(GitWebhook::getId)).get();
                simpleOntologyTimestampDiff
                    .setLatestGitWebhookIdAfterRequestedTimestamp(
                        latestGitWebhook.getId());
                simpleOntologyTimestampDiff
                    .setLatestGitWebhookTimestampAfterRequestedTimestamp(
                        latestGitWebhook.getDateCreated());
                
                // Timestamp diff with Git webhook ID
                if ( timestampDiffWithGitWebhookId ) {
                    
                    // Get the requested Git webhooks ID(s)
                    GitWebhook requestedGitWebhook = gitWebhookRepository
                        .findByOntologyIdAndGitWebhookId(
                            simpleOntologyTimestampDiff.getId(), 
                            simpleOntologyTimestampDiff
                                .getLatestGitWebhookIdBeforeRequestedTimestamp())
                    .orElseThrow(GitWebhookNotFoundException::new);
                    
                    // Update the diff object
                    simpleOntologyTimestampDiff
                        .setLatestGitWebhookIdBeforeRequestedTimestamp(
                                requestedGitWebhook.getId());
                    simpleOntologyTimestampDiff
                        .setLatestGitWebhookTimestampBeforeRequestedTimestamp(
                                requestedGitWebhook.getDateCreated());
                    simpleOntologyTimestampDiff.setRequestedTimestamp(
                            requestedGitWebhook.getDateCreated());
                    simpleOntologyTimestampDiff.setChangesExist(
                            latestGitWebhook.getId() > 
                                requestedGitWebhook.getId());
                    
                    // Logging
                    LOGGER.info("Ontology Diff Service - {} Git Webhooks "
                            + "have been consumed for this ontology.", 
                            gitWebhooks.size());
                    LOGGER.info("Ontology Diff Service - Requested Git "
                            + "webhook ID: {}", requestedGitWebhook.getId());
                    LOGGER.info("Ontology Diff Service - Latest Git "
                            + "webhook ID: {}", latestGitWebhook.getId());
                    
                }
                
                // Timestamp diff with timestamp
                else {
                    
                    // Get the latest Git webhook object BEFORE the requested timestamp
                    GitWebhook latestGitWebhookBeforeRequestedTimestamp = null;
                    int counter = 0;
                    for ( GitWebhook gitWebhook : gitWebhooks ) {
                        if (counter == 0)
                            latestGitWebhookBeforeRequestedTimestamp = gitWebhook;
                        if ( gitWebhook.getDateCreated().isBefore(
                                simpleOntologyTimestampDiff.getRequestedTimestamp()) && 
                                gitWebhook.getDateCreated().isAfter(
                                        latestGitWebhookBeforeRequestedTimestamp
                                            .getDateCreated()))
                            latestGitWebhookBeforeRequestedTimestamp = gitWebhook;
                        counter++;
                    }
                    
                    // Update the diff object
                    simpleOntologyTimestampDiff
                        .setLatestGitWebhookIdBeforeRequestedTimestamp(
                            latestGitWebhookBeforeRequestedTimestamp.getId());
                    simpleOntologyTimestampDiff
                        .setLatestGitWebhookTimestampBeforeRequestedTimestamp(
                            latestGitWebhookBeforeRequestedTimestamp.getDateCreated());
                    simpleOntologyTimestampDiff
                        .setChangesExist(latestGitWebhook.getId() != 
                            latestGitWebhookBeforeRequestedTimestamp.getId());
                    
                    // Logging
                    LOGGER.info("Ontology Diff Service - {} Git Webhooks "
                            + "have been consumed for this ontology.", gitWebhooks.size());
                    LOGGER.info("Ontology Diff Service - "
                            + "Latest Git Webhook ID BEFORE requested timestamp: {} at {}.", 
                            simpleOntologyTimestampDiff
                                .getLatestGitWebhookIdBeforeRequestedTimestamp(), 
                            simpleOntologyTimestampDiff
                                .getLatestGitWebhookTimestampBeforeRequestedTimestamp());
                    LOGGER.info("Ontology Diff Service - "
                            + "Latest Git Webhook ID AFTER requested timestamp: {} at {}.", 
                            simpleOntologyTimestampDiff
                                .getLatestGitWebhookIdAfterRequestedTimestamp(), 
                            simpleOntologyTimestampDiff
                                .getLatestGitWebhookTimestampAfterRequestedTimestamp());
                    
                }
                
            }
            
        } else {
            simpleOntologyTimestampDiff.setChangesExist(false);
            LOGGER.info("Ontology Diff Service - Zero Git Webhooks "
                    + "have been consumed for this ontology.");
        }
        
    }
    
    /**
     * Download the relevant ontology OWL files from object storage
     * @throws IOException
     */
    
    private void download(boolean timestampDiff) throws IOException {
        
        // Timestamp diff
        if ( timestampDiff ) {
            if ( simpleOntologyTimestampDiff.doChangesExist() ) {
                String beforeFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyTimestampDiff
                            .getLatestGitWebhookIdBeforeRequestedTimestamp());
                beforeDownloadedFileUri = 
                        checkExistenceBeforeDownload(beforeFilename);
                String afterFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyTimestampDiff
                            .getLatestGitWebhookIdAfterRequestedTimestamp());
                afterDownloadedFileUri = 
                        checkExistenceBeforeDownload(afterFilename);
            }
        }
        
        // Left right diff
        else {
            if ( simpleOntologyLeftRightDiff.getLeftGitWebhookId() != 
                    simpleOntologyLeftRightDiff.getRightGitWebhookId()) {
                String beforeFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyLeftRightDiff.getLeftGitWebhookId());
                beforeDownloadedFileUri = 
                        checkExistenceBeforeDownload(beforeFilename);
                String afterFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyLeftRightDiff.getRightGitWebhookId());
                afterDownloadedFileUri = 
                        checkExistenceBeforeDownload(afterFilename);
            }
        }
        
    }
    
    /**
     * Check for the existence of a file before downloading it again
     * from object storage
     * @param gitWebhookId
     * @param filename
     * @return
     * @throws IOException
     */
    
    private String checkExistenceBeforeDownload(String filename) 
            throws IOException {
        
        // Check if the OWL file has already been downloaded and exists. 
        // If so, return the path to the downloaded OWL file.
        if ( downloadedFilesMap != null ) {
            if ( downloadedFilesMap.containsKey(filename) ) {
                String previouslyDownloadedFilePath = 
                        downloadedFilesMap.get(filename);
                Path path = Paths.get(previouslyDownloadedFilePath);
                if ( Files.exists(path) )
                    return previouslyDownloadedFilePath;
                else
                    downloadedFilesMap.remove(filename);
            }
        } else {
            downloadedFilesMap = new HashMap<>();
        }
        
        // If not, download the OWL file
        String downloadedFilePath = objectStorageService.downloadObject(
                readContainerUri + filename, "_" + filename);
        downloadedFilesMap.put(filename, downloadedFilePath);
        return downloadedFilePath;
        
    }
    
    /**
     * Process the ontological diff
     * @throws OWLOntologyCreationException
     * @throws IOException 
     */
    
    private void diff(boolean timestampDiff) 
            throws OWLOntologyCreationException, IOException {
        if ( timestampDiff ) {
            if ( simpleOntologyTimestampDiff.doChangesExist() ) {
                SimpleOntologyDiff diff = OWLAPI.diff(beforeDownloadedFileUri, 
                        afterDownloadedFileUri);
                simpleOntologyTimestampDiff.setSimpleOntologyDiff(diff);
                simpleOntologyTimestampDiff.setChangesExist(diff.doChangesExist());
            }
        } else {
            if ( simpleOntologyLeftRightDiff.getLeftGitWebhookId() != 
                    simpleOntologyLeftRightDiff.getRightGitWebhookId()) {
                SimpleOntologyDiff diff = OWLAPI.diff(beforeDownloadedFileUri, 
                        afterDownloadedFileUri);
                simpleOntologyLeftRightDiff.setSimpleOntologyDiff(diff);
                simpleOntologyLeftRightDiff.setChangesExist(diff.doChangesExist());
            }
        }
    }

}
