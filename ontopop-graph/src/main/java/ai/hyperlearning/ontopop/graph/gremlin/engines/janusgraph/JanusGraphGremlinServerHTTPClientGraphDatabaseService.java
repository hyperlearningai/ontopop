package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.server.http.GremlinServerHTTPClientGraphDatabaseService;

/**
 * JanusGraph Gremlin Server HTTP Client Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.graph.service",
        havingValue = "janusgraph-http")
public class JanusGraphGremlinServerHTTPClientGraphDatabaseService 
        extends GremlinServerHTTPClientGraphDatabaseService {
    
    @Autowired
    @Qualifier("janusGraphGremlinServerHttpClient")
    private Client janusGraphGremlinServerHttpClient;
    
    public JanusGraphGremlinServerHTTPClientGraphDatabaseService() {
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
    public Client openGraph() throws IOException {
        super.client = janusGraphGremlinServerHttpClient;
        return client;
    }
    
    /**************************************************************************
     * SCHEMA MANAGEMENT
     *************************************************************************/

    @Override
    public void createSchema() {

        // Pending

    }

}
