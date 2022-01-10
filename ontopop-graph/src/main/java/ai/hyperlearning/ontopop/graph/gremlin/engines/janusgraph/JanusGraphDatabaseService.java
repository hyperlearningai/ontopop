package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;

/**
 * TinkerGraph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value="storage.graph.service", 
        havingValue = "janusgraph")
public class JanusGraphDatabaseService extends GremlinGraphDatabaseService {
    
    @Autowired
    @Qualifier("janusGraphGremlinServerTraversalSource")
    private GraphTraversalSource janusGraphGremlinServerTraversalSource;
	
	public JanusGraphDatabaseService() {
	    super.supportsUserDefinedIds = false;
		super.supportsNonStringIds = true;
		super.supportsSchema = true;
		super.supportsTransactions = true;
		super.supportsGeoshape = true;
		super.supportsTraversalsBy = true;
	}
	
	/**************************************************************************
     * GRAPH INSTANCE MANAGEMENT
     *************************************************************************/
    
	@Override
    public GraphTraversalSource openGraph() throws IOException {
        super.g = janusGraphGremlinServerTraversalSource;
        bindings.putIfAbsent("g", g);
        bindings.putIfAbsent("graph", g.getGraph());
        gremlinExecutor = GremlinExecutor.build()
                .evaluationTimeout(15000L)
                .globalBindings(bindings)
                .create();
        return g;
    }
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/

	@Override
	public void createSchema() {
		
		// See ontopop-utils: 
	    // ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphCreateSchemaApp
	    // This was moved to a separate module due to the fact that
	    // JanusGraph v0.6.0 only supports Java 8. Consequently the 
	    // janusgraph-core (and hence janusgraph-all) Maven dependency
	    // required to access the JanusGraph Management API
	    // introduces breaking conflicts such as java.util base package
	    // conflicts via the com.boundary:high-scale-lib transitive
	    // dependency and other conflicts with the Spring Framework.
		
	}

}
