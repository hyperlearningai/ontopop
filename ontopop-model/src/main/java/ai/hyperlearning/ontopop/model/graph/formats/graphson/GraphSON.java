package ai.hyperlearning.ontopop.model.graph.formats.graphson;

import java.io.Serializable;

/**
 * GraphSON Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GraphSON implements Serializable {

    private static final long serialVersionUID = 6056720960287769182L;
    
    private GraphSONGraph graph;
    
    public GraphSON() {
        
    }

    public GraphSON(GraphSONGraph graph) {
        super();
        this.graph = graph;
    }

    public GraphSONGraph getGraph() {
        return graph;
    }

    public void setGraph(GraphSONGraph graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        return "GraphSON ["
                + "graph=" + graph + 
                "]";
    }

}
