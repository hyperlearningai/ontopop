package ai.hyperlearning.ontology.services.utils;

import java.math.BigInteger;

/**
 * Encoding Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class EncodingUtils {
	
	/**
	 * Encode a given string from binary to base 10
	 * @param text
	 * @return
	 */
	
	public static BigInteger encodeString(String text) {
		return new BigInteger(text.getBytes());
	}
	
	/**
	 * Decode a given encoding from base 10 to binary
	 * @param encoding
	 * @return
	 */
	
	public static String decodeString(BigInteger encoding) {
		return new String(encoding.toByteArray());
	}

}
