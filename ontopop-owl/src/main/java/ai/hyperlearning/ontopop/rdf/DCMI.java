package ai.hyperlearning.ontopop.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.owl.OWLAPI;

/**
 * Dublin Core Metadata Initiative (DCMI) Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class DCMI {
	
	private static final String DCMI_RDF_SCHEMA_FILENAME = 
			"dublin_core_elements.rdf";
	private static final String DC_ELEMENTS_DESCRIPTION_IRI = 
			"http://purl.org/dc/elements/1.1/description";
	private static final String DC_ELEMENTS_DESCRIPTION_IRI_LABEL = 
			"Description";
	
	/**
	 * Read the DCMI RDF schema and return an OWL 2 ontology representation
	 * @return
	 * @throws OWLOntologyCreationException
	 * @throws IOException
	 */
	
	public static OWLOntology loadDcmiRdfSchema() 
			throws OWLOntologyCreationException, IOException {
		ClassLoader classLoader = 
				RDFSchema.class.getClassLoader();
		try (InputStream inputStream = 
				classLoader.getResourceAsStream(DCMI_RDF_SCHEMA_FILENAME)) {
			return OWLAPI.loadOntology(inputStream);
		}
	}
	
	/**
	 * Parse the DCMI RDF schema and generate a map of Annotation Property IRI to
	 * OntoPop Simple Annotation Property objects
	 * @param dcmiRdfSchema
	 * @return
	 */
	
	public static Map<String, SimpleAnnotationProperty> parseAnnotationProperties() {
		
		Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap = 
				new LinkedHashMap<>();
		
		// TO-DO: Parse dublin_core_elements.rdf
		
		// Explicitly create a Simple Annotation Property object for
		// http://purl.org/dc/elements/1.1/description
		SimpleAnnotationProperty dcmiElementDescription = 
				new SimpleAnnotationProperty(
						DC_ELEMENTS_DESCRIPTION_IRI, 
						DC_ELEMENTS_DESCRIPTION_IRI_LABEL, 
						new LinkedHashMap<>());
		simpleAnnotationPropertyMap.put(
				DC_ELEMENTS_DESCRIPTION_IRI, dcmiElementDescription);
		
		return simpleAnnotationPropertyMap;
		
	}

}
