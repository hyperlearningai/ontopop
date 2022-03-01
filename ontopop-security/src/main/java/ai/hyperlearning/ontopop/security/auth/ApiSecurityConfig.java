package ai.hyperlearning.ontopop.security.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import ai.hyperlearning.ontopop.security.auth.model.ApiKey;

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
    private static final String ROLE_NAME_PREFIX = "ROLE_";
    private static final String MANAGEMENT_API_ROLE_NAME = "ONTOPOP_MANAGEMENT_API";
    private static final String TRIPLESTORE_API_ROLE_NAME = "ONTOPOP_TRIPLESTORE_API";
    private static final String SEARCH_API_ROLE_NAME = "ONTOPOP_SEARCH_API";
    private static final String GRAPH_API_ROLE_NAME = "ONTOPOP_GRAPH_API";
    
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
        LOGGER.info("Using the {} API Key authentication service.",
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
                String credentials = (String) authentication.getCredentials();
                if ( Boolean.TRUE.equals(apiAuthenticationEnabled) ) {
                    
                    // Check that the API Key exists
                    if ( principal == null )
                        throw new BadCredentialsException("No API Key provided.");
                    
                    // Authenticate this API Key
                    boolean authenticated = false;
                    ApiKey apiKey = null;
                    try {
                        apiKey = apiKeyAuthenticationService.get(principal);
                        if ( apiKey != null )
                            authenticated = apiKeyAuthenticationService
                                    .authenticate(apiKey);
                    } catch (Exception e) {
                        LOGGER.error("Error encountered when authenticating the "
                                + "API Key.", e);
                    }
                    
                    // Invalid API Key
                    if ( !authenticated )
                        throw new BadCredentialsException("Invalid API Key.");
                    
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
                        
                        // Return a new authentication object
                        // Note that the UsernamePasswordAuthenticationToken 
                        // constructor automatically sets authenticated to true.
                        // Therefore we do not have to explicitly call 
                        // setAuthenticated(). If we do, then an illegal
                        // argument exception will be thrown.
                        return new UsernamePasswordAuthenticationToken(
                                principal, credentials, grantedAuthorities);
                        
                    }
                    
                } else {
                    
                    // If API authentication is disabled, then grant all
                    // authorities automatically
                    final List<GrantedAuthority> grantedAuthorities = 
                            new ArrayList<>();
                    grantedAuthorities.add(new SimpleGrantedAuthority(
                            ROLE_NAME_PREFIX + MANAGEMENT_API_ROLE_NAME));
                    grantedAuthorities.add(new SimpleGrantedAuthority(
                            ROLE_NAME_PREFIX + TRIPLESTORE_API_ROLE_NAME));
                    grantedAuthorities.add(new SimpleGrantedAuthority(
                            ROLE_NAME_PREFIX + SEARCH_API_ROLE_NAME));
                    grantedAuthorities.add(new SimpleGrantedAuthority(
                            ROLE_NAME_PREFIX + GRAPH_API_ROLE_NAME));
                    return new UsernamePasswordAuthenticationToken(
                            principal, credentials, grantedAuthorities);
                    
                }  
                
            }
            
        });
        
        // Define protected URIs and session policy
        httpSecurity
            .cors()
                .and()
            .csrf()
                .disable()
            .addFilter(filter)
            .authorizeRequests()
                .antMatchers("/management/**").hasRole(MANAGEMENT_API_ROLE_NAME)
                .antMatchers("/triplestore/**").hasRole(TRIPLESTORE_API_ROLE_NAME)
                .antMatchers("/search/**").hasRole(SEARCH_API_ROLE_NAME)
                .antMatchers("/graph/**").hasRole(GRAPH_API_ROLE_NAME)
                .anyRequest().authenticated()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(
                    (request, response, ex) -> {
                        response.sendError(
                                HttpStatus.UNAUTHORIZED.value(), 
                                "Invalid API Key.");
                    });
        
    }

}
