package ai.hyperlearning.ontopop.owl.clients.ontokai;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.google.common.collect.Maps;

import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNode;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNodeAttribute;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyPayload;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
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
    
    private OntoKaiOWLAPI() {
        throw new IllegalStateException("The OntoKaiOWLAPI utility class "
                + "cannot be instantiated.");
    }
    
    /**
     * Generate a SimpleOntology object given an OntoKai ontology
     * payload object and the latest existing SimpleOntology object
     * for a given ontology ID. This new SimpleOntology object will contain
     * newly created SimpleAnnotationProperty objects, 
     * newly created SimpleObjectProperty objects, 
     * and created/updated/deleted SimpleClass objects.
     * @param ontoKaiOntologyPayload
     * @param latestExistingOntology
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static SimpleOntology generateSimpleOntologyDiff(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) 
                    throws OWLOntologyCreationException, IOException {
        
        // Identify new annotation properties
        Map<String, SimpleAnnotationProperty> newSimpleAnnotationPropertyMap = 
                identifyNewAnnotationProperties(
                        ontoKaiOntologyPayload, simpleOntology);
        
        // Generate the SimpleOntology diff object
        SimpleOntology simpleOntologyDiff = new SimpleOntology();
        simpleOntologyDiff.setId(simpleOntology.getId());
        simpleOntologyDiff.setLatestGitWebhookId(
                simpleOntology.getLatestGitWebhookId());
        simpleOntologyDiff.setSimpleAnnotationPropertyMap(
                newSimpleAnnotationPropertyMap);
        
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
