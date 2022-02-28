package ai.hyperlearning.ontopop.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.security.auth.engines.SecretsEngineApiKeyAuthenticationService;

/**
 * API Key Authentication Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class ApiKeyAuthenticationServiceFactory {
    
    @Autowired(required = false)
    private SecretsEngineApiKeyAuthenticationService secretsEngineApiKeyAuthenticationService;
    
    /**
     * Select the relevant API Key Authentication service
     * 
     * @param type
     * @return
     */

    public ApiKeyAuthenticationService getApiKeyAuthenticationService(String type) {

        ApiKeyAuthenticationServiceType apiKeyAuthenticationServiceType =
                ApiKeyAuthenticationServiceType.valueOfLabel(type.toUpperCase());
        switch (apiKeyAuthenticationServiceType) {
            case SECRETS:
                return secretsEngineApiKeyAuthenticationService;
            default:
                return null;
        }

    }
    
    public ApiKeyAuthenticationService getApiKeyAuthenticationService(
            ApiKeyAuthenticationServiceType apiKeyAuthenticationServiceType) {

        switch (apiKeyAuthenticationServiceType) {
            case SECRETS:
                return secretsEngineApiKeyAuthenticationService;
            default:
                return null;
        }

    }

}
