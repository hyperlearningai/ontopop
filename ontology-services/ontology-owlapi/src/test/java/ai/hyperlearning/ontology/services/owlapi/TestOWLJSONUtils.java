package ai.hyperlearning.ontology.services.owlapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;

/**
 * OWL JSON Utility Methods Unit Tests
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class TestOWLJSONUtils {
	
	private static final String TEST_ONTOLOGY_FILENAME = "test-ontology.owl";
	private InputStream ontologyInputStream = null;
	private OWLOntology ontology = null;
	
	@BeforeEach
	public void loadOntology() throws OWLOntologyCreationException {
		ClassLoader classLoader = TestOWLAPIUtils.class.getClassLoader();
		ontology = OWLAPIUtils
				.loadOntologyFromOwlFile(new File(classLoader
						.getResource(TEST_ONTOLOGY_FILENAME).getFile()));
		ontologyInputStream = classLoader
				.getResourceAsStream(TEST_ONTOLOGY_FILENAME);
	}
	
	@AfterEach
	public void closeResources() {
		if (ontologyInputStream != null) {
			try {
				ontologyInputStream.close();
			} catch (IOException e) {
				
			}
		}
	}
	
	@Test
	public void testRdfOwlAnnotationPropertiesToJson() 
			throws JAXBException, JsonProcessingException {
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				OWLAPIUtils.getAnnotationProperties(ontologyInputStream);
		String json = OWLJSONUtils.rdfOwlAnnotationPropertiesToJson(
				owlAnnotationPropertyMap);
		assertFalse(json.isEmpty());
		assertNotEquals("", json);
	}
	
	@Test
	public void testRdfOwlObjectPropertiesToJson() 
			throws OWLOntologyCreationException, JAXBException, 
			JsonProcessingException {
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				OWLAPIUtils.getAnnotationProperties(ontologyInputStream);
		Map<String, RDFOwlObjectProperty> owlObjectPropertyMap = 
				OWLAPIUtils.getObjectProperties(
						ontology, owlAnnotationPropertyMap);
		String json = OWLJSONUtils.rdfOwlObjectPropertiesToJson(
				owlObjectPropertyMap);
		assertFalse(json.isEmpty());
		assertNotEquals("", json);
	}
	
	@Test
	public void testRdfOwlClassesToJson() 
			throws OWLOntologyCreationException, JAXBException, 
			JsonProcessingException {
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				OWLAPIUtils.getAnnotationProperties(ontologyInputStream);
		Map<String, RDFOwlObjectProperty> owlObjectPropertyMap = 
				OWLAPIUtils.getObjectProperties(
						ontology, owlAnnotationPropertyMap);
		Map<String, RDFOwlClass> owlClassMap = OWLAPIUtils.getClasses(
				ontology, owlAnnotationPropertyMap, owlObjectPropertyMap);
		String json = OWLJSONUtils.rdfOwlClassesToJson(owlClassMap);
		assertFalse(json.isEmpty());
		assertNotEquals("", json);
	}
	
}
