package ai.hyperlearning.ontopop.model.graph.formats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract Property Graph Format Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public abstract class PropertyGraphFormat implements Serializable {

    private static final long serialVersionUID = 535541349080103183L;
    
    protected List<Map<String, Object>> vertices = new ArrayList<>();
    protected List<Map<String, Object>> edges = new ArrayList<>();
    
    public List<Map<String, Object>> getVertices() {
        return vertices;
    }

    public void setVertices(List<Map<String, Object>> vertices) {
        this.vertices = vertices;
    }

    public List<Map<String, Object>> getEdges() {
        return edges;
    }

    public void setEdges(List<Map<String, Object>> edges) {
        this.edges = edges;
    }
    
    public abstract void addVertex(
            long vertexId, String label, Map<String, Object> properties);
    
    public abstract void addEdge(
            long edgeId, String label, long sourceVertexId, 
            long targetVertexId, 
            Map<String, Object> properties);

    @Override
    public String toString() {
        return "PropertyGraph ["
                + "vertices=" + vertices + ", "
                + "edges=" + edges 
                + "]";
    }

}
