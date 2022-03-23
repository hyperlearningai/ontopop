package ai.hyperlearning.ontopop.security.auth;

import java.util.Set;

import ai.hyperlearning.ontopop.security.auth.model.ApiKey;

/**
 * API Key Authentication Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface ApiKeyAuthenticationService {
    
    public ApiKey get(String key) throws Exception;
    
    public void create(ApiKey apiKey) throws Exception;
    
    public void create(String client) throws Exception;
    
    public void create(String client, Set<String> roles) throws Exception;
    
    public void create(String client, Set<String> roles, int durationDays) throws Exception;
    
    public void delete(String key) throws Exception;
    
    public boolean authenticate(String key) throws Exception;
    
    public boolean authenticate(ApiKey apiKey) throws Exception;

}
