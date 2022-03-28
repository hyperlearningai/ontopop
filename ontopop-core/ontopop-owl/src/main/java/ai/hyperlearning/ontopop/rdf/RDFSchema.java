package ai.hyperlearning.ontopop.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.owl.OWLAPI;

/**
 * RDF Schema Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class RDFSchema {

    private static final String RDF_SCHEMA_FILENAME = "rdf-schema.ttl";

    /**
     * Read the RDF schema and return an OWL 2 ontology representation
     * 
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */

    public static OWLOntology loadRdfSchema()
            throws OWLOntologyCreationException, IOException {
        ClassLoader classLoader = RDFSchema.class.getClassLoader();
        try (InputStream inputStream =
                classLoader.getResourceAsStream(RDF_SCHEMA_FILENAME)) {
            return OWLAPI.loadOntology(inputStream);
        }
    }

    /**
     * Parse the RDF schema and generate a map of Annotation Property IRI to
     * OntoPop Simple Annotation Property objects
     * 
     * @param rdfSchema
     * @return
     */

    public static Map<String, SimpleAnnotationProperty> parseAnnotationProperties(
            OWLOntology rdfSchema) {
        return OWLAPI.parseAnnotationProperties(rdfSchema);
    }

}
