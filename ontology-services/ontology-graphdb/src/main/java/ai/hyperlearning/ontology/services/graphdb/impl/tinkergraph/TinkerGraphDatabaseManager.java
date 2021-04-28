package ai.hyperlearning.ontology.services.graphdb.impl.tinkergraph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;

/**
 * Apache TinkerPop TinkerGraph Graph Database Manager
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class TinkerGraphDatabaseManager extends GraphDatabaseManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(
			TinkerGraphDatabaseManager.class);

	public TinkerGraphDatabaseManager(String configurationFilename) {
		super(configurationFilename);
		this.supportsSchema = true;
		this.supportsTransactions = false;
        this.supportsGeoshape = false;
	}
	
	@Override
    public void createSchema() {
		
        LOGGER.info("Creating the TinkerGraph graph schema");
        final TinkerGraph tinkerGraph = (TinkerGraph) graph;
        
        // Create vertex-centric indices
        if (!tinkerGraph.getIndexedKeys(Vertex.class).iterator().hasNext()) {
            tinkerGraph.createIndex("id", Vertex.class);
            tinkerGraph.createIndex("nodeId", Vertex.class);
            tinkerGraph.createIndex("rdfAbout", Vertex.class);
            tinkerGraph.createIndex(
            		GraphDatabaseManager.PROPERTY_KEY_USER_DEFINED, 
            		Vertex.class);
            tinkerGraph.createIndex(
            		GraphDatabaseManager.PROPERTY_KEY_USER_ID, 
            		Vertex.class);
        }
        
        // Create edge-centric indices
        if (!tinkerGraph.getIndexedKeys(Edge.class).iterator().hasNext()) {
        	tinkerGraph.createIndex("id", Edge.class);
        	tinkerGraph.createIndex("edgeId", Edge.class);
        	tinkerGraph.createIndex("objectPropertyRdfAbout", Edge.class);
        	tinkerGraph.createIndex("objectPropertyRdfLabel", Edge.class);
        }
        
    }

}
