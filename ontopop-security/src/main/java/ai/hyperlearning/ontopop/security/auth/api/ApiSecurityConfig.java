package ai.hyperlearning.ontopop.security.auth.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import ai.hyperlearning.ontopop.security.auth.AuthenticationFilter;
import ai.hyperlearning.ontopop.security.auth.utils.AuthorizationUtils;
import ai.hyperlearning.ontopop.security.auth.utils.JWTUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

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
    
    @Value("${security.authentication.api.enabled:false}")
    private Boolean apiAuthenticationEnabled;
    
    @Value("${security.authentication.api.jwt.secretKey}")
    private String jwtSecretKey;
    
    @Value("${security.authentication.api.jwt.username}")
    private String jwtUsername;
    
    @Value("${security.authentication.api.jwt.issuer}")
    private String jwtIssuer;
    
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        
        // Custom authentication - get the contents of the authorization header
        AuthenticationFilter filter = new AuthenticationFilter(
                PRINCIPAL_REQUEST_HEADER_KEY);
        filter.setAuthenticationManager(new AuthenticationManager() {
            
            @Override
            public Authentication authenticate(Authentication authentication) 
                    throws AuthenticationException {
                
                // API Authentication
                if ( Boolean.TRUE.equals(apiAuthenticationEnabled) ) {
                    
                    // Get the Authorization header
                    String principal = (String) 
                            authentication.getPrincipal();
                    String credentials = (String) 
                            authentication.getCredentials();
                    
                    // If the Authorization header is empty then
                    // throw a BadCredentialsException
                    if ( StringUtils.isBlank(principal) ) {
                        LOGGER.error("Missing JWT.");
                        throw new BadCredentialsException(
                                "Missing JWT.");
                    }
                    
                    // JWT verification
                    try {
                        
                        // Get the JWT from the Authorization header
                        String token = AuthorizationUtils.getToken(principal);
                        LOGGER.debug("JWT: {}", token);
                        
                        // Decode and verify the JWT
                        Jws<Claims> claims = JWTUtils.decodeJWT(
                                token, jwtSecretKey, jwtUsername, jwtIssuer);
                        
                        // Get the granted authorities
                        String roles = JWTUtils.getRoles(claims);
                        final List<GrantedAuthority> authorities = 
                                AuthorizationUtils.getGrantedAuthorities(roles);
                        
                        // Return a new authentication object
                        // Note that the UsernamePasswordAuthenticationToken 
                        // constructor automatically sets authenticated to true.
                        // Therefore we do not have to explicitly call 
                        // setAuthenticated(). If we do, then an illegal
                        // argument exception will be thrown.
                        return new UsernamePasswordAuthenticationToken(
                                principal, credentials, authorities);
                        
                        
                    } catch (Exception e) {
                        LOGGER.error("Invalid JWT.");
                        throw new BadCredentialsException(
                                "Invalid JWT.");
                    }
                    
                } else {
                    
                    // If API authentication is disabled entirely, 
                    // then grant all authorities automatically
                    return new UsernamePasswordAuthenticationToken(null, null, 
                            AuthorizationUtils.grantAllAuthorities());
                    
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
                .antMatchers("/mapping/**").hasRole(
                        AuthorizationUtils.MAPPING_API_ROLE_NAME)
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
                                "Invalid credentials.")
                    );
        
    }

}
