package ai.hyperlearning.ontopop.utils.git.github;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import ai.hyperlearning.ontopop.utils.git.GitService;
import reactor.core.publisher.Mono;

/**
 * GitHub Git Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GitHubGitService implements GitService  {

	private static final String BASE_URL = "https://api.github.com";
	private static final String REQUEST_URI = "/repos/{owner}/{repo}/contents/{path}?ref={branch}";
	private static final String HEADER_ACCEPT = "application/vnd.github.v3.raw+json";
	private static final String HEADER_AUTHORIZATION_PREFIX = "token";
	private static final String WEBHOOK_REQUEST_HEADER_SIGNATURE_KEY = "x-hub-signature-256";
	private static final String WEBHOOK_REQUEST_HEADER_SIGNATURE_VALUE_PREFIX = "sha256=";
	private WebClient client;
	
	public GitHubGitService() {
		this.client = WebClient.builder()
				.baseUrl(BASE_URL)
				.build();
	}
	
	@Override
	public WebhookEvent parseWebhookPayload(
			Map<String, String> headers, String payload) {
		
		JsonObject jsonPayload = new Gson().fromJson(payload, JsonObject.class);
		String ref = jsonPayload.get("ref").getAsString();
		
		
		
		
		String repoUrl;
		long repoId;
		String repoName;
		String repoOwner;
		String repoResourcePath;
		String repoBranch;
		
		String pusherName;
		String pusherEmail;
		
		String latestRelevantCommitId;
		LocalDateTime latestRelevantCommitTimestamp;
		String latestRelevantCommitAuthorName;
		String latestRelevantCommitAuthorEmail;
		String latestRelevantCommitAuthorUsername;
		String latestRelevantCommitCommitterName;
		String latestRelevantCommitCommitterEmail;
		String latestRelevantCommitCommitterUsername;
		String requestHeaderSignature = headers.get(WEBHOOK_REQUEST_HEADER_SIGNATURE_KEY);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private boolean isValidPayloadHash(Map<String, String> headers, 
			String payload, String secret) {
		
		// Validate the payload hash in the GitHub webhook request header
		// using the secret token defined when setting up the webhook
		String payloadHmacSha256 = Hashing.hmacSha256(secret.getBytes())
				.newHasher()
				.putString(payload, StandardCharsets.UTF_8)
				.hash()
				.toString();
		String signature = headers.get(WEBHOOK_REQUEST_HEADER_SIGNATURE_KEY);
		return signature.equals(
				WEBHOOK_REQUEST_HEADER_SIGNATURE_VALUE_PREFIX.concat(
						payloadHmacSha256));
		
	}
	
	private boolean isValidPayloadRepositoryName(
			JsonObject payload, String repo) {
		return repo.equals(payload
				.getAsJsonObject("repository")
				.get("name")
				.getAsString());
	}
	
	private boolean isValidPayloadBranch(
			JsonObject payload, String branch) {
		String ref = payload.get("ref").getAsString();
		return branch.equals(ref.substring(ref.lastIndexOf("/") + 1));
	}
	
	private boolean payloadCommitsContainsPathResource(
			JsonObject payload, String path) {
		
		// Validate that at least one of the payload commits modifies
		// the relevant resource path
		JsonArray commits = payload.getAsJsonArray("commits");
		for (JsonElement commit : commits) {
			JsonObject commitObject = commit.getAsJsonObject();
			JsonArray modifications = commitObject.getAsJsonArray("modified");
			for (JsonElement modification : modifications) {
				String modifiedResource = modification.getAsString();
				if ( modifiedResource.equals(path) )
					return true;
			}
		}
		
		return false;
		
	}
	
	@Override
	public boolean isValidWebhookPayload(
			Map<String, String> headers, String payload, String secret) {
		
		
		
	}

	@Override
	public ResponseEntity<String> getFile(String owner, String repo, 
			String path, String branch) throws IOException {

		// Request the raw public resource
		ResponseEntity<String> response = client.get()
			.uri(REQUEST_URI, owner, repo, path, branch)
			.header("Accept", HEADER_ACCEPT)
			.retrieve()
			.onStatus(
					status -> status.value() != 200,
			        clientResponse -> Mono.empty())
			.toEntity(String.class)
			.block();
		
		// Throw an exception if the HTTP response status is not 200 OK
		if ( response == null )
			throw new IOException(
					"Null HTTP Response");
		if ( response.getStatusCodeValue() != 200 )
			throw new IOException(
					"Invalid HTTP Response " + response.getStatusCodeValue());
		
		// Return the HTTP response object
		return response;
		
	}

	@Override
	public ResponseEntity<String> getFile(String token, String owner, 
			String repo, String path, String branch) throws IOException {
		
		// Request the raw resource requiring authorisation
		ResponseEntity<String> response = client.get()
			.uri(REQUEST_URI, owner, repo, path, branch)
			.header("Accept", HEADER_ACCEPT)
			.header("Authorization", HEADER_AUTHORIZATION_PREFIX + " " + token)
			.retrieve()
			.onStatus(
					status -> status.value() != 200,
			        clientResponse -> Mono.empty())
			.toEntity(String.class)
			.block();
		
		// Throw an exception if the HTTP response status is not 200 OK
		if ( response == null )
			throw new IOException(
					"Null HTTP Response");
		if ( response.getStatusCodeValue() != 200 )
			throw new IOException(
					"Invalid HTTP Response " + response.getStatusCodeValue());
		
		// Return the HTTP response object
		return response;
		
	}

}
