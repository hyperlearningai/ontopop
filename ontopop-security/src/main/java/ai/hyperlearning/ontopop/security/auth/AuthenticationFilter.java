package ai.hyperlearning.ontopop.security.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

/**
 * API Key Authentication Filter
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    
    private String principalRequestHeader;
    
    public ApiKeyAuthenticationFilter(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principalRequestHeader);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(
            HttpServletRequest request) {
        return null;
    }

}
