package ai.hyperlearning.ontopop.graph.tinkergraph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;

/**
 * TinkerGraph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class TinkerGraphDatabaseService extends GremlinGraphDatabaseService {
	
	public TinkerGraphDatabaseService() {
		super.supportsSchema = true;
	}
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/

	@Override
	public void createSchema() {
		
		final TinkerGraph tinkerGraph = (TinkerGraph) g.getGraph();
		
		// Create vertex-centric indices
        if (!tinkerGraph.getIndexedKeys(Vertex.class).iterator().hasNext()) {
            tinkerGraph.createIndex("id", Vertex.class);
            tinkerGraph.createIndex("iri", Vertex.class);
            tinkerGraph.createIndex("ontologyId", Vertex.class);
            tinkerGraph.createIndex("latestWebhookEventId", Vertex.class);
            tinkerGraph.createIndex("key", Vertex.class);
            tinkerGraph.createIndex("vertexId", Vertex.class);
        }
        
        // Create edge-centric indices
        if (!tinkerGraph.getIndexedKeys(Edge.class).iterator().hasNext()) {
        	tinkerGraph.createIndex("id", Edge.class);
        	tinkerGraph.createIndex("sourceVertexKey", Edge.class);
        	tinkerGraph.createIndex("sourceVertexId", Edge.class);
        	tinkerGraph.createIndex("targetVertexKey", Edge.class);
        	tinkerGraph.createIndex("targetVertexId", Edge.class);
        	tinkerGraph.createIndex("ontologyId", Edge.class);
        	tinkerGraph.createIndex("latestWebhookEventId", Edge.class);
        }
		
	}

}
