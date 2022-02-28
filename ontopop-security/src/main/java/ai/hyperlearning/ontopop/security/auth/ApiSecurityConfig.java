package ai.hyperlearning.ontopop.security.auth;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * API Web Security Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@EnableWebSecurity
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ApiSecurityConfig.class);
    
    private static final String PRINCIPAL_REQUEST_HEADER_KEY = "X-API-Key";
    
    @Autowired
    private ApiKeyAuthenticationServiceFactory apiKeyAuthenticationServiceFactory;
    
    @Value("${security.authentication.api.enabled:false}")
    private Boolean apiAuthenticationEnabled;
    
    @Value("${security.authentication.api.engine:secrets}")
    private String apiAuthenticationEngine;
    
    private ApiKeyAuthenticationService apiKeyAuthenticationService = null;
    
    @PostConstruct
    private void resolveApiKeyAuthenticationService() {
        ApiKeyAuthenticationServiceType apiKeyAuthenticationServiceType =
                ApiKeyAuthenticationServiceType
                        .valueOfLabel(apiAuthenticationEngine.toUpperCase());
        apiKeyAuthenticationService = apiKeyAuthenticationServiceFactory
                .getApiKeyAuthenticationService(apiKeyAuthenticationServiceType);
        LOGGER.debug("Using the {} API Key authentication service.",
                apiKeyAuthenticationServiceType);
    }
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        if ( apiKeyAuthenticationService == null )
            resolveApiKeyAuthenticationService();
        
        // Custom authentication - API Key authentication
        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(
                PRINCIPAL_REQUEST_HEADER_KEY);
        filter.setAuthenticationManager(new AuthenticationManager() {
            
            @Override
            public Authentication authenticate(Authentication authentication) 
                    throws AuthenticationException {
                
                // Get the API Key from the header
                String principal = (String) authentication.getPrincipal();
                if ( Boolean.TRUE.equals(apiAuthenticationEnabled) ) {
                    
                    // Check that the API Key exists
                    if ( principal == null )
                        throw new BadCredentialsException("No API Key provided.");
                    
                    // Authenticate this API Key
                    boolean authenticated = false;
                    try {
                        authenticated = apiKeyAuthenticationService
                                .authenticate(principal);
                        if ( !authenticated )
                            throw new BadCredentialsException("Invalid API Key.");
                    } catch (Exception e) {
                        LOGGER.error("Error encountered when authenticating the "
                                + "API Key.", e);
                    }
                    
                    // Set the authentication response
                    authentication.setAuthenticated(authenticated);
                    return authentication;
                    
                } else {
                    
                    authentication.setAuthenticated(true);
                    return authentication;
                    
                }  
                
            }
            
        });
        
        // Define protected URIs and session policy
        httpSecurity
            .requestMatchers()
                .antMatchers(
                        "/management/**", 
                        "/triplestore/**", 
                        "/search/**", 
                        "/graph/**")
                .and()
            .csrf()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(
                    (request, response, ex) -> {
                        response.sendError(
                                HttpStatus.UNAUTHORIZED.value(), 
                                "Invalid API Key.");
                    })
            .and()
            .addFilter(filter)
            .authorizeRequests()
            .anyRequest()
            .authenticated();
        
    }

}
