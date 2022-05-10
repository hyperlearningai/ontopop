package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.WebProtegeWebhookRepository;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineProcessingException;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;
import ai.hyperlearning.ontopop.webprotege.WebProtegeDownloader;

/**
 * WebProtege Exporter Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@Transactional
public class WebProtegeExporterService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeExporterService.class);
    
    @Autowired
    private OntologyRepository ontologyRepository;
    
    @Autowired
    private WebProtegeWebhookRepository webProtegeWebhookRepository;
    
    @Autowired
    private WebProtegeDownloader webProtegeDownloader;
    
    @Autowired
    private WebProtegeExporterUploadService webProtegeExporterUploadService;
    
    @Autowired
    private WebProtegeExporterGitPushService webProtegeExporterGitPushService;
    
    @Value("${plugins.webprotege.exporter.scheduler.enabled:false}")
    private Boolean schedulerEnabled;
    
    private WebProtegeWebhook webProtegeWebhook;
    private List<Ontology> ontologies;
    private boolean isLaterWebProtegeRevision = false;
    private String extractedOwlAbsolutePath;
    
    /**
     * Run the end-to-end WebProtege exporter service
     */
    
    public void run(WebProtegeWebhook webProtegeWebhook) 
            throws OntologyDataPipelineProcessingException {
        
        LOGGER.info("WebProtege export service started.");
        this.webProtegeWebhook = webProtegeWebhook;
        
        try {
         
            // 1. Insert the WebProtege webhook record if the
            // scheduler has been disabled
            insert();
            
            // 2. Validate that the revision number is greater than
            // the latest revision number already processed for the 
            // given WebProtege project ID
            validate();
            
            // 3. Export and download the ontology from WebProtege 
            // in RDF/XML OWL format
            download();
            
            // 4. Upload the downloaded ontology to object storage
            upload();
            
            // 5. Push the exported ontology to the relevant Git repositories
            gitPush();
            
        } catch (Exception e) {
            LOGGER.error("WebProtege export service encountered an error.", e);
            throw new OntologyDataPipelineProcessingException(
                    "WebProtege export service encountered an error: " + e);
        }
        
        LOGGER.info("WebProtege export service finished.");
        
    }
    
    /**
     * Insert the WebProtege webhook record if the scheduler 
     * has been disabled.
     */
    
    private void insert() {
        
        // Insert the WebProtege webhook record
        if ( Boolean.FALSE.equals(schedulerEnabled) )
            webProtegeWebhookRepository.save(webProtegeWebhook);
        
    }
    
    /**
     * Validate that the revision number is greater than the latest 
     * revision number already processed for the  given WebProtege project ID.
     */
    
    private void validate() {
        
        // Check that there exists an ontology with this WebProtege project ID
        LOGGER.info("Validating that there exists at least one ontology with "
                + "the following WebProtege project ID: {}", 
                webProtegeWebhook.getProjectId());
        ontologies = ontologyRepository.findByWebProtegeProjectId(
                webProtegeWebhook.getProjectId());
        LOGGER.info("Found {} ontologies with this WebProtege project ID.", 
                ontologies.size());
        int maxCurrentRevisionNumber = 0;
        if ( !ontologies.isEmpty() ) {
            
            // Get the latest revision number already processed for this 
            // WebProtege project ID
            maxCurrentRevisionNumber = ontologies.stream().max(
                    Comparator.comparing(
                            Ontology::getLatestWebProtegeRevisionNumber))
                    .get().getLatestWebProtegeRevisionNumber();
            if ( webProtegeWebhook.getRevisionNumber() 
                    > maxCurrentRevisionNumber )
              isLaterWebProtegeRevision = true;
            
        }
        
        // Explicit logging
        LOGGER.info("Current highest revision number already processed for "
                + "this WebProtege project ID: {}", maxCurrentRevisionNumber);
        LOGGER.info("Revision number received from WebProtege: {}", 
                webProtegeWebhook.getRevisionNumber());
        if ( isLaterWebProtegeRevision )
            LOGGER.info("Proceeding with the export from WebProtege.");
        else
            LOGGER.info("A higher revision number has already been processed "
                    + "for this WebProtege project ID.");
        
    }
    
    /**
     * Export and download the ontology from WebProtege in RDF/XML OWL format
     * @throws Exception 
     */
    
    private void download() throws Exception {
        if ( isLaterWebProtegeRevision ) {
            LOGGER.info("Exporting revision number {} for WebProtege project "
                    + "ID {} from WebProtege in RDF/XML OWL format.", 
                    webProtegeWebhook.getRevisionNumber(), 
                    webProtegeWebhook.getProjectId());
            extractedOwlAbsolutePath = webProtegeDownloader.run(
                    webProtegeWebhook.getProjectId(), 
                    webProtegeWebhook.getRevisionNumber(), 
                    30, true);
        }
    }
    
    /**
     * Upload the downloaded ontology to object storage
     * @throws Exception
     */
    
    private void upload() throws Exception {
        if ( isLaterWebProtegeRevision && 
                !extractedOwlAbsolutePath.isBlank() ) {
            LOGGER.info("Uploading revision number {} for WebProtege project "
                    + "ID {} to object storage.", 
                    webProtegeWebhook.getRevisionNumber(), 
                    webProtegeWebhook.getProjectId());
            webProtegeExporterUploadService.run(extractedOwlAbsolutePath);
        }
    }
    
    /**
     * Push the exported ontology to the relevant Git repositories
     * @throws Exception 
     */
    
    private void gitPush() throws Exception {
        
        if ( isLaterWebProtegeRevision ) {
            
            // Iterate over all the ontologies with this WebProtege project ID
            for ( Ontology ontology :  ontologies  ) {
                
                // Update the file in the relevant Git repository
                LOGGER.info("Pushing revision number {} for WebProtege project "
                        + "ID {} to the following Git repository: {}", 
                        webProtegeWebhook.getRevisionNumber(), 
                        webProtegeWebhook.getProjectId(), 
                        ontology.getRepoUrl());
                webProtegeExporterGitPushService.run(
                        webProtegeWebhook, ontology, extractedOwlAbsolutePath);
                
                // Update the ontology record
                ontology.setLatestWebProtegeRevisionNumber(
                        webProtegeWebhook.getRevisionNumber());
                ontologyRepository.save(ontology);
                
            }
            
        }
        
    }

}
