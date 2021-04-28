package ai.hyperlearning.ontology.services.owlapi;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;

/**
 * OWL to JSON Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class OWLJSONUtils {

	private static final ObjectMapper mapper = new ObjectMapper()
			.enable(SerializationFeature.INDENT_OUTPUT);
	private static final String JSON_PARENT_OWL_ANNOTATION_PROPERTIES = 
			"OwlAnnotationProperties";
	private static final String JSON_PARENT_OWL_OBJECT_PROPERTIES = 
			"OwlObjectProperties";
	private static final String JSON_PARENT_OWL_CLASSES = 
			"OwlClasses";
	
	/**
	 * Convert the map of custom RDFOwlAnnotationProperty POJOs to JSON
	 * @param owlAnnotationPropertyMap
	 * @return
	 * @throws JsonProcessingException
	 */
	
	public static String rdfOwlAnnotationPropertiesToJson(
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap) 
					throws JsonProcessingException {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put(JSON_PARENT_OWL_ANNOTATION_PROPERTIES, 
				owlAnnotationPropertyMap);
		return mapper.writeValueAsString(jsonMap);
	}
	
	/**
	 * Convert the map of custom RDFOwlObjectProperty POJOs to JSON
	 * @param owlObjectPropertyMap
	 * @return
	 * @throws JsonProcessingException
	 */
	
	public static String rdfOwlObjectPropertiesToJson(
			Map<String, RDFOwlObjectProperty> owlObjectPropertyMap) 
					throws JsonProcessingException {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put(JSON_PARENT_OWL_OBJECT_PROPERTIES, 
				owlObjectPropertyMap);
		return mapper.writeValueAsString(jsonMap);
	}
	
	/**
	 * Convert the map of custom RDFOwlClass POJOs to JSON
	 * @param owlClassMap
	 * @return
	 * @throws JsonProcessingException
	 */
	
	public static String rdfOwlClassesToJson(
			Map<String, RDFOwlClass> owlClassMap) 
					throws JsonProcessingException {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put(JSON_PARENT_OWL_CLASSES, owlClassMap);
		return mapper.writeValueAsString(jsonMap);
	}
	
}
