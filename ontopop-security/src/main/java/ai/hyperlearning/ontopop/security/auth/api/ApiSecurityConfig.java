package ai.hyperlearning.ontopop.security.auth.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.security.auth.AuthenticationFilter;
import ai.hyperlearning.ontopop.security.auth.AuthorizationHeader;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthenticationEngine;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthentication;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthenticationFactory;

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
    
    private static final String PRINCIPAL_REQUEST_HEADER_KEY = "Authorization";
    private static final String ROLE_NAME_PREFIX = "ROLE_";
    private static final String MANAGEMENT_API_ROLE_NAME = "ONTOPOP_MANAGEMENT_API";
    private static final String TRIPLESTORE_API_ROLE_NAME = "ONTOPOP_TRIPLESTORE_API";
    private static final String SEARCH_API_ROLE_NAME = "ONTOPOP_SEARCH_API";
    private static final String GRAPH_API_ROLE_NAME = "ONTOPOP_GRAPH_API";
    private static final String MAPPING_API_ROLE_NAME = "ONTOPOP_MAPPING_API";
    
    @Autowired
    private ApiKeyAuthenticationFactory apiKeyAuthenticationFactory;
    
    @Value("${security.authentication.api.enabled:false}")
    private Boolean apiAuthenticationEnabled;
    
    @Value("${security.authentication.api.apiKeyLookup.enabled:false}")
    private Boolean apiKeyLookupEnabled;
    
    @Value("${security.authentication.api.apiKeyLookup.engine:secrets}")
    private String apiKeyLookupEngine;
    
    @Value("${security.authentication.api.apiKeyLookup.guestCredentials.enabled:false}")
    private Boolean apiKeyLookupGuestCredentialsEnabled;
    
    private ApiKeyAuthentication apiKeyAuthentication = null;
    
    @PostConstruct
    private void resolveApiAuthenticationEngines() {
        
        // API key lookup authentication engine
        if ( Boolean.TRUE.equals(apiAuthenticationEnabled) && 
                Boolean.TRUE.equals(apiKeyLookupEnabled)) {
            
            ApiKeyAuthenticationEngine apiAuthenticationEngineType =
                    ApiKeyAuthenticationEngine.valueOfLabel(
                            apiKeyLookupEngine.toUpperCase());
            apiKeyAuthentication = apiKeyAuthenticationFactory
                    .getApiKeyAuthenticationService(
                            apiAuthenticationEngineType);
            LOGGER.info("Using the '{}' API authentication engine.",
                    apiKeyLookupEngine);
            
        }
        
    }
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        if ( apiKeyAuthentication == null )
            resolveApiAuthenticationEngines();
        
        // Custom authentication - Get the contents of the authorization header
        AuthenticationFilter filter = new AuthenticationFilter(
                PRINCIPAL_REQUEST_HEADER_KEY);
        filter.setAuthenticationManager(new AuthenticationManager() {
            
            @Override
            public Authentication authenticate(Authentication authentication) 
                    throws AuthenticationException {
                
                // API Authentication
                if ( Boolean.TRUE.equals(apiAuthenticationEnabled) ) {
                    
                    // Get and decode the Base64 encoded credentials 
                    // from the Authorization header
                    String encodedPrincipal = (String) authentication
                            .getPrincipal();
                    String encodedCredentials = (String) authentication
                            .getCredentials();
                    String decodedPrincipal = decodeHeader(encodedPrincipal);
                    
                    // API Key Lookup Authentication
                    if ( Boolean.TRUE.equals(apiKeyLookupEnabled) ) {
                        
                        try {
                            
                            // Get the API Key from the Authorization header
                            String apiKey = getApiKey(decodedPrincipal);
                            
                            // Verify the API Key and get the granted authorities
                            String guestCredentials = Boolean.TRUE.equals(
                                    apiKeyLookupGuestCredentialsEnabled) ? 
                                            decodedPrincipal : null;
                            final List<GrantedAuthority> authorities = 
                                    apiKeyAuthentication.authorize(
                                            apiKey, guestCredentials);
                            
                            // Return a new authentication object
                            // Note that the UsernamePasswordAuthenticationToken 
                            // constructor automatically sets authenticated to true.
                            // Therefore we do not have to explicitly call 
                            // setAuthenticated(). If we do, then an illegal
                            // argument exception will be thrown.
                            return new UsernamePasswordAuthenticationToken(
                                        decodedPrincipal, encodedCredentials, 
                                        authorities);
                            
                        } catch (JsonProcessingException e) {
                            throw new BadCredentialsException(
                                    "Invalid Credentials.");
                        }  
                    
                    }
                    
                    // No authentication mechanism is enabled
                    else return new UsernamePasswordAuthenticationToken(
                            decodedPrincipal, encodedCredentials, 
                            grantAllAuthorities());
                    
                } else {
                    
                    // If API authentication is disabled entirely, 
                    // then grant all authorities automatically
                    return new UsernamePasswordAuthenticationToken(
                            null, null, grantAllAuthorities());
                    
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
                .antMatchers("/").permitAll()
                .antMatchers("/management/**").hasRole(MANAGEMENT_API_ROLE_NAME)
                .antMatchers("/triplestore/**").hasRole(TRIPLESTORE_API_ROLE_NAME)
                .antMatchers("/search/**").hasRole(SEARCH_API_ROLE_NAME)
                .antMatchers("/graph/**").hasRole(GRAPH_API_ROLE_NAME)
                .antMatchers("/mapping/**").hasRole(MAPPING_API_ROLE_NAME)
                .anyRequest().authenticated()
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(
                    (request, response, ex) ->
                        response.sendError(
                                HttpStatus.UNAUTHORIZED.value(), 
                                "Invalid Credentials.")
                    );
        
    }
    
    /**
     * CORS Configuration
     * @return
     */
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = 
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Decode a Base64 encoded request header
     * @param encodedPrincipal
     * @return
     */
    
    private String decodeHeader(String encodedPrincipal) {
        byte[] decodedPrincipalBytes = Base64.getDecoder()
                .decode(encodedPrincipal);
        return new String(decodedPrincipalBytes);
    }
    
    /**
     * Extract the API Key from the request header
     * @return
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    
    private String getApiKey(String decodedPrincipal) 
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES
                .mappedFeature());
        AuthorizationHeader authorizationHeader = 
                mapper.readValue(decodedPrincipal, 
                        AuthorizationHeader.class);
        return authorizationHeader.getAppId();
    }
    
    /**
     * Grant all authorities automatically
     * @return
     */
    
    private List<GrantedAuthority> grantAllAuthorities() {
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
        grantedAuthorities.add(new SimpleGrantedAuthority(
                ROLE_NAME_PREFIX + MAPPING_API_ROLE_NAME));
        return grantedAuthorities;
    }

}
