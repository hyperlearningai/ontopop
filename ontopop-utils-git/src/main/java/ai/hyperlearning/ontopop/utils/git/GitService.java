package ai.hyperlearning.ontopop.utils.git;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

/**
 * Git Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface GitService {
	
	ResponseEntity<String> getFile(String owner, String repo, String path, 
			String branch) throws IOException;
	
	ResponseEntity<String> getFile(String token, String owner, String repo, 
			String path, String branch) throws IOException;

}
