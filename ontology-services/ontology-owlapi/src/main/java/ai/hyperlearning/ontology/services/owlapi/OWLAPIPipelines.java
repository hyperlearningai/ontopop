package ai.hyperlearning.ontology.services.owlapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;

import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;

/**
 * OWL API Data Processing Pipelines
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class OWLAPIPipelines {
	
	/**
	 * Pipeline to load and parse a local OWL file 
	 * @param ontologyOwlFile
	 * @return
	 * @throws OWLOntologyCreationException
	 * @throws InconsistentOntologyException
	 * @throws JAXBException
	 */
	
	public static ParsedRDFOwlOntology parseRdfOwlOntology(
			String ontologyOwlFilename) throws OWLOntologyCreationException, 
	InconsistentOntologyException, JAXBException {
		
		// Load the static ontology OWL file into an OWLAPI ontology object
		InputStream ontologyOwlFile = OWLAPIPipelines.class
				.getResourceAsStream("/" + ontologyOwlFilename);
		OWLOntology ontology = OWLAPIUtils
				.loadOntologyFromOwlFile(ontologyOwlFile);
		if (ontologyOwlFile != null) {
			try {
				ontologyOwlFile.close();
			} catch (IOException e1) {
				
			}
		}
		
		// Semantically validate the ontology
		if ( OWLAPIUtils.isValid(ontology) ) {
			
			// Parse and extract all the annotation properties
			ontologyOwlFile = OWLAPIPipelines.class
					.getResourceAsStream("/" + ontologyOwlFilename);
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
					OWLAPIUtils.getAnnotationProperties(ontologyOwlFile);
			if (ontologyOwlFile != null) {
				try {
					ontologyOwlFile.close();
				} catch (IOException e1) {
					
				}
			}
			
			// Parse and extract all the object properties
			Map<String, RDFOwlObjectProperty> owlObjectPropertyMap = 
					OWLAPIUtils.getObjectProperties(
							ontology, owlAnnotationPropertyMap);
			
			// Parse and extract all the classes
			Map<String, RDFOwlClass> owlClassMap = OWLAPIUtils.getClasses(
					ontology, owlAnnotationPropertyMap, owlObjectPropertyMap);
			
			// Return a parsed ontology object
			ParsedRDFOwlOntology parsedRDFOwlOntology = 
					new ParsedRDFOwlOntology(
							owlAnnotationPropertyMap, 
							owlObjectPropertyMap, 
							owlClassMap);
			return parsedRDFOwlOntology;
			
		} else {
			throw new InconsistentOntologyException(
					"Semantically invalid ontology");
		}
		
	}

}
