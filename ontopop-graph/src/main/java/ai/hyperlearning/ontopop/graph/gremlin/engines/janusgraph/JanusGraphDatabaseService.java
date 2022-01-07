package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.server.remoteconnection.GremlinServerRemoteConnectionGraphDatabaseService;

/**
 * TinkerGraph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class JanusGraphDatabaseService extends GremlinServerRemoteConnectionGraphDatabaseService {
	
	public JanusGraphDatabaseService() {
		super.supportsNonStringIds = true;
		super.supportsSchema = true;
		super.supportsTransactions = true;
		super.supportsGeoshape = true;
		super.supportsTraversalsBy = true;
	}
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/

	@Override
	public void createSchema() {
		
		// Get the JanusGraph management service
		final JanusGraph graph = (JanusGraph) g.getGraph();
		final JanusGraphManagement management = graph.openManagement();
		try {
			
			if (!doesSchemaExist(management)) {
				createProperties(management);
				createVertexLabels(management);
				createEdgeLabels(management);
				createCompositeIndexes(management);
				management.commit();
			}
			
		} catch (Exception e) {
			management.rollback();
		}
		
	}
	
	/**
	 * Naive check as to whether the schema already exists
	 * @param management
	 * @return
	 */
	
	private boolean doesSchemaExist(
			final JanusGraphManagement management) {
		return management.getPropertyKey("class") == null ? false : true;
	}
	
	/**
	 * Create vertex labels
	 * @param management
	 */
	
	private void createVertexLabels(
			final JanusGraphManagement management) {
		management.makeVertexLabel("class").make();
	}
	
	/**
	 * Create edge labels
	 * @param management
	 */
	
	private void createEdgeLabels(
			final JanusGraphManagement management) {
		management.makeEdgeLabel("subClassOf").make();
	}
	
	/**
	 * Create vertex and edge properties
	 * @param management
	 */
	
	private void createProperties(
			final JanusGraphManagement management) {
		
		// Common vertex properties
		management.makePropertyKey("iri")
			.dataType(String.class).make();
		management.makePropertyKey("ontologyId")
			.dataType(Integer.class).make();
		management.makePropertyKey("latestWebhookEventId")
			.dataType(Long.class).make();
		management.makePropertyKey("key")
			.dataType(String.class).make();
		management.makePropertyKey("vertexId")
			.dataType(Long.class).make();
		
		// Common edge properties
		management.makePropertyKey("sourceVertexKey")
			.dataType(String.class).make();
		management.makePropertyKey("sourceVertexId")
			.dataType(Long.class).make();
		management.makePropertyKey("targetVertexKey")
			.dataType(String.class).make();
		management.makePropertyKey("targetVertexId")
			.dataType(Long.class).make();
		
	}
	
	/**
	 * Create vertex and edge composite indexes for exact match lookups
	 * @param management
	 */
	
	private void createCompositeIndexes(
			final JanusGraphManagement management) {
		
		// Vertex-centric indexes
		management.buildIndex("iriIndex", Vertex.class)
			.addKey(management.getPropertyKey("iri"))
			.buildCompositeIndex();
		management.buildIndex("ontologyIdIndex", Vertex.class)
			.addKey(management.getPropertyKey("ontologyId"))
			.buildCompositeIndex();
		management.buildIndex("keyIndex", Vertex.class)
			.addKey(management.getPropertyKey("key"))
			.buildCompositeIndex();
		management.buildIndex("vertexIdIndex", Vertex.class)
			.addKey(management.getPropertyKey("vertexId"))
			.buildCompositeIndex();
		
		// Edge-centric indexes
		management.buildIndex("sourceVertexKeyIndex", Edge.class)
			.addKey(management.getPropertyKey("sourceVertexKey"))
			.buildCompositeIndex();
		management.buildIndex("sourceVertexIdIndex", Edge.class)
			.addKey(management.getPropertyKey("sourceVertexId"))
			.buildCompositeIndex();
		management.buildIndex("targetVertexKeyIndex", Edge.class)
			.addKey(management.getPropertyKey("targetVertexKey"))
			.buildCompositeIndex();
		management.buildIndex("targetVertexIdIndex", Edge.class)
			.addKey(management.getPropertyKey("targetVertexId"))
			.buildCompositeIndex();
		
	}

}
