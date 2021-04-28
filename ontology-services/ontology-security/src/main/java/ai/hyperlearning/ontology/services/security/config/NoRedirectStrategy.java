package ai.hyperlearning.ontology.services.security.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

/**
 * Redirect Strategy
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class NoRedirectStrategy implements RedirectStrategy {
	
	@Override
	public void sendRedirect(final HttpServletRequest request, 
			final HttpServletResponse response, final String url) 
					throws IOException {
		// Do not redirect (for our REST API)
		// Instead a HTTP 401 (unauthorized) will be returned
	}

}
