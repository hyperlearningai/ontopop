package ai.hyperlearning.ontopop.owl.mappers.vis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ai.hyperlearning.ontopop.model.graph.SimpleOntologyEdge;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyVertex;
import ai.hyperlearning.ontopop.model.graph.formats.vis.VisDataset;
import ai.hyperlearning.ontopop.model.graph.formats.vis.VisDatasetGraph;

/**
 * RDF/XML to vis.js dataset format mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Deprecated
public class RdfXmlVisDatasetMapper {
    
    private RdfXmlVisDatasetMapper() {
        throw new IllegalStateException("The RdfXmlVisDatasetMapper utility "
            + "class cannot be instantiated.");
    }
    
    public static String map(
            SimpleOntologyPropertyGraph simpleOntologyPropertyGraph) 
                    throws JsonProcessingException {
        
        // Initialise the Vis.js Dataset Graph object
        VisDatasetGraph graph = new VisDatasetGraph();
        
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
        
        // Return the Vis.js Dataset string
        VisDataset visDataset = new VisDataset(graph);
        ObjectWriter writer = new ObjectMapper()
                .writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(visDataset);
        
    }

}
