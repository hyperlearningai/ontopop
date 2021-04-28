package ai.hyperlearning.ontology.services.security.utils;

/**
 * HTTP Request Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RequestUtils {
	
	/**
	 * Get the username from the authorisation bearer token (if applicable)
	 * @param authorization
	 * @return
	 */
	
	public static String getUsernameFromAuthorizationHeader(
			String authorization) {
		return (authorization != null) ? 
				JWTUtils.getJWTSubject(
						JWTUtils.getJWTFromRequestHeaderAuthorization(
								authorization)) : null;
	}

}
