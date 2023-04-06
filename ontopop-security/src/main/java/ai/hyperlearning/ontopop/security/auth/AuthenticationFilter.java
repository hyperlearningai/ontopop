package ai.hyperlearning.ontopop.security.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * Authentication Request Header Filter
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class AuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    
    private String principalRequestHeader;
    
    public AuthenticationFilter(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principalRequestHeader) != null ? 
        		request.getHeader(principalRequestHeader) : "";
    }

    @Override
    protected Object getPreAuthenticatedCredentials(
            HttpServletRequest request) {
        return null;
    }

}
