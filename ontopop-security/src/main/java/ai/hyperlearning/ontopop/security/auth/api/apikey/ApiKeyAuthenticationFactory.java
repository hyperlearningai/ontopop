package ai.hyperlearning.ontopop.security.auth.api.apikey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.security.auth.api.apikey.engines.SecretsEngineApiKeyAuthentication;

/**
 * API Key Authentication Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class ApiKeyAuthenticationFactory {
    
    @Autowired(required = false)
    private SecretsEngineApiKeyAuthentication secretsEngineApiKeyAuthentication;
    
    /**
     * Select the relevant API Key Authentication service
     * 
     * @param type
     * @return
     */

    public ApiKeyAuthentication getApiKeyAuthenticationService(String engine) {

        ApiKeyAuthenticationEngine apiKeyAuthenticationServiceEngine =
                ApiKeyAuthenticationEngine.valueOfLabel(engine.toUpperCase());
        switch (apiKeyAuthenticationServiceEngine) {
            case SECRETS:
                return secretsEngineApiKeyAuthentication;
            default:
                return null;
        }

    }
    
    public ApiKeyAuthentication getApiKeyAuthenticationService(
            ApiKeyAuthenticationEngine apiKeyAuthenticationServiceEngine) {

        switch (apiKeyAuthenticationServiceEngine) {
            case SECRETS:
                return secretsEngineApiKeyAuthentication;
            default:
                return null;
        }

    }

}
