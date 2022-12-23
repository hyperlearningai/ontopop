package ai.hyperlearning.ontopop.security.auth.utils;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

/**
 * JWT Utilities
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class JWTUtils {
    
    private JWTUtils() {
        throw new IllegalStateException("The JWTUtils utility class "
                + "cannot be instantiated.");
    }
    
    /**
     * Decode a given JWT signed with HMAC.
     * If the secret is invalid, the token has expired or the 
     * required claim assertions cannot be verified then this
     * method will throw an exception.
     * @param token
     * @param secretKey
     * @param username
     * @param issuer
     * @return
     */
    
    public static Jws<Claims> decodeJWT(String token, String secretKey, 
            String username, String issuer) throws 
        ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, 
        SignatureException, InvalidClaimException, MissingClaimException, 
        IncorrectClaimException, IllegalArgumentException {
        Key hmacKey = new SecretKeySpec(secretKey.getBytes(),
                SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parserBuilder()
                .require("userName", username)
                .requireIssuer(issuer)
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token);
    }
    
    /**
     * Get the authorized roles from the JWT
     * @param claims
     * @return
     */
    public static String getRoles(Jws<Claims> claims) {
        return claims.getBody().get("roles", String.class);
    }

}
