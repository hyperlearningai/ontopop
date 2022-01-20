package ai.hyperlearning.ontopop.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb.AzureCosmosDbGremlinServerHTTPClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphGremlinServerHTTPClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphGremlinServerWebSocketClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.tinkergraph.TinkerGraphEmbeddedGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.http.GremlinServerHTTPClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.websocket.GremlinServerWebSocketClientGraphDatabaseService;

/**
 * Graph Database Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GraphDatabaseServiceFactory {

    @Autowired(required = false)
    private GremlinGraphDatabaseService gremlinGraphDatabaseService;

    @Autowired(required = false)
    private GremlinServerWebSocketClientGraphDatabaseService gremlinServerWebSocketClientGraphDatabaseService;

    @Autowired(required = false)
    private GremlinServerHTTPClientGraphDatabaseService gremlinServerHTTPClientGraphDatabaseService;
    
    @Autowired(required = false)
    private JanusGraphGremlinServerWebSocketClientGraphDatabaseService janusGraphGremlinServerWebSocketClientGraphDatabaseService;
    
    @Autowired(required = false)
    private JanusGraphGremlinServerHTTPClientGraphDatabaseService janusGraphGremlinServerHTTPClientGraphDatabaseService;
    
    @Autowired(required = false)
    private AzureCosmosDbGremlinServerHTTPClientGraphDatabaseService azureCosmosDbGremlinServerHTTPClientGraphDatabaseService;

    @Autowired(required = false)
    private TinkerGraphEmbeddedGraphDatabaseService tinkerGraphEmbeddedGraphDatabaseService;

    /**
     * Select the relevant object storage service
     * 
     * @param type
     * @return
     */

    public GraphDatabaseService getGraphDatabaseService(String type) {

        GraphDatabaseServiceType graphDatabaseServiceType =
                GraphDatabaseServiceType.valueOfLabel(type.toUpperCase());
        switch (graphDatabaseServiceType) {
            case GREMLIN_GRAPH:
                return gremlinGraphDatabaseService;
            case GREMLIN_SERVER_WS:
                return gremlinServerWebSocketClientGraphDatabaseService;
            case GREMLIN_SERVER_HTTP:
                return gremlinServerHTTPClientGraphDatabaseService;
            case JANUSGRAPH_WS:
                return janusGraphGremlinServerWebSocketClientGraphDatabaseService;
            case JANUSGRAPH_HTTP:
                return janusGraphGremlinServerHTTPClientGraphDatabaseService;
            case AZURE_COSMOSDB:
                return azureCosmosDbGremlinServerHTTPClientGraphDatabaseService;
            case TINKERGRAPH:
                return tinkerGraphEmbeddedGraphDatabaseService;
            default:
                return gremlinServerHTTPClientGraphDatabaseService;
        }

    }

    public GraphDatabaseService getGraphDatabaseService(
            GraphDatabaseServiceType graphDatabaseServiceType) {

        switch (graphDatabaseServiceType) {
            case GREMLIN_GRAPH:
                return gremlinGraphDatabaseService;
            case GREMLIN_SERVER_WS:
                return gremlinServerWebSocketClientGraphDatabaseService;
            case GREMLIN_SERVER_HTTP:
                return gremlinServerHTTPClientGraphDatabaseService;
            case JANUSGRAPH_WS:
                return janusGraphGremlinServerWebSocketClientGraphDatabaseService;
            case JANUSGRAPH_HTTP:
                return janusGraphGremlinServerHTTPClientGraphDatabaseService;
            case AZURE_COSMOSDB:
                return azureCosmosDbGremlinServerHTTPClientGraphDatabaseService;
            case TINKERGRAPH:
                return tinkerGraphEmbeddedGraphDatabaseService;
            default:
                return gremlinServerHTTPClientGraphDatabaseService;
        }

    }

}
