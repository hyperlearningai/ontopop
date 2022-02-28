package ai.hyperlearning.ontopop.security.auth;

import ai.hyperlearning.ontopop.security.auth.model.ApiKey;
import ai.hyperlearning.ontopop.security.auth.model.ApiKeys;

/**
 * API Key Authentication Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface ApiKeyAuthenticationService {
    
    public ApiKeys getApiKeys() throws Exception;
    
    public ApiKey get(String key) throws Exception;
    
    public void create(ApiKey apiKey) throws Exception;
    
    public void delete(String key) throws Exception;
    
    public void setEnabled(String key, boolean enabled) throws Exception;
    
    public boolean authenticate(String key) throws Exception;

}
