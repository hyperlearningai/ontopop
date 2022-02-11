package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.WebProtegeWebhookRepository;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;

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
    private WebProtegeExporterDownloadService webProtegeExporterDownloadService;
    
    @Autowired
    private WebProtegeExporterUploadService webProtegeExporterUploadService;
    
    private WebProtegeWebhook webProtegeWebhook;
    private List<Ontology> ontologies;
    private boolean isLaterWebProtegeRevision = false;
    private String extractedOwlAbsolutePath;
    
    /**
     * Run the end-to-end WebProtege exporter service
     */
    
    public void run(WebProtegeWebhook webProtegeWebhook) {
        
        LOGGER.info("WebProtege export service started.");
        this.webProtegeWebhook = webProtegeWebhook;
        
        try {
         
            // 1. Validate that the revision number is greater than
            // the latest revision number already processed for the 
            // given WebProtege project ID
            validate();
            
            // 2. Export and download the ontology from WebProtege 
            // in RDF/XML OWL format
            download();
            
            // 3. Upload the downloaded ontology to object storage
            upload();
            
            // 4. Push the exported ontology to the relevant Git repositories
            gitPush();
            
        } catch (Exception e) {
            LOGGER.error("WebProtege export service encountered an error.", e);
        }
        
        LOGGER.info("WebProtege export service finished.");
        
    }
    
    /**
     * Validate that the revision number is greater than the latest 
     * revision number already processed for the  given WebProtege project ID.
     */
    
    private void validate() {
        
        // Check that there exists an ontology with this WebProtege project ID
        ontologies = ontologyRepository.findByWebProtegeProjectId(
                webProtegeWebhook.getProjectId());
        if ( !ontologies.isEmpty() ) {
        
            // Get the latest revision number already processed for this 
            // WebProtege project ID
            List<WebProtegeWebhook> webProtegeWebhooks = webProtegeWebhookRepository
                    .findByWebProtegeProjectId(webProtegeWebhook.getProjectId());
            if ( webProtegeWebhooks.isEmpty() )
                isLaterWebProtegeRevision = true;
            else {
                int maxCurrentRevisionNumber = webProtegeWebhooks.stream()
                    .max(Comparator.comparing(
                            WebProtegeWebhook::getRevisionNumber))
                    .get().getRevisionNumber();
                if ( webProtegeWebhook.getRevisionNumber() 
                        > maxCurrentRevisionNumber )
                    isLaterWebProtegeRevision = true;
            }
            
        }
        
    }
    
    /**
     * Export and download the ontology from WebProtege in RDF/XML OWL format
     * @throws Exception 
     */
    
    private void download() throws Exception {
        if ( isLaterWebProtegeRevision )
            extractedOwlAbsolutePath = webProtegeExporterDownloadService.run(
                    webProtegeWebhook.getProjectId(), 
                    webProtegeWebhook.getRevisionNumber());
    }
    
    /**
     * Upload the downloaded ontology to object storage
     * @throws Exception
     */
    
    private void upload() throws Exception {
        if ( isLaterWebProtegeRevision && !extractedOwlAbsolutePath.isBlank() )
            webProtegeExporterUploadService.run(extractedOwlAbsolutePath);
    }
    
    /**
     * Push the exported ontology to the relevant Git repositories
     */
    
    private void gitPush() {
        
        if ( isLaterWebProtegeRevision ) {
            
            // Iterate over all the ontologies with this WebProtege project ID
            for ( Ontology ontology :  ontologies  ) {
                
                // Get the relevant Git repository details
                String repoUrl = ontology.getRepoUrl();
                String repoBranch = ontology.getRepoBranch();
                String repoResourcePath = ontology.getRepoResourcePath();
                
                
            }
            
        }
        
    }

}
