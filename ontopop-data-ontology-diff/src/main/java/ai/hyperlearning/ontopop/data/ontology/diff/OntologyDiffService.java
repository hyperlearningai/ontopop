package ai.hyperlearning.ontopop.data.ontology.diff;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
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
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffProcessingException;
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
    private String beforeDownloadedFileUri;
    private String afterDownloadedFileUri;
    private Ontology ontology;
    private SimpleOntologyTimestampDiff simpleOntologyTimestampDiff = 
            new SimpleOntologyTimestampDiff();
    private SimpleOntologyLeftRightDiff simpleOntologyLeftRightDiff = 
            new SimpleOntologyLeftRightDiff();
    
    /**
     * Ontological diff given an ontology ID and timestamp
     * @param ontologyId
     * @param requestedTimestamp
     * @return
     * @throws OntologyDiffProcessingException
     */
    
    public SimpleOntologyTimestampDiff run(
            int ontologyId, LocalDateTime requestedTimestamp) 
            throws OntologyDiffProcessingException {
        
        LOGGER.info("Ontology Diff Service started.");
        LOGGER.info("Ontology Diff Service - Ontology ID: {}", 
                ontologyId);
        LOGGER.info("Ontology Diff Service - Requested Timestamp: {}", 
                requestedTimestamp);
        simpleOntologyTimestampDiff.setId(ontologyId);
        simpleOntologyTimestampDiff.setRequestedTimestamp(requestedTimestamp);
        
        try {
            
            // 1. Environment setup
            setup(true);
            
            // 2. Resolve the latest Git webhooks
            resolve();
            
            // 3. Download the relevant ontology OWL files from object storage
            download(true);
            
            // 4. Process the ontological diff
            diff(true);
            
            // 5. Return the ontological diff
            return simpleOntologyTimestampDiff;
            
        } catch (Exception e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyDiffProcessingException(
                    "Ontology Diff Service encountered an error: " + e);
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
            
        } catch (Exception e) {
            LOGGER.error("Ontology Diff Service encountered an error.", e);
            throw new OntologyDiffProcessingException(
                    "Ontology Diff Service encountered an error: " + e);
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
                .orElseThrow(() -> new OntologyNotFoundException(ontologyId));
        
        // Get the requested Git webhooks IDs
        if ( !timestampDiff ) {
            gitWebhookRepository.findByOntologyIdAndGitWebhookId(
                    ontologyId, 
                    simpleOntologyLeftRightDiff.getLeftGitWebhookId())
            .orElseThrow(() -> new GitWebhookNotFoundException(
                    simpleOntologyLeftRightDiff.getLeftGitWebhookId()));
            gitWebhookRepository.findByOntologyIdAndGitWebhookId(
                    ontologyId, 
                    simpleOntologyLeftRightDiff.getRightGitWebhookId())
            .orElseThrow(() -> new GitWebhookNotFoundException(
                    simpleOntologyLeftRightDiff.getRightGitWebhookId()));
        }

    }
    
    /**
     * Resolve the latest Git webhook and the latest Git webhook
     * before the requested timestamp.
     * @throws OntologyNotFoundException
     */
    
    private void resolve() {
        
        // Get the latest Git webhook associated with this ontology
        Set<GitWebhook> gitWebhooks = ontology.getGitWebhooks();
        if ( !gitWebhooks.isEmpty() ) {
            
            // If only one Git webhook has been consumed
            if ( gitWebhooks.size() == 1 ) {
                GitWebhook latestGitWebhook = gitWebhooks.iterator().next();
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
                simpleOntologyTimestampDiff.setUpdatesExist(false);
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
                    .setUpdatesExist(latestGitWebhook.getId() != 
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
            
        } else {
            simpleOntologyTimestampDiff.setUpdatesExist(false);
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
            if ( simpleOntologyTimestampDiff.doUpdatesExist() ) {
                String beforeFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyTimestampDiff
                            .getLatestGitWebhookIdBeforeRequestedTimestamp());
                beforeDownloadedFileUri = objectStorageService.downloadObject(
                        readContainerUri + beforeFilename, "_" + beforeFilename);
                String afterFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyTimestampDiff
                            .getLatestGitWebhookIdAfterRequestedTimestamp());
                afterDownloadedFileUri = objectStorageService.downloadObject(
                        readContainerUri + afterFilename, "_" + afterFilename);
            }
        }
        
        // Left right diff
        else {
            if ( simpleOntologyLeftRightDiff.getLeftGitWebhookId() != 
                    simpleOntologyLeftRightDiff.getRightGitWebhookId()) {
                String beforeFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyLeftRightDiff.getLeftGitWebhookId());
                beforeDownloadedFileUri = objectStorageService.downloadObject(
                        readContainerUri + beforeFilename, "_" + beforeFilename);
                String afterFilename = ontology.generateFilenameForPersistence(
                        simpleOntologyLeftRightDiff.getRightGitWebhookId());
                afterDownloadedFileUri = objectStorageService.downloadObject(
                        readContainerUri + afterFilename, "_" + afterFilename);
            }
        }
        
    }
    
    /**
     * Process the ontological diff
     * @throws OWLOntologyCreationException
     * @throws IOException 
     */
    
    private void diff(boolean timestampDiff) 
            throws OWLOntologyCreationException, IOException {
        if ( timestampDiff ) {
            if ( simpleOntologyTimestampDiff.doUpdatesExist() ) {
                SimpleOntologyDiff diff = OWLAPI.diff(beforeDownloadedFileUri, 
                        afterDownloadedFileUri);
                simpleOntologyTimestampDiff.setSimpleOntologyDiff(diff);
                simpleOntologyTimestampDiff.setUpdatesExist(diff.doUpdatesExist());
            }
        } else {
            if ( simpleOntologyLeftRightDiff.getLeftGitWebhookId() != 
                    simpleOntologyLeftRightDiff.getRightGitWebhookId()) {
                SimpleOntologyDiff diff = OWLAPI.diff(beforeDownloadedFileUri, 
                        afterDownloadedFileUri);
                simpleOntologyLeftRightDiff.setSimpleOntologyDiff(diff);
                simpleOntologyLeftRightDiff.setChangesExist(diff.doUpdatesExist());
            }
        }
    }

}
