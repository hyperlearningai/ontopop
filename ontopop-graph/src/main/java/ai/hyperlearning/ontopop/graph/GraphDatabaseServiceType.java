package ai.hyperlearning.ontopop.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Graph Database Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum GraphDatabaseServiceType {

    GREMLIN_GRAPH("GREMLIN-GRAPH"),
    GREMLIN_SERVER_WS("GREMLIN-SERVER-WS"),
    GREMLIN_SERVER_DRIVER("GREMLIN-SERVER-DRIVER"),
    JANUSGRAPH_WS("JANUSGRAPH-WS"), 
    JANUSGRAPH_DRIVER("JANUSGRAPH-DRIVER"),
    AZURE_COSMOSDB("AZURE-COSMOSDB"), 
    TINKERGRAPH("TINKERGRAPH");

    private final String label;
    private static final Map<String, GraphDatabaseServiceType> LABEL_MAP =
            new HashMap<>();

    static {
        for (GraphDatabaseServiceType g : values()) {
            LABEL_MAP.put(g.label, g);
        }
    }

    private GraphDatabaseServiceType(final String label) {
        this.label = label;
    }

    public static GraphDatabaseServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
