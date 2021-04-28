package ai.hyperlearning.ontology.services.security.utils;

import static org.apache.commons.lang3.StringUtils.removeStart;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JSON Web Token Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class JWTUtils {
	
	// MVP DEVELOPMENT ONLY!!!
	private static final String SECRET_KEY = "<SECRET KEY>";
	private static final String JWT_ID = "<JWT ID>";
	private static final String JWT_ISSUER = "<JWT ISSUER>";
	private static final long JWT_TTL_MILLIS = 60 * 60 * 1000;
	private static final String JWT_HEADER = "<JWT HEADER>";
	
	// Base64 Decoder
	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String BEARER = "Bearer";
	
	/**
	 * Create a JWT as a URL-safe string
	 * @param id
	 * @param issuer
	 * @param subject
	 * @param ttlMillis
	 * @return
	 */
	
	public static String createJWT(String subject) {

        // Instantiate HMAC using SHA-256 Signing Algorithm 
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // Sign the JWT with the Secret Key
        byte[] apiKeySecretBytes = DatatypeConverter
        		.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(
        		apiKeySecretBytes, signatureAlgorithm.getJcaName());

        // Instantiate a JWT Builder
        JwtBuilder builder = Jwts.builder().setId(JWT_ID)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(JWT_ISSUER)
                .signWith(signatureAlgorithm, signingKey);

        // Add Expiration if TTL is greater than zero
        if (JWT_TTL_MILLIS >= 0) {
            long expMillis = nowMillis + JWT_TTL_MILLIS;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        
        // Build and return a URL-safe JWT string
        return builder.compact();
    
	}
	
	/**
	 * Get the payload and signature from a JWT string
	 * @param jwt
	 * @return
	 */
	
	public static String getJWTPayloadAndSignature(String jwt) {
		String[] jwtSections = jwt.split("\\.");
		return jwtSections[1] + "." + jwtSections[2];
	}
	
	/**
	 * Get the payload and signature from a JWT string and return 
	 * as a JSON object
	 * @param jwt
	 * @return
	 * @throws JsonProcessingException
	 */
	
	public static String getJWTPayloadAndSignatureAsJson(String jwt) 
			throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(
				objectMapper.createObjectNode()
				.putPOJO("token", getJWTPayloadAndSignature(jwt)));
	}
	
	/**
	 * Decode a given URL-safe JWT string
	 * @param jwt
	 * @return
	 */
	
	public static Claims decodeJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
	
	/**
	 * Verify a given JWT string
	 * @param jwt
	 * @return
	 */
	
	public static boolean isVerifiedJWT(String jwt) {
		try {
			Claims claims = decodeJWT(appendHeaderToJWT(jwt));
			if ( claims.getId().equals(JWT_ID) 
					&& claims.getIssuer().equals(JWT_ISSUER))
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Check whether a given JWT has expired
	 * @param jwt
	 * @return
	 */
	
	public static boolean isExpired(String jwt) {
		try {
			decodeJWT(appendHeaderToJWT(jwt));
		} catch (ExpiredJwtException e) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get the subject from a given JWT string
	 * @param jwt
	 * @return
	 */
	
	public static String getJWTSubject(String jwt) {
		Claims claims = decodeJWT(appendHeaderToJWT(jwt));
		return claims.getSubject();
	}
	
	/**
	 * Get the subject from a given expired JWT string
	 * @param jwt
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	
	@SuppressWarnings("unchecked")
	public static String getJWTSubjectFromExpiredToken(String jwt) 
			throws JsonMappingException, JsonProcessingException {
		String payload = appendHeaderToJWT(jwt).split("\\.")[1];
		String decodedPayload = new String(DECODER.decode(payload));
		Map<String, String> decodedPayloadMap = OBJECT_MAPPER
				.readValue(decodedPayload, Map.class); 
		return decodedPayloadMap.get("sub");
	}
	
	/**
	 * Append the header to a JWT string if required
	 * @param jwt
	 * @return
	 */
	
	private static String appendHeaderToJWT(String jwt) {
		int jwtSectionsCount = StringUtils.countOccurrencesOf(jwt, ".");
		return (jwtSectionsCount == 1) ? JWT_HEADER + "." + jwt : jwt;
	}
	
	/**
	 * Get the JWT string from the bearer token from a given 
	 * request header authorization
	 * @param authorization
	 * @return
	 */
	
	public static String getJWTFromRequestHeaderAuthorization(
			String authorization) {
		return removeStart(authorization, BEARER).trim();
	}
	
}
