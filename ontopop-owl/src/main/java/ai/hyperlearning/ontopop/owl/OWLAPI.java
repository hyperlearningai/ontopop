package ai.hyperlearning.ontopop.owl;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLAPIStreamUtils;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;

/**
 * OWL API Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OWLAPI {
	
	private static final OWLOntologyManager OWL_ONTOLOGY_MANAGER = 
			OWLManager.createOWLOntologyManager();
	private static final OWLDataFactory OWL_DATA_FACTORY = 
			OWL_ONTOLOGY_MANAGER.getOWLDataFactory();
	
	/**************************************************************************
	 * Loaders
	 *************************************************************************/

	/**
	 * Load an OWL ontology given an OWL file
	 * @param owlFile
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	
	public static OWLOntology loadOntology(File owlFile) 
			throws OWLOntologyCreationException {
		return OWL_ONTOLOGY_MANAGER
				.loadOntologyFromOntologyDocument(owlFile);
	}
	
	/**
	 * Load an OWL ontology given an input stream to an OWL file
	 * @param owlFile
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	
	public static OWLOntology loadOntology(InputStream owlFile) 
			throws OWLOntologyCreationException {
		return OWL_ONTOLOGY_MANAGER
				.loadOntologyFromOntologyDocument(owlFile);
	}
	
	/**************************************************************************
	 * Reasoners
	 *************************************************************************/
	
	/**
	 * Test whether a given OWL ontology is consistent using
	 * the HermiT reasoner
	 * @param ontology
	 * @return
	 */
	
	public static boolean isConsistent(OWLOntology ontology) {
		Configuration configuration = new Configuration();
		OWLReasoner reasoner = new Reasoner(configuration, ontology);
		return reasoner.isConsistent();
	}
	
	/**************************************************************************
	 * Annotations
	 *************************************************************************/
	
	/**
	 * Extract the literal value of an annotation value
	 * @param annotation
	 * @return
	 */
	
	public static String getAnnotationValueLiteral(
			OWLAnnotation annotation) {
		OWLAnnotationValue annotationValue = annotation.getValue();
		return ((OWLLiteral) annotationValue).getLiteral();
	}
	
	/**
	 * Extract the RDFS Label literal value from a 
	 * given list of OWL annotations
	 * @param annotations
	 * @return
	 */
	
	public static String getRDFSLabel(List<OWLAnnotation> annotations) {
		for (OWLAnnotation annotation : annotations) {
			if (annotation.getProperty().equals(
					OWL_DATA_FACTORY.getRDFSLabel())) {
				return getAnnotationValueLiteral(annotation);
			}
		}
		return null;
	}
	
	/**************************************************************************
	 * Annotation Properties
	 *************************************************************************/
	
	public static Set<OWLAnnotationProperty> getAnnotationProperties(
			OWLOntology ontology) {
		return ontology.getAnnotationPropertiesInSignature();
	}
	
	/**
	 * Parse the ontology and generate a map of Annotation Property IRI to
	 * OntoPop Simple Annotation Property objects
	 * @param ontology
	 * @return
	 */
	
	public static Map<String, SimpleAnnotationProperty> parseAnnotationProperties(
			OWLOntology ontology) {
		
		Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap = 
				new LinkedHashMap<>();
		
		// Iterate over all OWL annotation properties found in the OWL ontology
		Set<OWLAnnotationProperty> owlAnnotationProperties = 
				ontology.getAnnotationPropertiesInSignature();
		for ( OWLAnnotationProperty owlAnnotationProperty : 
				owlAnnotationProperties ) {
			
			// Get the OWL annotation property IRI
			String annotationPropertyIri = owlAnnotationProperty
					.getIRI().toString();
			
			// Extract the list of OWL annotations from this
			// OWL annotation property
			List<OWLAnnotation> owlAnnotations = 
					OWLAPIStreamUtils.asList(
							EntitySearcher.getAnnotations(
									owlAnnotationProperty, ontology));
			
			// Get the OWL annotation property RDFS Label
			String annotationPropertyRDFSLabel = getRDFSLabel(owlAnnotations);
			
			// Generate a map of annotation IRI to annotation literal value
			Map<String, String> annotations = new LinkedHashMap<>();
			for (OWLAnnotation owlAnnotation : owlAnnotations) {
				annotations.put(
						owlAnnotation.getProperty().getIRI().toString(), 
						getAnnotationValueLiteral(owlAnnotation));
			}
			
			// Create a Simple Annotation Property object
			SimpleAnnotationProperty simpleAnnotationProperty = 
					new SimpleAnnotationProperty(
							annotationPropertyIri, 
							annotationPropertyRDFSLabel, 
							annotations);
			simpleAnnotationPropertyMap.put(
					annotationPropertyIri, 
					simpleAnnotationProperty);
			
		}
		
		return simpleAnnotationPropertyMap;
		
	}
	
	/**************************************************************************
	 * Data Types
	 *************************************************************************/
	
	public static Set<OWLDatatype> getDatatypes(OWLOntology ontology) {
		return ontology.getDatatypesInSignature();
	}
	
	/**************************************************************************
	 * Data Properties
	 *************************************************************************/
	
	public static Set<OWLDataProperty> getDataProperties(
			OWLOntology ontology) {
		return ontology.getDataPropertiesInSignature();
	}
	
	/**************************************************************************
	 * Object Properties
	 *************************************************************************/
	
	/**
	 * Get all object properties
	 * @param ontology
	 * @return
	 */
	
	public static Set<OWLObjectProperty> getObjectProperties(
			OWLOntology ontology) {
		return ontology.getObjectPropertiesInSignature();
	}
	
	/**************************************************************************
	 * Individuals
	 *************************************************************************/
	
	public static Set<OWLNamedIndividual> getIndividuals(
			OWLOntology ontology) {
		return ontology.getIndividualsInSignature();
	}
	
	/**************************************************************************
	 * Classes
	 *************************************************************************/
	
	/**
	 * Get all classes
	 * @param ontology
	 * @return
	 */
	
	public static Set<OWLClass> getClasses(OWLOntology ontology) {
		return ontology.getClassesInSignature();
	}
	
}
