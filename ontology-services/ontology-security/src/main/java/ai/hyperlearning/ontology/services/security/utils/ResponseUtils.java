package ai.hyperlearning.ontology.services.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * HTTP Response Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class ResponseUtils {
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String MESSAGE_KEY = "message";
	
	/**
	 * Create a JSON response message
	 * @param message
	 * @return
	 */
	
	public static String createResponseMessage(String message) {
		ObjectNode objectNode = OBJECT_MAPPER.createObjectNode();
		objectNode.putPOJO(MESSAGE_KEY, message);
		try {
			return OBJECT_MAPPER.writeValueAsString(objectNode);
		} catch (JsonProcessingException e) {
			return message;
		}
	}

}
