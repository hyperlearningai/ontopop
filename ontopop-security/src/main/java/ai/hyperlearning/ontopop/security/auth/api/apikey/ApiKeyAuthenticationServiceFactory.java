package ai.hyperlearning.ontopop.security.auth.api.apikey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.security.auth.api.apikey.engines.SecretsEngineApiKeyAuthenticationService;

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

    public ApiKeyAuthenticationService getApiKeyAuthenticationService(String engine) {

        ApiKeyAuthenticationServiceEngine apiKeyAuthenticationServiceEngine =
                ApiKeyAuthenticationServiceEngine.valueOfLabel(engine.toUpperCase());
        switch (apiKeyAuthenticationServiceEngine) {
            case SECRETS:
                return secretsEngineApiKeyAuthenticationService;
            default:
                return null;
        }

    }
    
    public ApiKeyAuthenticationService getApiKeyAuthenticationService(
            ApiKeyAuthenticationServiceEngine apiKeyAuthenticationServiceEngine) {

        switch (apiKeyAuthenticationServiceEngine) {
            case SECRETS:
                return secretsEngineApiKeyAuthenticationService;
            default:
                return null;
        }

    }

}
