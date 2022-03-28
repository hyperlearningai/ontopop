package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ai.hyperlearning.ontopop.graph.gremlin.server.http.GremlinServerHttpWebClientGraphDatabaseService;

/**
 * JanusGraph Gremlin Server HTTP Web Client Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.graph.service",
        havingValue = "janusgraph-http")
public class JanusGraphGremlinServerHttpWebClientGraphDatabaseService 
        extends GremlinServerHttpWebClientGraphDatabaseService {
    
    @Autowired
    @Qualifier("janusGraphGremlinServerHttpWebClient")
    private WebClient janusGraphGremlinServerHttpWebClient;
    
    public JanusGraphGremlinServerHttpWebClientGraphDatabaseService() {
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
    public WebClient openGraph() throws IOException {
        super.client = janusGraphGremlinServerHttpWebClient;
        return client;
    }
    
    /**************************************************************************
     * SCHEMA MANAGEMENT
     *************************************************************************/

    @Override
    public void createSchema() {
        // See ontopop-utils:src/main/groovy/janusgraph-create-schema.groovy
    }

}
