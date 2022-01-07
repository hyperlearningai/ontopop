package ai.hyperlearning.ontopop.graph.gremlin.server.remoteconnection;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;

/**
 * Gremlin Server Bytecode Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GremlinServerRemoteConnectionGraphDatabaseService extends GremlinGraphDatabaseService {
	
	@Autowired
	@Qualifier("gremlinServerTraversalSource")
	private GraphTraversalSource gremlinServerTraversalSource;
	
	/**************************************************************************
	 * GRAPH INSTANCE MANAGEMENT
	 *************************************************************************/
	
	@Override
	public GraphTraversalSource openGraph() throws IOException {
		super.g = gremlinServerTraversalSource;
		bindings.putIfAbsent("g", g);
		bindings.putIfAbsent("graph", g.getGraph());
		gremlinExecutor = GremlinExecutor.build()
				.evaluationTimeout(15000L)
				.globalBindings(bindings)
				.create();
		return g;
	}

}
