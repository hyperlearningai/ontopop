package ai.hyperlearning.ontopop.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.aws.neptune.AwsNeptuneGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.tinkergraph.TinkerGraphDatabaseService;

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
	private TinkerGraphDatabaseService tinkerGraphDatabaseService;
	
	@Autowired
	private AwsNeptuneGraphDatabaseService awsNeptuneGraphDatabaseService;
	
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
			case TINKERGRAPH:
				return tinkerGraphDatabaseService;
			case AWS_NEPTUNE:
				return awsNeptuneGraphDatabaseService;
			default:
				return gremlinGraphDatabaseService;
		}
		
	}
	
	public GraphDatabaseService getGraphDatabaseService(
			GraphDatabaseServiceType graphDatabaseServiceType) {
			
		switch ( graphDatabaseServiceType ) {
			case GREMLIN_GRAPH:
				return gremlinGraphDatabaseService;
			case TINKERGRAPH:
				return tinkerGraphDatabaseService;
			case AWS_NEPTUNE:
				return awsNeptuneGraphDatabaseService;
			default:
				return gremlinGraphDatabaseService;
		}
		
	}

}
