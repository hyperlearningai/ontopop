package ai.hyperlearning.ontopop.owl.clients.ontokai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.WordUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.google.common.collect.Maps;

import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNode;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNodeAttribute;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNodeRelationship;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyPayload;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleAnnotationPropertyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleObjectPropertyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;
import ai.hyperlearning.ontopop.owl.OWLRDFXMLAPI;
import ai.hyperlearning.ontopop.rdf.StandardRDFSchema;

/**
 * OntoKai-specific OWL Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOWLAPI {
    
    private static final String DEFAULT_IRI_PREFIX = 
            "http://webprotege.stanford.edu/R";
    private static final int DEFAULT_IRI_SUFFIX_LENGTH = 22;
    private static final String RDF_SCHEMA_LABEL_IRI = 
            "http://www.w3.org/2000/01/rdf-schema#label";
    private static final String SUBCLASS_OF_OBJECT_PROPERTY_RDFS_LABEL = 
            "SUBCLASS OF";
    
    private OntoKaiOWLAPI() {
        throw new IllegalStateException("The OntoKaiOWLAPI utility class "
                + "cannot be instantiated.");
    }
    
    /**
     * Generate a SimpleOntologyDiff object given an OntoKai ontology
     * payload object and the latest existing SimpleOntology object
     * for a given ontology ID. This new SimpleOntologyDiff object will contain
     * newly created SimpleAnnotationProperty objects, 
     * newly created SimpleObjectProperty objects, 
     * and created/updated/deleted SimpleClass objects.
     * @param ontoKaiOntologyPayload
     * @param latestExistingOntology
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static SimpleOntologyDiff generateSimpleOntologyDiff(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) 
                    throws OWLOntologyCreationException, IOException {
        
        // Identify new annotation properties
        List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationProperties = 
                new ArrayList<>();
        for ( SimpleAnnotationProperty simpleAnnotationProperty : 
            identifyNewAnnotationProperties(
                    ontoKaiOntologyPayload, simpleOntology).values()) {
            SimpleAnnotationPropertyDiff simpleAnnotationPropertyDiff = 
                    new SimpleAnnotationPropertyDiff();
            simpleAnnotationPropertyDiff.setAfter(simpleAnnotationProperty);
            simpleAnnotationPropertyDiff.setAfterXml(
                    OWLRDFXMLAPI.generateNewOwlAnnotationPropertyXml(
                            simpleAnnotationProperty.getIri(), 
                            simpleAnnotationProperty.getLabel()));
            createdSimpleAnnotationProperties.add(simpleAnnotationPropertyDiff);
        }
        
        // Identify new object properties
        List<SimpleObjectPropertyDiff> createdSimpleObjectProperties = 
                new ArrayList<>();
        for ( SimpleObjectProperty simpleObjectProperty : 
            identifyNewObjectProperties(
                    ontoKaiOntologyPayload, simpleOntology).values()) {
            SimpleObjectPropertyDiff simpleObjectPropertyDiff = 
                    new SimpleObjectPropertyDiff();
            simpleObjectPropertyDiff.setAfter(simpleObjectProperty);
            simpleObjectPropertyDiff.setAfterXml(
                    OWLRDFXMLAPI.generateNewOwlObjectPropertyXml(
                            simpleObjectProperty.getIri(), 
                            simpleObjectProperty.getLabel()));
            createdSimpleObjectProperties.add(simpleObjectPropertyDiff);
        }
        
        
        // Generate the SimpleOntologyDiff object
        SimpleOntologyDiff simpleOntologyDiff = new SimpleOntologyDiff();
        simpleOntologyDiff.setCreatedSimpleAnnotationProperties(
                createdSimpleAnnotationProperties);
        simpleOntologyDiff.setCreatedSimpleObjectProperties(
                createdSimpleObjectProperties);
        
        return simpleOntologyDiff;
        
    }
    
    /**
     * Generate a mapping between OntoKai node ID and the OntoKai
     * node object
     * @param ontoKaiOntologyPayload
     * @return
     */
    
    public static Map<Integer, OntoKaiOntologyNode> generateNodeMap(
            OntoKaiOntologyPayload ontoKaiOntologyPayload) {
        return Maps.uniqueIndex(ontoKaiOntologyPayload.getNodes(), 
                OntoKaiOntologyNode::getId);
    }
    
    /**
     * Identify any new annotation properties in the OntoKai ontology
     * payload object. If new annotation properties are found,
     * create SimpleAnnotationProperty object representations of them and 
     * return a mapping between generated IRI and the SimpleAnnotationProperty.
     * @param ontoKaiOntologyPayload
     * @param latestExistingOntology
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static Map<String, SimpleAnnotationProperty> identifyNewAnnotationProperties(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) 
                    throws OWLOntologyCreationException, IOException {
        
        // Instantiate a map of new SimpleAnnotationProperty objects
        Set<String> newSimpleAnnotationPropertyLabels = new HashSet<>();
        Map<String, SimpleAnnotationProperty> newSimpleAnnotationProperties = 
                new HashMap<>();
        
        // Get a list of unique standard schema annotation property labels
        Set<String> standardSchemaAnnotationPropertyLabels = StandardRDFSchema
                .getUniqueStandardSchemaAnnotationPropertyLabels();
        
        // Get a list of unique existing annotation property labels
        Set<String> uniqueExistingSimpleAnnotationPropertyLabels = 
                simpleOntology.getUniqueSimpleAnnotationPropertyLabels();
        
        // Iterate over all the nodes in the OntoKai ontology payload object
        for ( OntoKaiOntologyNode node : ontoKaiOntologyPayload.getNodes() ) {
            
            // Iterate over all the attributes for this node
            for ( OntoKaiOntologyNodeAttribute attribute : node.getAttributes() ) {
                
                // Check whether the attribute key already exists
                // as an annotation property in the given SimpleOntology
                String attributeName = attribute.getName().strip();
                if ( !standardSchemaAnnotationPropertyLabels.contains(
                        attributeName.toUpperCase()) && 
                        !uniqueExistingSimpleAnnotationPropertyLabels.contains(
                                attributeName.toUpperCase()) && 
                        !newSimpleAnnotationPropertyLabels.contains(
                                attributeName.toUpperCase()) ) {
                    
                    // Create a new SimpleAnnotationProperty object
                    SimpleAnnotationProperty newSimpleAnnotationProperty = 
                            new SimpleAnnotationProperty();
                    newSimpleAnnotationProperty.setIri(
                            generateIri(simpleOntology));
                    newSimpleAnnotationProperty.setLabel(attributeName);
                    Map<String, String> annotations = new LinkedHashMap<>();
                    annotations.put(RDF_SCHEMA_LABEL_IRI, attributeName);
                    newSimpleAnnotationProperty.setAnnotations(annotations);
                    newSimpleAnnotationPropertyLabels.add(attributeName.toUpperCase());
                    newSimpleAnnotationProperties.put(
                            newSimpleAnnotationProperty.getIri(), 
                            newSimpleAnnotationProperty);
                    
                }
                
            }
            
        }
        
        return newSimpleAnnotationProperties;
        
    }
    
    /**
     * Identify new object properties in the OntoKai ontology payload object. 
     * If new object properties are found, create SimpleObjectProperty object
     * representations of them and return a mapping between generated IRI and
     * the SimpleObjectProperty.
     * @param ontoKaiOntologyPayload
     * @param simpleOntology
     * @return
     */
    
    public static Map<String, SimpleObjectProperty> identifyNewObjectProperties(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) {
        
        // Instantiate a map of new SimpleObjectProperty objects
        Set<String> newSimpleObjectPropertyLabels = new HashSet<>();
        Map<String, SimpleObjectProperty> newSimpleObjectProperties = 
                new HashMap<>();
        
        // Get a list of unique existing object property labels
        Set<String> uniqueSimpleObjectPropertyLabels = 
                simpleOntology.getUniqueSimpleObjectPropertyLabels();
        
        // Iterate over all the nodes in the OntoKai ontology payload object
        for ( OntoKaiOntologyNode node : ontoKaiOntologyPayload.getNodes() ) {
            
            // Iterate over all the relationships for this node
            for ( OntoKaiOntologyNodeRelationship relationship : 
                node.getRelationships() ) {
                
                // Check whether the type key already exists
                // as an object property in the given SimpleOntology
                String typeTransformed = relationship.getType().strip()
                        .toUpperCase().replace("_", " ");
                if ( !typeTransformed.equalsIgnoreCase(
                        SUBCLASS_OF_OBJECT_PROPERTY_RDFS_LABEL) && 
                        !uniqueSimpleObjectPropertyLabels.contains(typeTransformed) &&
                        !newSimpleObjectPropertyLabels.contains(typeTransformed) ) {
                    
                    // Create a new SimpleObjectProperty object
                    SimpleObjectProperty newSimpleObjectProperty = 
                            new SimpleObjectProperty();
                    newSimpleObjectProperty.setIri(
                            generateIri(simpleOntology));
                    newSimpleObjectProperty.setLabel(
                            WordUtils.capitalize(typeTransformed.toLowerCase()));
                    Map<String, String> annotations = new LinkedHashMap<>();
                    annotations.put(RDF_SCHEMA_LABEL_IRI, 
                            WordUtils.capitalize(typeTransformed.toLowerCase()));
                    newSimpleObjectProperty.setAnnotations(annotations);
                    newSimpleObjectPropertyLabels.add(typeTransformed);
                    newSimpleObjectProperties.put(
                            newSimpleObjectProperty.getIri(), 
                            newSimpleObjectProperty);
                    
                }
                
            }
            
        }
        
        return newSimpleObjectProperties;
        
    }
    
    /**
     * Generate a random IRI using a given prefix
     * @return
     */
    
    public static String generateIri(SimpleOntology simpleOntology) {
        String iri = null;
        boolean generatedUniqueIri = false;
        while (!generatedUniqueIri) {
            iri = DEFAULT_IRI_PREFIX + RandomStringUtils.random(
                    DEFAULT_IRI_SUFFIX_LENGTH, true, true);
            if ( !simpleOntology.getSimpleAnnotationPropertyMap().containsKey(iri) && 
                    !simpleOntology.getSimpleObjectPropertyMap().containsKey(iri) && 
                    !simpleOntology.getSimpleClassMap().containsKey(iri))
                generatedUniqueIri = true;
        } 
        return iri;
    }

}
