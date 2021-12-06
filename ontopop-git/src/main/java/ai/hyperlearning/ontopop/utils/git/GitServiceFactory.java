package ai.hyperlearning.ontopop.utils.git;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.utils.git.github.GitHubGitService;

/**
 * Git Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GitServiceFactory {
	
	@Autowired
	private GitHubGitService gitHubGitService;
	
	private static final String HTTP_HEADER_GITHUB_KEY = "x-github-delivery";
	
	/**
	 * Select the relevant Git service based on a given type string
	 * @param type
	 * @return
	 */
	
	public GitService getGitService(String type) {
		
		GitServiceType gitServiceType = GitServiceType
				.valueOf(type.toUpperCase());
		switch ( gitServiceType ) {
			case GITHUB:
				return gitHubGitService;
			default:
				return gitHubGitService;
		}
		
	}
	
	/**
	 * Select the relevant Git service based on the HTTP headers
	 * of a webhook event request
	 * @param headers
	 * @return
	 */
	
	public GitService getGitService(Map<String, String> headers) {
	
		if ( headers.containsKey(HTTP_HEADER_GITHUB_KEY) )
			return gitHubGitService;
		return gitHubGitService;
		
	}

}
