package ai.hyperlearning.ontology.services.security.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import ai.hyperlearning.ontology.services.security.utils.ResponseUtils;

/**
 * Custom Authentication Failure Handler
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	
	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException exception) 
					throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.getOutputStream().println(
				ResponseUtils.createResponseMessage(exception.getMessage()));
	}

}
