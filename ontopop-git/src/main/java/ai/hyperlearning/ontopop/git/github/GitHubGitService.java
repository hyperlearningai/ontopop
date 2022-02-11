package ai.hyperlearning.ontopop.git.github;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

import ai.hyperlearning.ontopop.git.GitService;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.git.github.GitHubWebhook;
import reactor.core.publisher.Mono;

/**
 * GitHub Git Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GitHubGitService implements GitService {

    private static final String REQUEST_URI =
            "/repos/{owner}/{repo}/contents/{path}?ref={branch}";
    private static final String HEADER_ACCEPT =
            "application/vnd.github.v3.raw+json";
    private static final String HEADER_AUTHORIZATION_PREFIX = "token";
    private static final String WEBHOOK_REQUEST_HEADER_SIGNATURE_KEY =
            "x-hub-signature-256";
    private static final String WEBHOOK_REQUEST_HEADER_SIGNATURE_VALUE_PREFIX =
            "sha256=";

    @Autowired
    @Qualifier("gitHubWebClient")
    private WebClient webClient;

    /**
     * Parse a GitHub webhook JSON request payload into a GitWebhook object
     */

    @Override
    public GitWebhook parseWebhookPayload(Map<String, String> headers,
            String payload, String path) throws IOException {

        // Map the payload to a GitHubWebhook object
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        GitHubWebhook gitHubWebhook =
                mapper.readValue(payload, GitHubWebhook.class);

        // Convert the GitHubWebhook object to a general GitWebhook object
        String requestHeaderSignature =
                headers.get(WEBHOOK_REQUEST_HEADER_SIGNATURE_KEY);
        return gitHubWebhook.toGitWebhook(path, requestHeaderSignature);

    }

    /**
     * Validate that the contents of a GitHub webhook JSON request payload
     * matches the expected signature hash, and repository & resource attributes
     */

    @Override
    public boolean isValidWebhookPayload(Map<String, String> headers,
            String payload, String path, String secret, String repo,
            String owner, String branch) throws IOException {

        // Parse the payload
        GitWebhook gitWebhook = parseWebhookPayload(headers, payload, path);

        // Validate the payload hash
        boolean validPayloadHash =
                isValidPayloadHash(gitWebhook, payload, secret);

        // Validate the repository name
        boolean validRepositoryName = repo.equals(gitWebhook.getRepoName());

        // Validate the repository owner
        boolean validRepositoryOwner =
                owner.equals(gitWebhook.getRepoOwner());

        // Validate the repository branch
        boolean validBranch = branch.equals(gitWebhook.getRepoBranch());

        // Validate that at least one of the payload commits modifies
        // the relevant resource path
        boolean relevantCommitExists = !Strings
                .isNullOrEmpty(gitWebhook.getLatestRelevantCommitId());

        return validPayloadHash && validRepositoryName && validRepositoryOwner
                && validBranch && relevantCommitExists;

    }

    /**
     * Validate the payload hash in the GitHub webhook request header using the
     * secret token defined when setting up the webhook
     * 
     * @param gitWebhook
     * @param payload
     * @param secret
     * @return
     */

    private boolean isValidPayloadHash(GitWebhook gitWebhook,
            String payload, String secret) {

        String payloadHmacSha256 = Hashing.hmacSha256(secret.getBytes())
                .newHasher().putString(payload, StandardCharsets.UTF_8).hash()
                .toString();
        String signature = gitWebhook.getRequestHeaderSignature();
        return signature.equals(WEBHOOK_REQUEST_HEADER_SIGNATURE_VALUE_PREFIX
                .concat(payloadHmacSha256));

    }

    /**
     * Get the string contents of a file resource managed by a public GitHub
     * repository
     */

    @Override
    public ResponseEntity<String> getFile(String owner, String repo,
            String path, String branch) throws IOException {

        // Request the raw public resource
        ResponseEntity<String> response = webClient.get()
                .uri(REQUEST_URI, owner, repo, path, branch)
                .header("Accept", HEADER_ACCEPT).retrieve()
                .onStatus(status -> status.value() != HttpStatus.OK.value(),
                        clientResponse -> Mono.empty())
                .toEntity(String.class).block();

        // Throw an exception if the HTTP response status is not 200 OK
        if (response == null)
            throw new IOException("Null HTTP Response");
        if (response.getStatusCodeValue() != HttpStatus.OK.value())
            throw new IOException(
                    "Invalid HTTP Response " + response.getStatusCodeValue());

        // Return the HTTP response object
        return response;

    }

    /**
     * Get the string contents of a file resource managed by a private GitHub
     * repository
     */

    @Override
    public ResponseEntity<String> getFile(String token, String owner,
            String repo, String path, String branch) throws IOException {

        // Request the raw resource requiring authorisation
        ResponseEntity<String> response = webClient.get()
                .uri(REQUEST_URI, owner, repo, path, branch)
                .header("Accept", HEADER_ACCEPT)
                .header("Authorization",
                        HEADER_AUTHORIZATION_PREFIX + " " + token)
                .retrieve()
                .onStatus(status -> status.value() != HttpStatus.OK.value(),
                        clientResponse -> Mono.empty())
                .toEntity(String.class).block();

        // Throw an exception if the HTTP response status is not 200 OK
        if (response == null)
            throw new IOException("Null HTTP Response");
        if (response.getStatusCodeValue() != HttpStatus.OK.value())
            throw new IOException(
                    "Invalid HTTP Response " + response.getStatusCodeValue());

        // Return the HTTP response object
        return response;

    }

}
