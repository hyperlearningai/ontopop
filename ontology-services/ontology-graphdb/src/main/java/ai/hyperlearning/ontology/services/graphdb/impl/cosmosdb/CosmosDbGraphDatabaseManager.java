package ai.hyperlearning.ontology.services.graphdb.impl.cosmosdb;

import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.jsr223.ConcurrentBindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

/**
 * Azure Cosmos DB Graph Database Manager
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class CosmosDbGraphDatabaseManager extends GraphDatabaseManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(
			CosmosDbGraphDatabaseManager.class);
	private Cluster cluster = null;
	
	public CosmosDbGraphDatabaseManager(String configurationFilename) {
		super(configurationFilename);
		this.supportsSchema = true;
		this.supportsTransactions = true;
        this.supportsGeoshape = false;
	}
	
	@Override
	public GraphTraversalSource openGraph() throws Exception {
		
		LOGGER.info("Opening the Azure Cosmos DB graph engine");
		cluster = Cluster.build(
				ResourceUtils.getFile("classpath:" + configurationFilename))
				.create();
		g = traversal().withRemote(DriverRemoteConnection.using(cluster));
		
		LOGGER.info("Opening a Gremlin Executor Engine");
		bindings = new ConcurrentBindings();
		bindings.putIfAbsent("g", g);
		gremlinExecutor = GremlinExecutor.build()
				.evaluationTimeout(15000L)
				.globalBindings(bindings)
				.create();
		return g;
		
	}
	
	@Override
	public void closeGraph() throws Exception {
		
		LOGGER.info("Closing the Azure Cosmos DB graph engine");
		try {
			if (gremlinExecutor != null)
				gremlinExecutor.close();
            if (g != null)
                g.close();
            if (cluster != null)
            	cluster.close();
        } catch (Exception e) {
        	
        } finally {
        	gremlinExecutor = null;
        	g = null;
            cluster = null;
        }
		
	}

}
