package ai.hyperlearning.ontopop.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb.AzureCosmosDbGremlinServerDriverClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphGremlinServerDriverClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphGremlinServerHttpWebClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphGremlinServerWebSocketClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.tinkergraph.TinkerGraphEmbeddedGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.driver.GremlinServerDriverClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.http.GremlinServerHttpWebClientGraphDatabaseService;
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
    private GremlinServerDriverClientGraphDatabaseService gremlinServerDriverClientGraphDatabaseService;
    
    @Autowired(required = false)
    private GremlinServerHttpWebClientGraphDatabaseService gremlinServerHttpWebClientGraphDatabaseService;
    
    @Autowired(required = false)
    private JanusGraphGremlinServerWebSocketClientGraphDatabaseService janusGraphGremlinServerWebSocketClientGraphDatabaseService;
    
    @Autowired(required = false)
    private JanusGraphGremlinServerDriverClientGraphDatabaseService janusGraphGremlinServerDriverClientGraphDatabaseService;
    
    @Autowired(required = false)
    private JanusGraphGremlinServerHttpWebClientGraphDatabaseService janusGraphGremlinServerHttpWebClientGraphDatabaseService;
    
    @Autowired(required = false)
    private AzureCosmosDbGremlinServerDriverClientGraphDatabaseService azureCosmosDbGremlinServerDriverClientGraphDatabaseService;

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
            case GREMLIN_SERVER_DRIVER:
                return gremlinServerDriverClientGraphDatabaseService;
            case GREMLIN_SERVER_HTTP:
                return gremlinServerHttpWebClientGraphDatabaseService;
            case JANUSGRAPH_WS:
                return janusGraphGremlinServerWebSocketClientGraphDatabaseService;
            case JANUSGRAPH_DRIVER:
                return janusGraphGremlinServerDriverClientGraphDatabaseService;
            case JANUSGRAPH_HTTP:
                return janusGraphGremlinServerHttpWebClientGraphDatabaseService;
            case AZURE_COSMOSDB:
                return azureCosmosDbGremlinServerDriverClientGraphDatabaseService;
            case TINKERGRAPH:
                return tinkerGraphEmbeddedGraphDatabaseService;
            default:
                return gremlinServerDriverClientGraphDatabaseService;
        }

    }

    public GraphDatabaseService getGraphDatabaseService(
            GraphDatabaseServiceType graphDatabaseServiceType) {

        switch (graphDatabaseServiceType) {
            case GREMLIN_GRAPH:
                return gremlinGraphDatabaseService;
            case GREMLIN_SERVER_WS:
                return gremlinServerWebSocketClientGraphDatabaseService;
            case GREMLIN_SERVER_DRIVER:
                return gremlinServerDriverClientGraphDatabaseService;
            case GREMLIN_SERVER_HTTP:
                return gremlinServerHttpWebClientGraphDatabaseService;
            case JANUSGRAPH_WS:
                return janusGraphGremlinServerWebSocketClientGraphDatabaseService;
            case JANUSGRAPH_DRIVER:
                return janusGraphGremlinServerDriverClientGraphDatabaseService;
            case JANUSGRAPH_HTTP:
                return janusGraphGremlinServerHttpWebClientGraphDatabaseService;
            case AZURE_COSMOSDB:
                return azureCosmosDbGremlinServerDriverClientGraphDatabaseService;
            case TINKERGRAPH:
                return tinkerGraphEmbeddedGraphDatabaseService;
            default:
                return gremlinServerDriverClientGraphDatabaseService;
        }

    }

}
