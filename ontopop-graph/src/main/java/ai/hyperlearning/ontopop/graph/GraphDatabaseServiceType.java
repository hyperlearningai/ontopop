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
	GREMLIN_SERVER_REMOTE_CONNECTION("GREMLIN-SERVER-REMOTE-CONNECTION"), 
	GREMLIN_SERVER_CLIENT("GREMLIN-SERVER-CLIENT"), 
	TINKERGRAPH("TINKERGRAPH"), 
	JANUSGRAPH("JANUSGRAPH"), 
	AZURE_COSMOSDB("AZURE-COSMOSDB");
	
	private final String label;
	private static final Map<String, GraphDatabaseServiceType> LABEL_MAP = 
			new HashMap<>();
	
	static {
        for (GraphDatabaseServiceType g: values()) {
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
