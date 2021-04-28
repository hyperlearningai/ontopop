package ai.hyperlearning.ontology.services.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import io.jsonwebtoken.Claims;

/**
 * JSON Web Token Utility Method Unit Tests
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class TestJWTUtils {
	
	@SuppressWarnings("unused")
	@Test
    public void createAndDecodeJWT() {
		
		// Create a JWT
		String jwtId = "h938f23q47e3418db7e687123e3b2d78"; 		// Claims jti
        String jwtIssuer = "HyperLearning AI";					// Claims iss
        String jwtSubject = "jillur.quddus@hyperlearning.ai";	// Claims sub
        int jwtTimeToLive = 3600000;							// Claims exp
        String jwt = JWTUtils.createJWT(jwtSubject);
        System.out.println(jwt);
        
        // Decode the JWT
        Claims claims = JWTUtils.decodeJWT(jwt);
        System.out.println(claims.toString());
        
        // Verify the JWT 
        assertEquals(jwtId, claims.getId());
        assertEquals(jwtIssuer, claims.getIssuer());
        assertEquals(jwtSubject, claims.getSubject());
		
	}

}
