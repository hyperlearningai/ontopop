package ai.hyperlearning.ontopop.security.auth.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Authorization Utilities
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class AuthorizationUtils {
    
    private static final String ROLE_NAME_PREFIX = "ROLE_";
    public static final String MAPPING_API_ROLE_NAME = "ONTOPOP_MAPPING_API";
    
    private AuthorizationUtils() {
        throw new IllegalStateException("The AuthorizationUtils utility class "
                + "cannot be instantiated.");
    }
    
    /**
     * Extract the token from the Authorization header
     * @return
     */
    public static String getToken(String principal) {
        return principal.replace("Bearer ", "");
    }
    
    /**
     * Grant all authorities automatically
     * @return
     */
    public static List<GrantedAuthority> grantAllAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = 
                new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(
                ROLE_NAME_PREFIX + MAPPING_API_ROLE_NAME));
        return grantedAuthorities;
    }
    
    /**
     * Get a list of granted authorities from a string of roles.
     * @param roles
     * @return
     */
    public static List<GrantedAuthority> getGrantedAuthorities(String roles) {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<String> authorities = Arrays.asList(roles.split("\\s*,\\s*"));
        for (String authority : authorities) {
            grantedAuthorities.add(
                    new SimpleGrantedAuthority(authority.toUpperCase()));
        }
        return grantedAuthorities;
    }

}
