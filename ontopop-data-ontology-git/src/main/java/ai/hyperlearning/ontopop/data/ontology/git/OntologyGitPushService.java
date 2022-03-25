package ai.hyperlearning.ontopop.data.ontology.git;

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
import ai.hyperlearning.ontopop.security.secrets.managers.OntologySecretDataManager;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;

/**
 * Ontology Git Push Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class OntologyGitPushService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGitPushService.class);
    
    private static final String GIT_SERVICE = "github";
    private static final String DEFAULT_COMMIT_MESSAGE = 
            "Update via the OntoPop Triplestore API";
    
    @Autowired
    private GitServiceFactory gitServiceFactory;
    
    @Autowired
    private OntologySecretDataManager ontologySecretDataManager;
    
    @Value("${vcs.git.committer.name}")
    private String committerName;
    
    @Value("${vcs.git.committer.email}")
    private String committerEmail;
    
    private Ontology ontology;
    private String ontologyDataFileToPushAbsolutePath;
    private String clientName;
    private String commitMessage;
    private GitService gitService;
    
    public void run(Ontology ontology, 
            String ontologyDataFileToPushAbsolutePath, 
            String clientName, String commitMessage) 
            throws Exception {
        
        LOGGER.info("Ontology Git push service started.");
        this.ontology = ontology;
        this.ontologyDataFileToPushAbsolutePath = 
                ontologyDataFileToPushAbsolutePath;
        this.clientName = clientName;
        this.commitMessage = commitMessage;
        
        try {
            
            // 1. Environment setup
            setup();
            
            // 2. Push the exported OWL file to the relevant Git repository
            push();
            
        } finally {
            
            // 3. Close all Git clients
            cleanup();
            
        }
        
        LOGGER.info("Ontology Git push service finished.");
        
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
     * Push the ontology data file to the relevant Git repository
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
        String gitAuthorUsername = clientName != null ? clientName : 
            committerName;
        GitAuthor gitAuthor = new GitAuthor(gitAuthorUsername, committerEmail);
        LOGGER.debug("Created a Git Author: {}.", gitAuthor);
        
        // Generate the commit message
        String gitCommitMessage = commitMessage != null ? commitMessage : 
            DEFAULT_COMMIT_MESSAGE;
        LOGGER.debug("Created a Git Commit Message: {}.", gitCommitMessage);
        
        // Update the file in the Git repository
        LOGGER.info("Updating file in the Git Repository with '{}': "
                + "repos/{}/{}/contents/{}?ref={}", 
                ontologyDataFileToPushAbsolutePath, 
                ontology.getRepoOwner(), ontology.getRepoName(), 
                ontology.getRepoResourcePath(), ontology.getRepoBranch());
        gitService.putFile(
                ontology.getRepoOwner(), 
                ontology.getRepoName(), 
                ontology.getRepoResourcePath(), 
                ontology.getRepoBranch(), 
                ontologySecretData.getRepoToken(), 
                ontologyDataFileToPushAbsolutePath, 
                gitCommitter, gitAuthor, gitCommitMessage);
        
    }
    
    /**
     * Close all Git Clients
     */
    
    private void cleanup() {
        
        
    }

}
