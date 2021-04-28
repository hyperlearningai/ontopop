package ai.hyperlearning.ontology.services.ui;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;

/**
 * Graph Response Model 01 Transformations
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class GraphResponseModel1 implements IGraphResponseModel {
	
	private static final String GRAPH_OBJECT_PROPERTY_KEY_ID = "id";
	private static final List<String> PROPERTY_KEYS_TO_REMOVE = 
			Arrays.asList("NULL");
	private static final Map<String, String> PROPERTY_KEYS_TO_RENAME = 
			Map.ofEntries(
					new AbstractMap.SimpleEntry<String, String>("objectPropertyRdfAbout", "rdfAbout"), 
					new AbstractMap.SimpleEntry<String, String>("objectPropertyRdfsLabel", "rdfsLabel"), 
					new AbstractMap.SimpleEntry<String, String>("sourceNodeId", "from"), 
					new AbstractMap.SimpleEntry<String, String>("targetNodeId", "to")
			);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * Write the edge-centric graph traversal result as a flattened
	 * JSON string with no child objects
	 */
	
	@Override
	public String transformVertexCentricGraphTraversalResult(
			List<Object> result) {
		try {
			return MAPPER.writeValueAsString(
					generateIdToFlattenedGraphObjectMap(result));
		} catch (Exception e) {
			return result.toString();
		}
	}

	/**
	 * Write the edge-centric graph traversal result as a flattened
	 * JSON string with no child objects
	 */
	
	@Override
	public String transformEdgeCentricGraphTraversalResult(
			List<Object> result) {
		try {
			return MAPPER.writeValueAsString(
					generateIdToFlattenedGraphObjectMap(result));
		} catch (Exception e) {
			return result.toString();
		}
	}
	
	/**
	 * Generate a map between the graph object ID (key)
	 * and the graph object itself (value)
	 * @param resultList
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	
	private Map<Integer, Map<String, Object>> generateIdToFlattenedGraphObjectMap(
			List<Object> resultList) throws 
				JsonMappingException, JsonProcessingException {
		
		// Instantiate a map between the graph object id (key)
		// and the graph object (value)
		Map<Integer, Map<String, Object>> map = 
				new LinkedHashMap<Integer, Map<String, Object>>();
		
		// Parse the graph traversal result as a JSON array
		JsonNode rootArray = (JsonNode) MAPPER.readTree(
				MAPPER.writeValueAsString(resultList));
		
		// Iterate over all the JSON nodes in the JSON array
		for (JsonNode jsonNode : rootArray) {
			
			// Generate a flattened version of the current JSON node (value)
			Map<String, Object> flattenedJsonNode = 
					flattenJsonNode(jsonNode);
			
			// Extract the key (id) from the flattened value map
			int id = ((IntNode) flattenedJsonNode.get(
					GRAPH_OBJECT_PROPERTY_KEY_ID)).asInt();
			
			// Add the graph object ID (key) and the flattened JSON node (value)
			// to the map
			map.put(id, flattenedJsonNode);
			
		}
		
		// Return the map
		return map;
		
	}
	
	/**
	 * Recursively flatten a given JSON node
	 * @param jsonNode
	 * @return
	 */
	
	private Map<String, Object> flattenJsonNode(JsonNode jsonNode) {
		
		// Instantiate a map to hold the flattened key/value pairs
		// for this JSON node
		Map<String, Object> flattenedMap = 
				new LinkedHashMap<String, Object>();
		
		// Get all the key/value pairs at the root level
		Iterator<Map.Entry<String, JsonNode>> fieldsIterator = 
				jsonNode.fields();
		
		// Recursively flatten any keys with child nodes
		flattenJSONFields(flattenedMap, fieldsIterator);
		
		// Return the flattened JSON node
		return flattenedMap;
		
	}
	
	/**
	 * Recursively flatten all keys with child JSON node values
	 * @param flattenedMap
	 * @param fieldsIterator
	 */
	
	private void flattenJSONFields(Map<String, Object> flattenedMap, 
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator) {
		
		while (fieldsIterator.hasNext()) {
			
			// Get the current key/value pair
			Map.Entry<String, JsonNode> entry = fieldsIterator.next();
			
			// Check if the property key is to be removed
			if (!PROPERTY_KEYS_TO_REMOVE.contains(entry.getKey())) {
				
				// Check if the property key is to be renamed
				String propertyKey = PROPERTY_KEYS_TO_RENAME.containsKey(
						entry.getKey()) ? PROPERTY_KEYS_TO_RENAME.get(
								entry.getKey()) : entry.getKey();
				
				// Add the key/value (non-node value) pair to the flattened map
				if (entry.getValue().isValueNode())
					flattenedMap.put(propertyKey, entry.getValue());
				else {
					Iterator<Map.Entry<String, JsonNode>> childFieldsIterator = 
							entry.getValue().fields();
					flattenJSONFields(flattenedMap, childFieldsIterator);
				}
				
			}
		}
		
	}

}
