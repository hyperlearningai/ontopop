package ai.hyperlearning.ontopop.utils.git;

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
	
	/**
	 * Select the relevant git service
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

}
