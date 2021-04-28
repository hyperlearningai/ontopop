package ai.hyperlearning.ontology.services.graphdb.utils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gremlin Traversal Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class GremlinTraversalUnits {
	
	public static final Map<String, String> PROPERTY_KEYS_TO_SPLIT = 
			Map.ofEntries(
			  new AbstractMap.SimpleEntry<String, String>("BUSINESS AREA", "\\s+"),
			  new AbstractMap.SimpleEntry<String, String>("SUBDOMAIN", "\\s+")
			);
	
	/**
	 * Convert a list of unique property key values to a set
	 * where each unique property key value is delimited by whitespace
	 * @param propertyKey
	 * @param uniquePropertyValues
	 * @return
	 */
	
	public static Object nodesPropertyKeyUniqueValuesToSet(
			String propertyKey, List<Object> uniquePropertyValues) {
		if ( PROPERTY_KEYS_TO_SPLIT.containsKey(propertyKey.toUpperCase()) ) {
			Set<String> uniqueValues = new HashSet<String>();
			for (Object propertyValue : uniquePropertyValues) {
				uniqueValues.addAll(
						new HashSet<String>(Arrays.asList(
								propertyValue.toString()
								.replaceAll(",\\s*", " ").split(
										PROPERTY_KEYS_TO_SPLIT.get(
												propertyKey.toUpperCase())))));
			}
			return uniqueValues;
		} else
			return uniquePropertyValues;
	}

}
