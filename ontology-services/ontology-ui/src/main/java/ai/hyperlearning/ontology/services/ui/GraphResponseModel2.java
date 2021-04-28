package ai.hyperlearning.ontology.services.ui;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ai.hyperlearning.ontology.services.model.vocabulary.Synonym;
import ai.hyperlearning.ontology.services.model.vocabulary.Term;

/**
 * Graph Response Model 02 Transformations for Azure Search Indexing
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class GraphResponseModel2 implements IGraphResponseModel {
	
	private static final String AZURE_INDEX_DEFINITION_FILENAME = 
			"search-azure-search-index-schema.json";
	private static final Set<String> AZURE_INDEX_SCHEMA_FIELD_NAMES = 
			getIndexSchemaFieldNames();
	private static final String GRAPH_OBJECT_PROPERTY_KEY_ID = "id";
	private static final String PROPERTY_KEY_SEARCH_ACTION = 
			"@search.action";
	private static final String PROPERTY_KEY_VALUE_SEARCH_ACTION = 
			"upload";
	private static final List<String> PROPERTY_KEYS_TO_COLLECTIONIFY =
			Arrays.asList("entities", "synonyms", "related_terms");
	private static final Map<String, String> PROPERTY_KEYS_TO_RENAME = 
			Map.ofEntries(
					new AbstractMap.SimpleEntry<String, String>("objectPropertyRdfAbout", "rdfAbout"), 
					new AbstractMap.SimpleEntry<String, String>("objectPropertyRdfsLabel", "rdfsLabel"), 
					new AbstractMap.SimpleEntry<String, String>("sourceNodeId", "from"), 
					new AbstractMap.SimpleEntry<String, String>("targetNodeId", "to")
			);
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private List<Synonym> injectedSynonyms = null;
	private List<Term> injectedRelatedTerms = null;

	@Override
	public String transformVertexCentricGraphTraversalResult(
			List<Object> result) {
		try {
			ObjectNode value = MAPPER.createObjectNode();
			value.putPOJO("value", generateFlattenedGraphObjectArray(result));
			return MAPPER.writerWithDefaultPrettyPrinter()
					.writeValueAsString(value);
		} catch (Exception e) {
			return result.toString();
		}
	}
	
	public String transformVertexCentricGraphTraversalResult(
			List<Object> result, List<Synonym> synonyms, 
			List<Term> relatedTerms) {
		this.injectedSynonyms = synonyms;
		this.injectedRelatedTerms = relatedTerms;
		return transformVertexCentricGraphTraversalResult(result);
	}

	@Override
	public String transformEdgeCentricGraphTraversalResult(
			List<Object> result) {
		try {
			ObjectNode value = MAPPER.createObjectNode();
			value.putPOJO("value", generateFlattenedGraphObjectArray(result));
			return MAPPER.writerWithDefaultPrettyPrinter()
					.writeValueAsString(value);
		} catch (Exception e) {
			return result.toString();
		}
	}
	
	/**
	 * Generate a list of flattened graph objects
	 * @param resultList
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	
	private List<Map<String, Object>> generateFlattenedGraphObjectArray(
			List<Object> resultList) throws 
	JsonMappingException, JsonProcessingException {
		
		// Instantiate a list of flattened graph objects
		List<Map<String, Object>> graphObjectsList = 
				new ArrayList<Map<String, Object>>();
		
		// Parse the graph traversal result as a JSON array
		JsonNode rootArray = (JsonNode) MAPPER.readTree(
				MAPPER.writeValueAsString(resultList));
		
		// Iterate over all the JSON nodes in the JSON array
		for (JsonNode jsonNode : rootArray) {
			
			// Generate a flattened version of the current JSON node (value)
			Map<String, Object> flattenedJsonNode = flattenJsonNode(jsonNode);
			
			// Create a nodeId if it does not already exist
			if ( !flattenedJsonNode.containsKey("nodeId") )
				flattenedJsonNode.put("nodeId", 
						Integer.valueOf((String) flattenedJsonNode
								.get(GRAPH_OBJECT_PROPERTY_KEY_ID)));
			
			// Create injected synonyms if it does not already exist
			if ( !flattenedJsonNode.containsKey("synonyms") 
					&& this.injectedSynonyms != null ) {
				List<String> newSynonyms = new ArrayList<String>();
				this.injectedSynonyms.forEach(synonym -> newSynonyms
						.add(synonym.getSynonym()));
				flattenedJsonNode.put("synonyms",  newSynonyms);
			}	
			
			// Create injected related terms if it does not already exist
			if ( !flattenedJsonNode.containsKey("related_terms") 
					&& this.injectedRelatedTerms != null ) {
				List<String> newRelatedTerms = new ArrayList<String>();
				this.injectedRelatedTerms.forEach(relatedTerm -> newRelatedTerms
						.add(relatedTerm.getTerm()));
				flattenedJsonNode.put("related_terms", newRelatedTerms);
			}
			
			// Add the search index action as a new property key/value pair
			flattenedJsonNode.put(
					PROPERTY_KEY_SEARCH_ACTION, 
					PROPERTY_KEY_VALUE_SEARCH_ACTION);
			
			// Add the flattened graph object to the list of flattened objects
			graphObjectsList.add(flattenedJsonNode);
			
		}
		
		// Return the list
		return graphObjectsList;
		
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
				
			// Check if the property key is to be renamed
			String propertyKey = PROPERTY_KEYS_TO_RENAME.containsKey(
					entry.getKey()) ? PROPERTY_KEYS_TO_RENAME.get(
							entry.getKey()) : entry.getKey();
			
			// Lowercase and remove whitespace
			if ( propertyKey.contains(" ") )
				propertyKey = propertyKey
						.replaceAll(" ", "_")
						.toLowerCase()
						.strip();
				
			// Check if the property key is to be kept (
			if (AZURE_INDEX_SCHEMA_FIELD_NAMES.contains(propertyKey)) {
				
				// Add the key/value (non-node value) pair to the flattened map
				if (entry.getValue().isValueNode()) {
					
					Object propertyValue = null;
					
					// Convert a string list into a list object
					if ( PROPERTY_KEYS_TO_COLLECTIONIFY.contains(propertyKey) ) {
						
						// Get a cleansed string list
						String cleansedList = 
								String.valueOf(entry.getValue())
								.replace("[", "")
								.replace("]", "")
								.replaceAll("\"", "")
								.replaceAll(", ", ",")
								.strip();
						
						// Append the injected synonyms to the string list
						if ( propertyKey
								.equalsIgnoreCase("SYNONYMS")
								&& this.injectedSynonyms != null) {
							for ( Synonym synonym : this.injectedSynonyms ) {
								cleansedList = cleansedList 
										+ ", " + synonym.getSynonym().strip();
							}
						} 
						
						// Append the injected related terms to the string list
						else if ( propertyKey
								.equalsIgnoreCase("RELATED_TERMS")
								&& this.injectedRelatedTerms != null) {
							for ( Term term : this.injectedRelatedTerms ) {
								cleansedList = cleansedList 
										+ ", " + term.getTerm().strip();
							}
						}
							
						// Transform the string list into a list object
						propertyValue = Arrays.asList(
								StringUtils.splitPreserveAllTokens(
										cleansedList, ","));
						
					} else {
						
						// ID in Azure Search must be a STRING object
						propertyValue = propertyKey.equals(
								GRAPH_OBJECT_PROPERTY_KEY_ID) ? 
								String.valueOf(entry.getValue()) : 
									entry.getValue();
						
					}
					
					// Add the key/value pair to the flattened map
					flattenedMap.put(propertyKey, propertyValue);
					
				} else {
					
					// Recursively flatten collections
					Iterator<Map.Entry<String, JsonNode>> childFieldsIterator = 
							entry.getValue().fields();
					flattenJSONFields(flattenedMap, childFieldsIterator);
				
				}
				
			}
		}
		
	}
	
	/**
	 * Get the set of Azure search index schema field names
	 * @return
	 * @throws Exception
	 */
	
	private static Set<String> getIndexSchemaFieldNames() {
		
		Set<String> fieldNames = new LinkedHashSet<String>();
		
		// Get the set of index schema field names
		try {
			
			InputStream inputStream = GraphResponseModel2.class
					.getResourceAsStream("/" + AZURE_INDEX_DEFINITION_FILENAME);
			String indexDefinition = new String(
					inputStream.readAllBytes(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(indexDefinition);
			JsonNode fieldsArray = root.get("fields");
			for (JsonNode field : fieldsArray) {
				fieldNames.add(field.get("name").asText());
			}
			
		} catch (Exception e) {
			
		}
		
		return fieldNames;
		
	}

}
