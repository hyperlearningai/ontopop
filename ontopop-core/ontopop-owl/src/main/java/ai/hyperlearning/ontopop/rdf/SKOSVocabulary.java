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
 * Simple Knowledge Organization System Namespace Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SKOSVocabulary {

    private static final String SKOS_RDF_FILENAME = "skos.rdf";
    private static final String SKOS_CORE_COMMENT_IRI =
            "http://www.w3.org/2004/02/skos/core#comment";
    private static final String SKOS_CORE_COMMENT_LABEL = "Comment";

    /**
     * Read the SKOS RDF and return an OWL 2 ontology representation
     * 
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */

    public static OWLOntology loadSKOSRDF()
            throws OWLOntologyCreationException, IOException {
        ClassLoader classLoader = SKOSVocabulary.class.getClassLoader();
        try (InputStream inputStream =
                classLoader.getResourceAsStream(SKOS_RDF_FILENAME)) {
            return OWLAPI.loadOntology(inputStream);
        }
    }

    /**
     * Parse the SKOS namespace and generate a map of Annotation Property IRI to
     * OntoPop Simple Annotation Property objects
     * 
     * @param skosRdf
     * @return
     */

    public static Map<String, SimpleAnnotationProperty> parseAnnotationProperties(
            OWLOntology skosRdf) {

        Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap =
                OWLAPI.parseAnnotationProperties(skosRdf);

        // Explicitly create a Simple Annotation Property object for
        // http://www.w3.org/2004/02/skos/core#comment
        SimpleAnnotationProperty skosCoreComment =
                new SimpleAnnotationProperty(SKOS_CORE_COMMENT_IRI,
                        SKOS_CORE_COMMENT_LABEL, new LinkedHashMap<>());
        simpleAnnotationPropertyMap.put(SKOS_CORE_COMMENT_IRI, skosCoreComment);

        return simpleAnnotationPropertyMap;

    }

}
