package ai.hyperlearning.ontopop.model.graph.formats.graphson;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ai.hyperlearning.ontopop.model.graph.formats.PropertyGraphFormat;

/**
 * GraphSON Graph Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GraphSONGraph extends PropertyGraphFormat implements Serializable  {

    private static final long serialVersionUID = 799775699830477685L;
    
    // Internal property keys
    private static final String COMMON_PROPERTY_KEY_ID = "_id";
    private static final String COMMON_PROPERTY_KEY_TYPE = "_type";
    private static final String COMMON_PROPERTY_KEY_LABEL = "_label";
    private static final String VERTEX_PROPERTY_KEY_RDFS_LABEL = "label";
    private static final String VERTEX_PROPERTY_KEY_NAME = "name";
    private static final String EDGE_PROPERTY_KEY_OUT_V = "_outV";
    private static final String EDGE_PROPERTY_KEY_IN_V = "_inV";
    private static final String EDGE_PROPERTY_KEY_RELATIONSHIP = "relationship";
    private static final String EDGE_PROPERTY_KEY_WEIGHT = "weight";
    
    // Internal property key values
    private static final String VERTEX_PROPERTY_VALUE_TYPE = "vertex";
    private static final String EDGE_PROPERTY_VALUE_TYPE = "edge";
    private static final int EDGE_PROPERTY_VALUE_WEIGHT = 1;
    
    private String mode = "NORMAL";
    
    public GraphSONGraph() {
        
    }

    public GraphSONGraph(List<Map<String, Object>> vertices,
            List<Map<String, Object>> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public void addVertex(long vertexId, String label, 
            Map<String, Object> properties) {
        
        // Add vertex properties
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        propertyMap.put(COMMON_PROPERTY_KEY_ID, String.valueOf(vertexId));
        propertyMap.put(COMMON_PROPERTY_KEY_TYPE, VERTEX_PROPERTY_VALUE_TYPE);
        propertyMap.put(COMMON_PROPERTY_KEY_LABEL, label);
        propertyMap.putAll(properties);
        
        // Explicitly generate the name property
        if ( propertyMap.containsKey(VERTEX_PROPERTY_KEY_RDFS_LABEL) ) {
            String rdfsLabel = (String) propertyMap.get(
                    VERTEX_PROPERTY_KEY_RDFS_LABEL);
            propertyMap.put(VERTEX_PROPERTY_KEY_NAME, rdfsLabel);
            propertyMap.remove(VERTEX_PROPERTY_KEY_RDFS_LABEL);
        }
        
        // Add the vertex to the list of vertices
        this.vertices.add(propertyMap);
        
    }
    
    public void addEdge(long edgeId, String label, 
            long sourceVertexId, long targetVertexId, 
            Map<String, Object> properties) {
        
        // Add edge properties
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        propertyMap.put(COMMON_PROPERTY_KEY_ID, String.valueOf(edgeId));
        propertyMap.put(COMMON_PROPERTY_KEY_TYPE, EDGE_PROPERTY_VALUE_TYPE);
        propertyMap.put(COMMON_PROPERTY_KEY_LABEL, label);
        propertyMap.put(EDGE_PROPERTY_KEY_OUT_V, String.valueOf(sourceVertexId));
        propertyMap.put(EDGE_PROPERTY_KEY_IN_V, String.valueOf(targetVertexId));
        propertyMap.put(EDGE_PROPERTY_KEY_WEIGHT, EDGE_PROPERTY_VALUE_WEIGHT);
        propertyMap.putAll(properties);
        
        // Explicitly generate the relationship property if required
        if ( !propertyMap.containsKey(EDGE_PROPERTY_KEY_RELATIONSHIP) )
            propertyMap.put(EDGE_PROPERTY_KEY_RELATIONSHIP, label);
        
        // Add the edge to the list of edges
        this.edges.add(propertyMap);
        
    }

    @Override
    public String toString() {
        return "GraphSONGraph ["
                + "mode=" + mode + ", "
                + "vertices=" + this.vertices + ", "
                + "edges=" + this.edges 
                + "]";
    }

}
