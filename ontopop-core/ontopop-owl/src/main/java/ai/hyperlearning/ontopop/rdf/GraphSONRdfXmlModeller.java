package ai.hyperlearning.ontopop.rdf;

import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;

/**
 * RDF/XML to GraphSON format modeller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GraphSONRdfXmlModeller {
    
    private GraphSONRdfXmlModeller() {
        throw new IllegalStateException("The GraphSONRdfXmlModeller utility "
            + "class cannot be instantiated.");
    }
    
    /**
     * Transform a given SimpleOntologyPropertyGraph object
     * into GraphSON format
     * Reference: https://github.com/tinkerpop/blueprints/wiki/GraphSON-Reader-and-Writer-Library
     * @param simpleOntologyPropertyGraph
     * @return
     */
    
    public static String model(
            SimpleOntologyPropertyGraph simpleOntologyPropertyGraph) {
        return null;
    }

}
