package ai.hyperlearning.ontopop.utils.git;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import ai.hyperlearning.ontopop.model.git.WebhookEvent;

/**
 * Git Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface GitService {
	
	WebhookEvent parseWebhookPayload(
			Map<String, String> headers, String payload);
	
	boolean isValidWebhookPayload(
			Map<String, String> headers, String payload, String secret);
	
	ResponseEntity<String> getFile(String owner, String repo, String path, 
			String branch) throws IOException;
	
	ResponseEntity<String> getFile(String token, String owner, String repo, 
			String path, String branch) throws IOException;

}
