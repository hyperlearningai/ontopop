package ai.hyperlearning.ontopop.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.tinkergraph.TinkerGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.GremlinServerByteCodeGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.GremlinServerClientGraphDatabaseService;

/**
 * Graph Database Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GraphDatabaseServiceFactory {
	
	@Autowired
	private GremlinGraphDatabaseService gremlinGraphDatabaseService;
	
	@Autowired
	private GremlinServerByteCodeGraphDatabaseService gremlinServerByteCodeGraphDatabaseService;
	
	@Autowired
	private GremlinServerClientGraphDatabaseService gremlinServerClientGraphDatabaseService;
	
	@Autowired
	private TinkerGraphDatabaseService tinkerGraphDatabaseService;
	
	@Autowired
	private JanusGraphDatabaseService janusGraphDatabaseService;
	
	/**
	 * Select the relevant object storage service
	 * @param type
	 * @return
	 */
	
	public GraphDatabaseService getGraphDatabaseService(String type) {
		
		GraphDatabaseServiceType graphDatabaseServiceType = 
				GraphDatabaseServiceType.valueOfLabel(type.toUpperCase());
		switch ( graphDatabaseServiceType ) {
			case GREMLIN_GRAPH:
				return gremlinGraphDatabaseService;
			case GREMLIN_SERVER_BYTECODE:
				return gremlinServerByteCodeGraphDatabaseService;
			case GREMLIN_SERVER_CLIENT:
				return gremlinServerClientGraphDatabaseService;
			case TINKERGRAPH:
				return tinkerGraphDatabaseService;
			case JANUSGRAPH:
				return janusGraphDatabaseService;
			default:
				return gremlinServerClientGraphDatabaseService;
		}
		
	}
	
	public GraphDatabaseService getGraphDatabaseService(
			GraphDatabaseServiceType graphDatabaseServiceType) {
			
		switch ( graphDatabaseServiceType ) {
			case GREMLIN_GRAPH:
				return gremlinGraphDatabaseService;
			case GREMLIN_SERVER_BYTECODE:
				return gremlinServerByteCodeGraphDatabaseService;
			case GREMLIN_SERVER_CLIENT:
				return gremlinServerClientGraphDatabaseService;
			case TINKERGRAPH:
				return tinkerGraphDatabaseService;
			case JANUSGRAPH:
				return janusGraphDatabaseService;
			default:
				return gremlinServerClientGraphDatabaseService;
		}
		
	}

}
