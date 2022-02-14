package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.git.GitService;
import ai.hyperlearning.ontopop.git.GitServiceFactory;
import ai.hyperlearning.ontopop.model.git.GitAuthor;
import ai.hyperlearning.ontopop.model.git.GitCommitter;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;
import ai.hyperlearning.ontopop.security.secrets.managers.OntologySecretDataManager;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;

/**
 * WebProtege Exporter Git Push Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class WebProtegeExporterGitPushService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeExporterGitPushService.class);
    
    private static final String GIT_SERVICE = "github";
    private static final String WEBPROTEGE_COMMIT_MESSAGE = "WebProtege "
            + "revision number {revisionNumber}";
    
    @Autowired
    private GitServiceFactory gitServiceFactory;
    
    @Autowired
    private OntologySecretDataManager ontologySecretDataManager;
    
    @Value("${vcs.git.committer.name}")
    private String committerName;
    
    @Value("${vcs.git.committer.email}")
    private String committerEmail;
    
    private WebProtegeWebhook webProtegeWebhook;
    private Ontology ontology;
    private String extractedOwlAbsolutePath;
    private GitService gitService;
    
    /**
     * Run the WebProtege Exporter Git Push service
     * @param extractedOwlAbsolutePath
     * @throws Exception
     */
    
    public void run(WebProtegeWebhook webProtegeWebhook, 
            Ontology ontology, String extractedOwlAbsolutePath) 
            throws Exception {
        
        LOGGER.info("WebProtege exporter Git push service started.");
        this.webProtegeWebhook = webProtegeWebhook;
        this.ontology = ontology;
        this.extractedOwlAbsolutePath = extractedOwlAbsolutePath;
        
        try {
            
            // 1. Environment setup
            setup();
            
            // 2. Push the exported OWL file to the relevant Git repository
            push();
            
        } finally {
            
            // 3. Close all Git clients
            cleanup();
            
        }
        
        LOGGER.info("WebProtege exporter Git push service finished.");
        
    }
    
    /**
     * Instantiate the relevant Git service
     * 
     * @throws IOException
     */
    
    private void setup() {
        
        // 1. Select the relevant Git service
        gitService = gitServiceFactory.getGitService(GIT_SERVICE);
        LOGGER.debug("Using the {} object storage service.", GIT_SERVICE);
        
    }
    
    /**
     * Push the exported OWL file to the relevant Git repository
     * @throws Exception 
     */
    
    private void push() throws Exception {
        
        // Get the personal access token for the Git repository
        OntologySecretData ontologySecretData =
                ontologySecretDataManager.get(ontology.getId());
        
        // Create a GitCommitter object
        GitCommitter gitCommitter = new GitCommitter(
                committerName, committerEmail);
        LOGGER.debug("Created a Git Committer: {}.", gitCommitter);
        
        // Create a GitAuthor object
        int atIndex = webProtegeWebhook.getUserId().indexOf("@");
        String gitAuthorUsername = atIndex > -1 ? 
                webProtegeWebhook.getUserId().substring(0, atIndex) : 
                    webProtegeWebhook.getUserId();
        String gitAuthorEmail = atIndex > -1 ? webProtegeWebhook.getUserId() : 
            gitAuthorUsername + "@ontopop.com";
        GitAuthor gitAuthor = new GitAuthor(gitAuthorUsername, gitAuthorEmail);
        LOGGER.debug("Created a Git Author: {}.", gitAuthor);
        
        // Generate the commit message
        String commitMessage = WEBPROTEGE_COMMIT_MESSAGE.replace(
                "{revisionNumber}", 
                String.valueOf(webProtegeWebhook.getRevisionNumber()));
        LOGGER.debug("Created a Git Commit Message: {}.", commitMessage);
        
        // Update the file in the Git repository
        LOGGER.info("Updating file in the Git Repository with '{}': "
                + "repos/{}/{}/contents/{}?ref={}", extractedOwlAbsolutePath, 
                ontology.getRepoOwner(), ontology.getRepoName(), 
                ontology.getRepoResourcePath(), ontology.getRepoBranch());
        gitService.putFile(
                ontology.getRepoOwner(), 
                ontology.getRepoName(), 
                ontology.getRepoResourcePath(), 
                ontology.getRepoBranch(), 
                ontologySecretData.getRepoToken(), 
                extractedOwlAbsolutePath, 
                gitCommitter, gitAuthor, commitMessage);
        
    }
    
    /**
     * Close all Git Clients
     */
    
    private void cleanup() {
        
        
    }

}
