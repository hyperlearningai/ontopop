package ai.hyperlearning.ontology.services.api.graph.config;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

/**
 * Application Shutdown Callback
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@Component
public class GraphAPIApplicationShutdownCallback {
	
	@PreDestroy
    public void destroy() {
		
		// Gracefully close the connection to the graph database manager
		// If TinkerGraph is being used, then this will cause the graph
		// to be automatically serialized and written to disk
//		if ( GraphAPIStaticObjects.getGraphDatabaseManager() != null )
//			GraphAPIStaticObjects.closeGraphDatabaseManager();
		
	}

}
