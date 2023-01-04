package ai.hyperlearning.ontopop.model.graph.formats.vis;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ai.hyperlearning.ontopop.model.graph.formats.PropertyGraphFormat;

/**
 * Vis.js Dataset Graph Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class VisDatasetGraph extends PropertyGraphFormat implements Serializable {

    private static final long serialVersionUID = -4080082062382561699L;
    
    // Internal property keys
    private static final String COMMON_PROPERTY_KEY_ID = "id";
    private static final String COMMON_PROPERTY_KEY_LABEL = "type";
    private static final String NODE_PROPERTY_KEY_GROUP = "group";
    private static final String EDGE_PROPERTY_KEY_OUT_V = "from";
    private static final String EDGE_PROPERTY_KEY_IN_V = "to";
    private static final String EDGE_PROPERTY_KEY_RELATIONSHIP = "label";
    private static final String EDGE_PROPERTY_KEY_WEIGHT = "weight";
    
    // Internal property key values
    private static final int EDGE_PROPERTY_VALUE_WEIGHT = 1;
    
    public VisDatasetGraph() {
        
    }

    public VisDatasetGraph(List<Map<String, Object>> vertices, 
            List<Map<String, Object>> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }
    
    public void addVertex(long vertexId, String label, 
            Map<String, Object> properties) {
        
        // Add vertex properties
        String group = StringUtils.deleteWhitespace(label.toLowerCase());
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        propertyMap.put(COMMON_PROPERTY_KEY_ID, String.valueOf(vertexId));
        propertyMap.put(COMMON_PROPERTY_KEY_LABEL, label);
        propertyMap.put(NODE_PROPERTY_KEY_GROUP, group);
        propertyMap.putAll(properties);
        
        // Add the vertex to the list of vertices
        this.vertices.add(propertyMap);
        
    }
    
    public void addEdge(long edgeId, String label, 
            long sourceVertexId, long targetVertexId, 
            Map<String, Object> properties) {
        
        // Add edge properties
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        propertyMap.put(COMMON_PROPERTY_KEY_ID, String.valueOf(edgeId));
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
        return "VisDatasetGraph ["
                + "vertices=" + this.vertices + ", "
                + "edges=" + this.edges 
                + "]";
    }

}
