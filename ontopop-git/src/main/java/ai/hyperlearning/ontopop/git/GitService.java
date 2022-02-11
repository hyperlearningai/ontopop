package ai.hyperlearning.ontopop.git;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import ai.hyperlearning.ontopop.model.git.GitWebhook;

/**
 * Git Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface GitService {

    /**
     * Parse a webhook JSON request payload into a GitWebhook object
     * 
     * @param headers
     * @param payload
     * @param path
     * @return
     * @throws IOException
     */

    GitWebhook parseWebhookPayload(Map<String, String> headers,
            String payload, String path) throws IOException;

    /**
     * Validate that the contents of a webhook JSON request payload matches the
     * expected signature hash, and repository & resource attributes
     * 
     * @param headers
     * @param payload
     * @param path
     * @param secret
     * @param repo
     * @param owner
     * @param branch
     * @return
     * @throws IOException
     */

    boolean isValidWebhookPayload(Map<String, String> headers, String payload,
            String path, String secret, String repo, String owner,
            String branch) throws IOException;

    /**
     * Get the string contents of a file resource managed by a public GitHub
     * repository
     * 
     * @param owner
     * @param repo
     * @param path
     * @param branch
     * @return
     * @throws IOException
     */

    ResponseEntity<String> getFile(String owner, String repo, String path,
            String branch) throws IOException;

    /**
     * Get the string contents of a file resource managed by a private GitHub
     * repository
     * 
     * @param token
     * @param owner
     * @param repo
     * @param path
     * @param branch
     * @return
     * @throws IOException
     */

    ResponseEntity<String> getFile(String token, String owner, String repo,
            String path, String branch) throws IOException;

}
