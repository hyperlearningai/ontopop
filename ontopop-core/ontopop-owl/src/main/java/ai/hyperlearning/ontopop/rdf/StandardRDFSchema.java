package ai.hyperlearning.ontopop.rdf;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;

/**
 * Standard RDF Schema
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class StandardRDFSchema {
    
    /**
     * Load and parse the standard SKOS, RDF and DCMI RDF schema
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    
    public static Map<String, SimpleAnnotationProperty> loadStandardSchema() 
            throws OWLOntologyCreationException, IOException {
        
        // Load the SKOS Vocabulary and parse its annotation properties
        OWLOntology skos = SKOSVocabulary.loadSKOSRDF();
        Map<String, SimpleAnnotationProperty> skosAnnotationProperties =
                SKOSVocabulary.parseAnnotationProperties(skos);

        // Load the RDF Schema and parse its annotation properties
        OWLOntology rdf = RDFSchema.loadRdfSchema();
        Map<String, SimpleAnnotationProperty> rdfSchemaAnnotationProperties =
                RDFSchema.parseAnnotationProperties(rdf);

        // Load the DCMI RDF Schema and parse its annotation properties
        Map<String, SimpleAnnotationProperty> dcmiSchemaAnnotationProperties =
                DCMI.parseAnnotationProperties();

        // Aggregate the standard schema annotation properties
        Map<String, SimpleAnnotationProperty> standardSchemaAnnotationProperties =
                new LinkedHashMap<>(skosAnnotationProperties);
        standardSchemaAnnotationProperties
                .putAll(rdfSchemaAnnotationProperties);
        standardSchemaAnnotationProperties
                .putAll(dcmiSchemaAnnotationProperties);
        return standardSchemaAnnotationProperties;
        
    }
    
    /**
     * Transform the standard schema map and generate a map between
     * label and SimpleAnnotationProperty.
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    
    public static Map<String, SimpleAnnotationProperty> 
            getLabelStandardSchemaAnnotationPropertyMap() 
                    throws OWLOntologyCreationException, IOException {
        Map<String, SimpleAnnotationProperty> labelStandardSchemaAnnotationPropertyMap = 
                new HashMap<>();
        Map<String, SimpleAnnotationProperty> standardSchema = 
                loadStandardSchema();
        for ( SimpleAnnotationProperty simpleAnnotationProperty : 
            standardSchema.values() ) {
            if ( simpleAnnotationProperty.getLabel() != null )
                labelStandardSchemaAnnotationPropertyMap.put(
                        simpleAnnotationProperty.getLabel().strip().toUpperCase(), 
                        simpleAnnotationProperty);
        }
        return labelStandardSchemaAnnotationPropertyMap;
    }
    
    /**
     * Generate a unique set of standard schema annotation property labels
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    
    public static Set<String> getUniqueStandardSchemaAnnotationPropertyLabels() 
            throws OWLOntologyCreationException, IOException {
        Map<String, SimpleAnnotationProperty> standardSchema = loadStandardSchema();
        Set<String> standardSchemaAnnotationPropertyLabels = new HashSet<>();
        for ( SimpleAnnotationProperty simpleAnnotationProperty : 
            standardSchema.values() ) {
            if ( simpleAnnotationProperty.getLabel() != null )
                standardSchemaAnnotationPropertyLabels.add(
                        simpleAnnotationProperty.getLabel()
                            .strip().toUpperCase());
        }
        return standardSchemaAnnotationPropertyLabels;
    }

}
