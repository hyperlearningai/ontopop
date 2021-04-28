package ai.hyperlearning.ontology.services.security.auth;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontology.services.security.model.User;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;

/**
 * Open LDAP Authentication Provider
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Component
public class OpenLDAPAuthenticationProvider implements AuthenticationProvider {
	
	private LdapTemplate ldapTemplate;
	
	@Autowired
    private LdapContextSource contextSource;
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@PostConstruct
    private void initContext() {
        contextSource.setUrl(
        		globalProperties.getSpringLdapEmbeddedUrl() + "/" 
        				+ globalProperties.getSpringLdapEmbeddedUserDn() + ","
        				+ globalProperties.getSpringLdapEmbeddedBaseDn());
        contextSource.setAnonymousReadOnly(true);
        contextSource.setUserDn(
        		globalProperties.getSpringLdapEmbeddedUserDn() + ",");
        contextSource.setAnonymousReadOnly(true);
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(contextSource);
    }
	
	@Override
    public Authentication authenticate(Authentication authentication) 
    		throws AuthenticationException {

		Filter filter = new EqualsFilter("uid", authentication.getName());
        Boolean authenticate = ldapTemplate.authenticate(
        		LdapUtils.emptyLdapName(), filter.encode(),
                authentication.getCredentials().toString());
        if (authenticate) {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            UserDetails userDetails = new User(authentication.getName(), 
            		authentication.getCredentials().toString());
            Authentication auth = new UsernamePasswordAuthenticationToken(
            		userDetails, 
            		authentication.getCredentials().toString(),
            		grantedAuthorities);
            return auth;

        } else {
            return null;
        }
        
    }
	
	@Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
