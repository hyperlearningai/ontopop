package ai.hyperlearning.ontopop.graph.gremlin.engines.tinkergraph;

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
	    super.supportsUserDefinedIds = true;
		super.supportsNonStringIds = true;
		super.supportsSchema = true;
		super.supportsTransactions = false;
		super.supportsGeoshape = false;
		super.supportsTraversalsBy = true;
	}
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/

	@Override
	public void createSchema() {
		
		final TinkerGraph graph = (TinkerGraph) g.getGraph();
		
		// Create vertex-centric indices
        if (!graph.getIndexedKeys(Vertex.class).iterator().hasNext()) {
        	graph.createIndex("id", Vertex.class);
        	graph.createIndex("iri", Vertex.class);
        	graph.createIndex("ontologyId", Vertex.class);
        	graph.createIndex("latestWebhookEventId", Vertex.class);
        	graph.createIndex("vertexKey", Vertex.class);
        	graph.createIndex("vertexId", Vertex.class);
        }
        
        // Create edge-centric indices
        if (!graph.getIndexedKeys(Edge.class).iterator().hasNext()) {
        	graph.createIndex("id", Edge.class);
        	graph.createIndex("sourceVertexKey", Edge.class);
        	graph.createIndex("sourceVertexId", Edge.class);
        	graph.createIndex("targetVertexKey", Edge.class);
        	graph.createIndex("targetVertexId", Edge.class);
        	graph.createIndex("ontologyId", Edge.class);
        	graph.createIndex("latestWebhookEventId", Edge.class);
        }
		
	}

}
