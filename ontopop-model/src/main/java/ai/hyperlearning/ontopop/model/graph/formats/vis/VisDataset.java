package ai.hyperlearning.ontopop.model.graph.formats.vis;

import java.io.Serializable;

/**
 * Vis.js Dataset Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class VisDataset implements Serializable {

    private static final long serialVersionUID = -8546487298286086683L;
    
    private VisDatasetGraph graph;
    
    public VisDataset() {
        
    }

    public VisDataset(VisDatasetGraph graph) {
        super();
        this.graph = graph;
    }

    public VisDatasetGraph getGraph() {
        return graph;
    }

    public void setGraph(VisDatasetGraph graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        return "VisDataset ["
                + "graph=" + graph 
                + "]";
    }

}
