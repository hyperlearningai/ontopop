package ai.hyperlearning.ontopop.security.auth;

import org.apache.commons.lang3.StringUtils;

import ai.hyperlearning.ontopop.exceptions.security.InvalidClientNameException;
import ai.hyperlearning.ontopop.security.auth.model.ApiKey;

/**
 * API Key Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class ApiKeyUtils {
    
    private static final String DEFAULT_CLIENT_NAME = "ontopop";
    
    private ApiKeyUtils() {
        throw new IllegalStateException("The ApiKeyUtils utility class "
                + "cannot be instantiated.");
    }
    
    /**
     * Get the client name from the API Key.
     * Spring will inject UsernamePasswordAuthenticationToken into the 
     * Principal interface, enabling us to retrieve the API Key from the
     * principal object via the calling controller.
     * @param apiAuthenticationEnabled
     * @param apiKeyAuthenticationService
     * @param principalApiKey
     * @param requestParameterClient
     * @return
     */
    
    public static String getClientName(Boolean apiAuthenticationEnabled, 
            ApiKeyAuthenticationService apiKeyAuthenticationService, 
            String principalApiKey, String requestParameterClient) {
        
        // If API key authentication is enabled then get the client name
        // from the API Key. 
        if ( Boolean.TRUE.equals(apiAuthenticationEnabled) ) {
            try {
                ApiKey apiKey = apiKeyAuthenticationService
                        .get(principalApiKey);
                return apiKey.getClient();
            } catch (Exception e) {
                return DEFAULT_CLIENT_NAME;
            }
        }
        
        // If API key authentication is NOT enabled, then the client request 
        // parameter MUST be provided
        else {
            if ( !StringUtils.isAllBlank(requestParameterClient) )
                return requestParameterClient;
            else throw new InvalidClientNameException();
        }
        
    }

}
