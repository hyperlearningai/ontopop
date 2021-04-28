package ai.hyperlearning.ontology.services.owlapi;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLAPIStreamUtils;

import ai.hyperlearning.ontology.services.model.ontology.RDF;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlRestriction;
import ai.hyperlearning.ontology.services.model.ontology.RDFSSubClassOf;

/**
 * OWL API Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class OWLAPIUtils {
	
	private static final OWLOntologyManager OWL_ONTOLOGY_MANAGER = 
			OWLManager.createOWLOntologyManager();
	private static final OWLDataFactory OWL_DATA_FACTORY = 
			OWL_ONTOLOGY_MANAGER.getOWLDataFactory();
	private static final Pattern PATTERN_SUBOBJECTPROPERTYOF_IRI_EXTRACTOR = 
			Pattern.compile("<(.*?)>", Pattern.DOTALL);
	private static final Pattern PATTERN_SUBCLASSOF_IRI_EXTRACTOR = 
			Pattern.compile("<(.*?)>", Pattern.DOTALL);
	
	/**************************************************************************
	 * OWL General Utility Methods
	 *************************************************************************/
	
	/**
	 * Load an OWL-compliant ontology given an .owl file
	 * @param owlFile
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	
	public static OWLOntology loadOntologyFromOwlFile(File owlFile) 
			throws OWLOntologyCreationException {
		OWLOntology ontology = OWL_ONTOLOGY_MANAGER
				.loadOntologyFromOntologyDocument(owlFile);
		return ontology;
	}
	
	/**
	 * Load an OWL-compliant ontology given an .owl inputstream
	 * @param owlFile
	 * @return
	 * @throws OWLOntologyCreationException
	 */
	
	public static OWLOntology loadOntologyFromOwlFile(InputStream owlFile) 
			throws OWLOntologyCreationException {
		OWLOntology ontology = OWL_ONTOLOGY_MANAGER
				.loadOntologyFromOntologyDocument(owlFile);
		return ontology;
	}
	
	/**
	 * Validate the consistency of a given OWL-compliant ontology
	 * using the HermiT reasoner
	 * @param ontology
	 * @return
	 */
	
	public static boolean isValid(OWLOntology ontology) {
		Configuration configuration = new Configuration();
		OWLReasoner reasoner = new Reasoner(configuration, ontology);
		return reasoner.isConsistent();
	}
	
	/**
	 * Extract a list of referencing axioms from a given OWL entity
	 * @param ontology
	 * @param owlEntity
	 * @return
	 */
	
	public static List<OWLAxiom> getReferencingAxioms(
			OWLOntology ontology, OWLEntity owlEntity) {
		List<OWLAxiom> owlReferencingAxioms = OWLAPIStreamUtils.asList(
				EntitySearcher.getReferencingAxioms(owlEntity, ontology));
		return owlReferencingAxioms;
	}
	
	/**
	 * Extract a list of annotations from a given OWL entity e.g. OWL Class
	 * @param ontology
	 * @param owlEntity
	 * @return
	 */
	
	public static List<OWLAnnotation> getAnnotations(
			OWLOntology ontology, OWLEntity owlEntity) {
		List<OWLAnnotation> owlAnnotationsList = OWLAPIStreamUtils.asList(
				EntitySearcher.getAnnotations(owlEntity, ontology));
		return owlAnnotationsList;
	}
	
	/**
	 * Extract an annotation value given a list of OWL entity annotations
	 * and a given IRI string
	 * @param annotations
	 * @param IRI
	 * @return
	 */
	
	public static String getAnnotationValueByIRI(
			List<OWLAnnotation> annotations, String IRI) {
		String annotationValue = null;
		for (OWLAnnotation annotation : annotations) {
			if (annotation.getProperty().getIRI().getIRIString()
					.equals(IRI)) {
				annotationValue = getAnnotationValue(annotation);
				break;
			}
		}
		return annotationValue;
	}
	
	/**
	 * Get the value of an annotation
	 * @param annotation
	 * @return
	 */
	
	public static String getAnnotationValue(OWLAnnotation annotation) {
		OWLAnnotationValue annotationValue = annotation.getValue();
		return ((OWLLiteral) annotationValue).getLiteral();
	}
	
	/**
	 * Get the IRI of an annotation
	 * @param annotation
	 * @return
	 */
	
	public static String getAnnotationIRI(OWLAnnotation annotation) {
		return annotation.getProperty().getIRI().getIRIString();
	}
	
	/**
	 * Extract the RDFS Label given a list of OWL entity annotations
	 * @param annotations
	 * @return
	 */
	
	public static String getRdfsLabel(List<OWLAnnotation> annotations) {
		String rdfsLabel = null;
		for (OWLAnnotation annotation : annotations) {
			if (annotation.getProperty().equals(
					OWL_DATA_FACTORY.getRDFSLabel())) {
				rdfsLabel = getAnnotationValue(annotation);
				break; 
			}
		}
		return rdfsLabel;
	}
	
	/**
	 * Extract the RDF About identifier given an OWL entity
	 * @param owlEntity
	 * @return
	 */
	
	public static String getRdfAbout(OWLEntity owlEntity) {
		return owlEntity.getSignature().iterator().next().toStringID();
	}
	
	/**************************************************************************
	 * OWL Class Utility Methods
	 *************************************************************************/
	
	/**
	 * Extract the set of OWL classes
	 * @param ontology
	 * @return
	 */
	
	public static Set<OWLClass> getOwlClasses(OWLOntology ontology) {
		Set<OWLClass> classes = new LinkedHashSet<OWLClass>();
		ontology.classesInSignature().forEach(classes::add);
		return classes;
	}
	
	/**
	 * Extract a map of all OWL classes that have been defined in a 
	 * given ontology
	 * @param ontology
	 * @param owlAnnotationPropertyMap
	 * @param owlObjectPropertyMap
	 * @return
	 */
	
	public static Map<String, RDFOwlClass> getClasses(
			OWLOntology ontology, 
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap, 
			Map<String, RDFOwlObjectProperty> owlObjectPropertyMap){
		
		// Instantiate a hash map of custom RDFOwlClass objects
		// This will map rdf:about (key) to RDFOwlClass (value)
		Map<String, RDFOwlClass> owlClassMap = 
				new LinkedHashMap<String, RDFOwlClass>();
		
		// Extract all classes using the OWL API
		Set<OWLClass> owlClasses = OWLAPIUtils.getOwlClasses(ontology);
		
		// Iterate over all classes and generate RDFOwlClass POJOs
		int nodeId = 1;
		for (OWLClass owlClass : owlClasses) {
			
			// Get the RDF About (IRI)
			String rdfAbout = OWLAPIUtils.getRdfAbout(owlClass);
			
			// Extract a set of RDFSSubClassOf POJOS for this class
			Set<RDFSSubClassOf> rdfsSubClassOf = OWLAPIUtils.
					getClassSubClassesOf(ontology, owlClass, rdfAbout, 
							nodeId, owlObjectPropertyMap);
			
			// Get the annotations for this class
			List<OWLAnnotation> annotations = OWLAPIUtils.getAnnotations(
					ontology, owlClass);
						
			// Get the RDFS Label
			String rdfsLabel = OWLAPIUtils.getRdfsLabel(annotations);
			
			// Instantiate a map of RDFOwlAnnotationProperty:rdfAbout (key) to
			// annotation values (value) to capture the other annotations
			// for this class
			Map<String, String> owlAnnotationProperties = 
					new LinkedHashMap<String, String>();
			
			// Iterate over all annotations
			for (OWLAnnotation annotation : annotations) {
				
				// If the annotation IRI is in the map of RDF annotation 
				// properties then create an annotation property object
				String annotationIRI = OWLAPIUtils.getAnnotationIRI(annotation);
				
				// Test whether the annotation IRI is in the map of RDF
				// annotation properties
				if (owlAnnotationPropertyMap.containsKey(annotationIRI)) {
					
					// Add the RDFOWLAnnotationProperty and its realised
					// value to the map of RDFOwlAnnotationProperty POJOs
					// for this object property
					owlAnnotationProperties.put(
							annotationIRI, 
							OWLAPIUtils.getAnnotationValue(annotation));
					
				}
				
			}
			
			// Create a new RDFOwlClass POJO
			RDFOwlClass rdfOwlClass = new RDFOwlClass();
			rdfOwlClass.setNodeId(nodeId);
			rdfOwlClass.setRdfAbout(rdfAbout);
			rdfOwlClass.setRdfsLabel(rdfsLabel);
			rdfOwlClass.setOwlAnnotationProperties(owlAnnotationProperties);
			rdfOwlClass.setRdfsSubClassOf(rdfsSubClassOf);
			
			// Add the new RDFOwlClass POJO to the map of custom 
			// RDFOwlClass objects
			owlClassMap.put(rdfAbout, rdfOwlClass);
			nodeId++;
			
		}
		
		return owlClassMap;
		
	}
	
	/**
	 * Extract a set of super classes for a given class
	 * @param ontology
	 * @param owlClass
	 * @param rdfAbout
	 * @return
	 */
	
	public static Set<RDFSSubClassOf> getClassSubClassesOf(
			OWLOntology ontology, OWLClass owlClass, 
			String rdfAbout, int owlClassId, 
			Map<String, RDFOwlObjectProperty> owlObjectPropertyMap) {
		
		// Instantiate a set of custom RDFSSubClassOf POJOS
		// to store the superclasses of this class
		Set<RDFSSubClassOf> rdfsSubClassesOf = 
				new LinkedHashSet<RDFSSubClassOf>();
		
		// Get a list of referencing axioms for this class
		List<OWLAxiom> axioms = OWLAPIUtils.getReferencingAxioms(
				ontology, owlClass);
		
		// Iterate over all the axioms
		int id = 1;
		for (OWLAxiom axiom : axioms) {
			
			// Test that the axiom type is SubClassOf
			if ( axiom.getAxiomType() == AxiomType.SUBCLASS_OF) {
				
				// Get the axiom signatures
				Set<OWLEntity> signatures = axiom.getSignature();
				
				// Get all unrestricted superclasses
				if (signatures.size() == 2) {
					
					// Extract the source and target class IRIs
					Matcher matcher = PATTERN_SUBCLASSOF_IRI_EXTRACTOR
							.matcher(axiom.toString());
					String sourceClassIRI = null;
					String targetClassIRI = null;
					int counter = 1;
					while (matcher.find()) {
						if (counter == 1)
							sourceClassIRI = matcher.group(1);
						else if (counter == 2) {
							targetClassIRI = matcher.group(1);
							break;
						}
						counter++;
					}
					
					// Get the unrestricted source (subclass) to 
					// target (superclass) relationship where the source is
					// equal to the IRI of the given class
					if (sourceClassIRI.equalsIgnoreCase(rdfAbout)) {
						
						// Create a new RDFSSubClassOf POJO
						RDFSSubClassOf rdfsSubClassOf = 
								new RDFSSubClassOf();
						rdfsSubClassOf.setClassRdfAbout(targetClassIRI);
						rdfsSubClassOf.setOwlRestriction(null);
						
						// Generate the RDFSSubClassOf ID
						int subclassOfId = Integer.parseInt(
								String.valueOf(owlClassId) 
								+ String.valueOf(id));
						rdfsSubClassOf.setEdgeId(subclassOfId);
						
						// Add the RDFSSubClassOf to the set of
						// RDFSSubClassOf POJOs
						rdfsSubClassesOf.add(rdfsSubClassOf);
						id++;
						
					}
					
				}
				
				// Get all restricted superclasses
				else if (signatures.size() == 3) {
					
					// Extract the source class, target class and
					// object property IRIs
					Matcher matcher = PATTERN_SUBCLASSOF_IRI_EXTRACTOR
							.matcher(axiom.toString());
					String sourceClassIRI = null;
					String targetClassIRI = null;
					String objectPropertyIRI = null;
					int counter = 1;
					while (matcher.find()) {
						if (counter == 1)
							sourceClassIRI = matcher.group(1);
						else if (counter == 2)
							objectPropertyIRI = matcher.group(1);
						else if (counter == 3) {
							targetClassIRI = matcher.group(1);
							break;
						}
						counter++;
					}
					
					// Get the restricted source (subclass) to 
					// target (superclass) relationship where the source is
					// equal to the IRI of the given class
					if (sourceClassIRI.equalsIgnoreCase(rdfAbout)) {
						
						// Create a new RDFSSubClassOf POJO
						RDFSSubClassOf rdfsSubClassOf = 
								new RDFSSubClassOf();
						rdfsSubClassOf.setClassRdfAbout(targetClassIRI);
						RDFOwlRestriction rdfOwlRestriction = 
								new RDFOwlRestriction();
						rdfOwlRestriction.setClassRdfAbout(targetClassIRI);
						rdfOwlRestriction.setObjectPropertyRdfAbout(
								objectPropertyIRI);
						rdfOwlRestriction.setObjectPropertyRdfsLabel(
								owlObjectPropertyMap.get(
										objectPropertyIRI).getRdfsLabel());
						rdfsSubClassOf.setOwlRestriction(rdfOwlRestriction);
						
						// Generate the RDFSSubClassOf ID
						int subclassOfId = Integer.parseInt(
								String.valueOf(owlClassId) 
								+ String.valueOf(id));
						rdfsSubClassOf.setEdgeId(subclassOfId);
						
						// Add the RDFSSubClassOf to the set of
						// RDFSSubClassOf POJOs
						rdfsSubClassesOf.add(rdfsSubClassOf);
						id++;
						
					}
					
				}
				
			}
			
		}
		
		return rdfsSubClassesOf;
		
	}
	
	/**************************************************************************
	 * OWL Annotation Properties Utility Methods
	 * @throws JAXBException 
	 *************************************************************************/
	
	/**
	 * Extract a map of all OWL annotation properties that have been defined
	 * in a given ontology file
	 * @param owlFile
	 * @return
	 * @throws JAXBException
	 */
	
	public static Map<String, RDFOwlAnnotationProperty> getAnnotationProperties(
			InputStream owlFile) throws JAXBException {
		
		// Unmarshall the ontology XML file into a RDF POJO
		JAXBContext jaxbContext = JAXBContext.newInstance(RDF.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		RDF rdf = (RDF) jaxbUnmarshaller.unmarshal(owlFile);
		
		// Extract the RDFAnnotationProperty POJOs and populate the hash map
		List<RDFOwlAnnotationProperty> rdfOwlAnnotationProperties = 
				rdf.getOwlAnnotationProperties();
		int id = 1;
		for (RDFOwlAnnotationProperty rdfOwlAnnotationProperty : 
			rdfOwlAnnotationProperties) {
			rdfOwlAnnotationProperty.setId(id);
			id++;
		}
		
		// Generate a hash map of custom RDFOwlAnnotationProperty objects
		// This will map rdf:about (key) to RDFOwlAnnotationProperty (value)
		Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap = 
				rdfOwlAnnotationProperties.stream().collect(Collectors.toMap(
						RDFOwlAnnotationProperty::getRdfAbout, 
						rdfOwlAnnotationProperty -> rdfOwlAnnotationProperty));
		
		// Generate annotation properties for SKOS classes
		Map<String, RDFOwlAnnotationProperty> owlSkosAnnotationPropertyMap =
				OWLSKOSUtils.generateSkosAnnotationPropertyMap(id);
		owlAnnotationPropertyMap.putAll(owlSkosAnnotationPropertyMap);
		
		return owlAnnotationPropertyMap;
		
	}
	
	/**************************************************************************
	 * OWL Object Properties Utility Methods
	 *************************************************************************/
	
	/**
	 * Extract a map of all OWL object properties that have been defined
	 * in a given ontology
	 * @param ontology
	 * @param owlAnnotationPropertyMap
	 * @return
	 */
	
	public static Map<String, RDFOwlObjectProperty> getObjectProperties(
			OWLOntology ontology, 
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap) {
		
		// Instantiate a hash map of custom RDFOwlObjectProperty objects
		// This will map rdf:about (key) to RDFOwlObjectProperty (value)
		Map<String, RDFOwlObjectProperty> owlObjectPropertyMap = 
				new LinkedHashMap<String, RDFOwlObjectProperty>();
		
		// Extract all object properties using the OWL API
		Set<OWLObjectProperty> owlObjectProperties = 
				ontology.getObjectPropertiesInSignature();
		
		// Iterate over all object properties and generate 
		// RDFOwlObjectProperty POJOs
		int id = 1;
		for (OWLObjectProperty owlObjectProperty : owlObjectProperties) {
			
			// Get the RDF About
			String rdfAbout = OWLAPIUtils.getRdfAbout(owlObjectProperty);
			
			// Extract a set of super object property IRIs
			// for this object property
			Set<String> rdfsSubPropertyOf = OWLAPIUtils
					.getObjectPropertySubPropertiesOf(
							ontology, owlObjectProperty, rdfAbout);
			
			// Get the annotations for this object property
			List<OWLAnnotation> annotations = OWLAPIUtils.getAnnotations(
					ontology, owlObjectProperty);
			
			// Get the RDFS Label
			String rdfsLabel = OWLAPIUtils.getRdfsLabel(annotations);
			
			// Instantiate a map of RDFOwlAnnotationProperty:rdfAbout (key) to
			// annotation values (value) to capture the other annotations
			// for this object property
			Map<String, String> owlAnnotationProperties = 
					new LinkedHashMap<String, String>();
			
			// Iterate over all annotations
			for (OWLAnnotation annotation : annotations) {
				
				// If the annotation IRI is in the map of RDF annotation 
				// properties then create an annotation property object
				String annotationIRI = OWLAPIUtils.getAnnotationIRI(annotation);
				
				// Test whether the annotation IRI is in the map of RDF
				// annotation properties
				if (owlAnnotationPropertyMap.containsKey(annotationIRI)) {
					
					// Add the RDFOWLAnnotationProperty and its realised
					// value to the map of RDFOwlAnnotationProperty POJOs
					// for this object property
					owlAnnotationProperties.put(
							annotationIRI, 
							OWLAPIUtils.getAnnotationValue(annotation));
					
				}
				
			}
			
			// Create a new RDFOwlObjectProperty POJO
			RDFOwlObjectProperty rdfOwlObjectProperty = 
					new RDFOwlObjectProperty();
			rdfOwlObjectProperty.setId(id);
			rdfOwlObjectProperty.setRdfAbout(rdfAbout);
			rdfOwlObjectProperty.setRdfsLabel(rdfsLabel);
			rdfOwlObjectProperty.setOwlAnnotationProperties(
					owlAnnotationProperties);
			rdfOwlObjectProperty.setRdfsSubPropertyOf(rdfsSubPropertyOf);
			
			// Add the new RDFOwlObjectProperty POJO to the map of custom 
			// RDFOwlObjectProperty objects
			owlObjectPropertyMap.put(rdfAbout, rdfOwlObjectProperty);
			id++;
			
		}
		
		return owlObjectPropertyMap;
	}
	
	/**
	 * Extract a set of super object property IRIs for a given object property
	 * @param ontology
	 * @param owlObjectProperty
	 * @param rdfAbout
	 * @return
	 */
	
	public static Set<String> getObjectPropertySubPropertiesOf(
			OWLOntology ontology, OWLObjectProperty owlObjectProperty, 
			String rdfAbout) {
		
		// Instantiate a set to store the rdf:about for the 
		// applicable super object properties
		Set<String> rdfsSubPropertyOf = new LinkedHashSet<String>();
		
		// Get a list of referencing axioms for this object property
		List<OWLAxiom> axioms = OWLAPIUtils.getReferencingAxioms(
				ontology, owlObjectProperty);
		
		// Iterate over all the axioms
		for (OWLAxiom axiom : axioms) {
			
			// If the axiom type is SubObjectPropertyOf then add the 
			// SubObjectPropertyOf IRI to the set of sub properties
			if ( axiom.getAxiomType() == AxiomType.SUB_OBJECT_PROPERTY) {
				
				// Extract the source and target class IRIs
				Matcher matcher = PATTERN_SUBOBJECTPROPERTYOF_IRI_EXTRACTOR
						.matcher(axiom.toString());
				String sourceClassIRI = null;
				int counter = 1;
				while (matcher.find()) {
					if (counter == 1) {
						sourceClassIRI = matcher.group(1);
						break;
					}
					counter++;
				}
				
				// Get the object property (source) to 
				// sub object property (target) relationship where the source 
				// is equal to the IRI of the given object property
				if (sourceClassIRI.equalsIgnoreCase(rdfAbout)) {
				
					Set<OWLEntity> signatures = axiom.getSignature();
					for (OWLEntity signature : signatures) {
						String signatureIRI = signature.getIRI().getIRIString();
						if (!signatureIRI.equalsIgnoreCase(rdfAbout))
							rdfsSubPropertyOf.add(signatureIRI);
					}
					
				}
				
			}
			
		}
		
		return rdfsSubPropertyOf;
		
	}

}
