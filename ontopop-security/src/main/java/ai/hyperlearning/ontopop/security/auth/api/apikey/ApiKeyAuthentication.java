package ai.hyperlearning.ontopop.security.auth.api.apikey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ai.hyperlearning.ontopop.security.auth.api.apikey.model.ApiKey;

/**
 * API Key Authentication Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public abstract class ApiKeyAuthentication {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ApiKeyAuthentication.class);
    
    public abstract ApiKey get(String key) throws Exception;
    
    public abstract ApiKey get(String temporaryCredentials, String key) 
            throws Exception;
    
    public abstract void create(ApiKey apiKey) throws Exception;
    
    public abstract void create(String client) throws Exception;
    
    public abstract void create(String client, Set<String> roles) 
            throws Exception;
    
    public abstract void create(String client, Set<String> roles,
            int durationDays) throws Exception;
    
    public abstract void delete(String key) throws Exception;
    
    public abstract boolean authenticate(String key) throws Exception;
    
    public abstract boolean authenticate(ApiKey apiKey) throws Exception;
    
    /**
     * Verify that a given API Key exists in the configured persistence engine. 
     * If it exists, then return the list of authorities provisioned to 
     * this API Key.
     * 
     * @param headerApiKey
     * @return
     */
    
    public List<GrantedAuthority> authorize(String headerApiKey, 
            String guestCredentials) {
        
        // Check that the API Key exists
        if ( StringUtils.isBlank(headerApiKey) )
            throw new BadCredentialsException("No API Key provided.");
        
        // Authenticate this API Key
        boolean authenticated = false;
        ApiKey apiKey = null;
        try {
            apiKey = guestCredentials == null ? get(headerApiKey) : 
                get(guestCredentials, headerApiKey);
            if ( apiKey != null )
                authenticated = authenticate(apiKey);
        } catch (Exception e) {
            LOGGER.error("Error encountered when authenticating the "
                    + "API Key.", e);
        }
        
        // Invalid API Key
        if ( !authenticated )
            throw new BadCredentialsException("Invalid Credentials.");
        
        // Valid API Key
        else {
            
            // Set authorities
            final List<GrantedAuthority> grantedAuthorities = 
                    new ArrayList<>();
            Set<String> apiKeyRoles = apiKey.getRoles();
            if ( !apiKeyRoles.isEmpty() ) {
                for ( String apiKeyRole : apiKeyRoles ) {
                    grantedAuthorities.add(
                            new SimpleGrantedAuthority(
                                    apiKeyRole.toUpperCase()));
                }
            }
            return grantedAuthorities;
            
        }
        
    }

}
