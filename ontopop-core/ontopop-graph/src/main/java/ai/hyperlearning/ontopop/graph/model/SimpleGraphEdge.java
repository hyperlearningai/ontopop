package ai.hyperlearning.ontopop.graph.model;

import java.io.Serializable;
import java.util.Map;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Simple Edge model for graph loading
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleGraphEdge implements Serializable {

    private static final long serialVersionUID = 1133567718026149941L;
    private String label;
    private Vertex sourceVertex;
    private long sourceVertexId;
    private Vertex targetVertex;
    private long targetVertexId;
    private Map<String, Object> properties;

    public SimpleGraphEdge() {

    }

    public SimpleGraphEdge(String label, Vertex sourceVertex,
            Vertex targetVertex, Map<String, Object> properties) {
        this.label = label;
        this.sourceVertex = sourceVertex;
        this.sourceVertexId = (Long) sourceVertex.id();
        this.targetVertex = targetVertex;
        this.targetVertexId = (Long) targetVertex.id();
        this.properties = properties;
    }

    public SimpleGraphEdge(String label, long sourceVertexId,
            long targetVertexId, Map<String, Object> properties) {
        this.label = label;
        this.sourceVertexId = sourceVertexId;
        this.targetVertexId = targetVertexId;
        this.properties = properties;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public void setSourceVertex(Vertex sourceVertex) {
        this.sourceVertex = sourceVertex;
        this.sourceVertexId = (Long) sourceVertex.id();
    }

    public long getSourceVertexId() {
        return sourceVertexId;
    }

    public void setSourceVertexId(long sourceVertexId) {
        this.sourceVertexId = sourceVertexId;
    }

    public Vertex getTargetVertex() {
        return targetVertex;
    }

    public void setTargetVertex(Vertex targetVertex) {
        this.targetVertex = targetVertex;
        this.targetVertexId = (Long) targetVertex.id();
    }

    public long getTargetVertexId() {
        return targetVertexId;
    }

    public void setTargetVertexId(long targetVertexId) {
        this.targetVertexId = targetVertexId;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "SimpleGraphEdge [" 
                + "label=" + label + ", " 
                + "sourceVertex=" + sourceVertex + ", " 
                + "sourceVertexId=" + sourceVertexId + ", " 
                + "targetVertex=" + targetVertex + ", "
                + "targetVertexId=" + targetVertexId + ", "
                + "properties=" + properties 
                + "]";
    }

}
