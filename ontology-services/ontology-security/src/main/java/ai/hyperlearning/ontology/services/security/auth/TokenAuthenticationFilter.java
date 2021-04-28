package ai.hyperlearning.ontology.services.security.auth;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import ai.hyperlearning.ontology.services.security.model.User;
import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.removeStart;

/**
 * Extract the authentication token from request headers
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public final class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	
	private static final String AUTHORIZATION = "Authorization";
	private static final String BEARER = "Bearer";
	
	public TokenAuthenticationFilter(final RequestMatcher requiresAuth) {
		super(requiresAuth);
	}
	
	@Override
	public Authentication attemptAuthentication(
			final HttpServletRequest request, 
			final HttpServletResponse response) 
					throws AuthenticationException {
		
		// Get the JWT token from the Authorization header
		final String param = ofNullable(request.getHeader(AUTHORIZATION))
			      .orElse(request.getParameter("token"));
	    final String token = ofNullable(param)
	      .map(value -> removeStart(value, BEARER))
	      .map(String::trim)
	      .orElseThrow(() -> new BadCredentialsException(
	    		  "Missing Authentication Token"));
	    
	    // Token verification
	    try {
	    	
	    	// Verify the token
		    boolean isVerifiedToken = JWTUtils.isVerifiedJWT(token);
		    
		    // Get the username (subject) from the token
		    String username = JWTUtils.getJWTSubject(token);
		    
		    if ( isVerifiedToken && username != null) {
			    
		    	// Manually set the authenticated principal
		    	List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		    	UserDetails userDetails = new User(username, null);
		    	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
			    		new UsernamePasswordAuthenticationToken(
			    				userDetails, null, grantedAuthorities);
		    	usernamePasswordAuthenticationToken.setDetails(
	                    new WebAuthenticationDetailsSource()
	                    .buildDetails(request));
		    	SecurityContextHolder.getContext().setAuthentication(
		    			usernamePasswordAuthenticationToken);
			    return usernamePasswordAuthenticationToken;
		    	
		    }
	    	
	    } catch (MalformedJwtException e) {
	    	throw new BadCredentialsException("Invalid Authentication Token");
	    } catch (ExpiredJwtException e) {
	    	throw new BadCredentialsException("Expired Authentication Token");
	    } catch (SignatureException e) {
	    	throw new BadCredentialsException("Invalid Authentication Token");
	    } catch (ClaimJwtException e) {
	    	throw new BadCredentialsException("Invalid Authentication Token");
	    } catch (JwtException e) {
	    	throw new BadCredentialsException("Invalid Authentication Token");
	    } catch (Exception e) {
	    	throw new BadCredentialsException("Invalid Authentication");
	    }
	    
	    return null;
	    
	}
	
	@Override
	protected void successfulAuthentication(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final FilterChain chain,
			final Authentication authResult) 
					throws IOException, ServletException {
	    super.successfulAuthentication(request, response, chain, authResult);
	    chain.doFilter(request, response);
	}

}
