package ai.hyperlearning.ontopop.utils.git.github;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
	private WebClient client;
	
	public GitHubGitService() {
		this.client = WebClient.builder()
				.baseUrl(BASE_URL)
				.build();
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
