package ai.hyperlearning.ontology.services.security.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Simple File-based Authentication Provider (DEVELOPMENT ONLY)
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Component
public class FileAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) 
			throws AuthenticationException {
		
		String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
        	UserDetails userDetail = userDetailsService
        			.loadUserByUsername(username);
        	UsernamePasswordAuthenticationToken token = 
        			new UsernamePasswordAuthenticationToken(
        					userDetail, userDetail.getPassword(), 
        					userDetail.getAuthorities());
        	return token;
        } else {
        	return null;
        }
	
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication
				.equals(UsernamePasswordAuthenticationToken.class);
	}

}
