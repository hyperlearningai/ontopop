package ai.hyperlearning.ontology.services.owlapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;

/**
 * OWL API Utility Methods Unit Tests
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class TestOWLAPIUtils {
	
	private static final String TEST_ONTOLOGY_FILENAME = "test-ontology.owl";
	private static final int TEST_ONTOLOGY_CLASS_COUNT = 196;
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

		// Close the OWL file input stream
		if ( ontologyInputStream != null ) {
			try {
				ontologyInputStream.close();
			} catch (IOException e) {
				
			}
		}
		
	}
	
	/**************************************************************************
	 * OWL General Utility Methods
	 *************************************************************************/
	
	@Test
	public void testLoadOntologyFromOwlFile() 
			throws OWLOntologyCreationException {
		assertFalse(ontology.isEmpty());
	}
	
	@Test
	public void testIsValid() 
			throws OWLOntologyCreationException {
		assertTrue(OWLAPIUtils.isValid(ontology));
	}
	
	@Test
	public void testGetAnnotations() 
			throws OWLOntologyCreationException {
		Set<OWLClass> classes = OWLAPIUtils.getOwlClasses(ontology);
		List<OWLAnnotation> annotations = OWLAPIUtils.getAnnotations(
				ontology, classes.iterator().next());
		assertFalse(annotations.isEmpty());
	}
	
	@Test
	public void testGetRdfsLabel() 
			throws OWLOntologyCreationException {
		Set<OWLClass> classes = OWLAPIUtils.getOwlClasses(ontology);
		String rdfsLabel = OWLAPIUtils.getRdfsLabel(OWLAPIUtils.getAnnotations(
				ontology, classes.iterator().next()));
		assertTrue(StringUtils.isNotBlank(rdfsLabel));
	}
	
	/**************************************************************************
	 * OWL Class Utility Methods
	 *************************************************************************/
	
	@Test
	public void testGetOwlClasses() 
			throws OWLOntologyCreationException {
		assertEquals(OWLAPIUtils.getOwlClasses(ontology).size(),
				TEST_ONTOLOGY_CLASS_COUNT);
	}
	
	@Test
	public void testGetClasses() throws 
	OWLOntologyCreationException, JAXBException {
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				OWLAPIUtils.getAnnotationProperties(ontologyInputStream);
		Map<String, RDFOwlObjectProperty> owlObjectPropertyMap = 
				OWLAPIUtils.getObjectProperties(
						ontology, owlAnnotationPropertyMap);
		Map<String, RDFOwlClass> owlClassMap = OWLAPIUtils.getClasses(
				ontology, owlAnnotationPropertyMap, owlObjectPropertyMap);
		assertFalse(owlClassMap.isEmpty());
	}
	
	/**************************************************************************
	 * OWL Annotation Properties Utility Methods
	 * @throws JAXBException 
	 *************************************************************************/
	
	@Test
	public void testGetAnnotationProperties() 
			throws OWLOntologyCreationException, JAXBException {
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				OWLAPIUtils.getAnnotationProperties(ontologyInputStream);
		assertFalse(owlAnnotationPropertyMap.isEmpty());
	}
	
	/**************************************************************************
	 * OWL Object Properties Utility Methods
	 * @throws OWLOntologyCreationException 
	 * @throws JAXBException 
	 *************************************************************************/
	
	@Test
	public void testGetObjectProperties() 
			throws OWLOntologyCreationException, JAXBException {
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				OWLAPIUtils.getAnnotationProperties(ontologyInputStream);
		Map<String, RDFOwlObjectProperty> owlObjectPropertyMap = 
				OWLAPIUtils.getObjectProperties(
						ontology, owlAnnotationPropertyMap);
		assertFalse(owlObjectPropertyMap.isEmpty());
	}

}
