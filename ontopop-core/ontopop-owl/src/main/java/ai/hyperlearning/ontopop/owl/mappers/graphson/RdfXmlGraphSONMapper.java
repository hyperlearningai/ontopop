package ai.hyperlearning.ontopop.owl.mappers.graphson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyEdge;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyVertex;
import ai.hyperlearning.ontopop.model.graph.formats.graphson.GraphSON;
import ai.hyperlearning.ontopop.model.graph.formats.graphson.GraphSONGraph;

/**
 * RDF/XML to GraphSON format mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class RdfXmlGraphSONMapper {
    
    private RdfXmlGraphSONMapper() {
        throw new IllegalStateException("The GraphSONRdfXmlModeller utility "
            + "class cannot be instantiated.");
    }
    
    /**
     * Map a given SimpleOntologyPropertyGraph object
     * into GraphSON format
     * Reference: https://github.com/tinkerpop/blueprints/wiki/GraphSON-Reader-and-Writer-Library
     * @param simpleOntologyPropertyGraph
     * @return
     * @throws JsonProcessingException 
     */
    
    public static String map(
            SimpleOntologyPropertyGraph simpleOntologyPropertyGraph) 
                    throws JsonProcessingException {
        
        // Initialise the GraphSONGraph object
        GraphSONGraph graph = new GraphSONGraph();
        
        // Generate the vertices
        for (SimpleOntologyVertex simpleOntologyVertex : 
                simpleOntologyPropertyGraph.getVertices().values()) {
            simpleOntologyVertex.preparePropertiesForModelling();
            graph.addVertex(simpleOntologyVertex.getVertexId(), 
                    simpleOntologyVertex.getProperties());
        }
        
        // Generate the edges
        long edgeId = 1;
        for (SimpleOntologyEdge simpleOntologyEdge : 
                simpleOntologyPropertyGraph.getEdges()) {
            graph.addEdge(edgeId, 
                    simpleOntologyEdge.getSourceVertexId(), 
                    simpleOntologyEdge.getTargetVertexId(), 
                    simpleOntologyEdge.getProperties());
            edgeId++;
        }
        
        // Return the GraphSON string
        GraphSON graphSON = new GraphSON(graph);
        ObjectWriter writer = new ObjectMapper()
                .writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(graphSON);
        
    }

}
