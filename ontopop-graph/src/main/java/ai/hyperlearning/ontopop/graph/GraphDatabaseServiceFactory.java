package ai.hyperlearning.ontopop.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb.AzureCosmosDbDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph.JanusGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.engines.tinkergraph.TinkerGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.client.GremlinServerClientGraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.server.remoteconnection.GremlinServerRemoteConnectionGraphDatabaseService;

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
    private GremlinServerRemoteConnectionGraphDatabaseService gremlinServerRemoteConnectionGraphDatabaseService;

    @Autowired(required = false)
    private GremlinServerClientGraphDatabaseService gremlinServerClientGraphDatabaseService;

    @Autowired(required = false)
    private TinkerGraphDatabaseService tinkerGraphDatabaseService;

    @Autowired(required = false)
    private JanusGraphDatabaseService janusGraphDatabaseService;

    @Autowired(required = false)
    private AzureCosmosDbDatabaseService azureCosmosDbDatabaseService;

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
            case GREMLIN_SERVER_REMOTE_CONNECTION:
                return gremlinServerRemoteConnectionGraphDatabaseService;
            case GREMLIN_SERVER_CLIENT:
                return gremlinServerClientGraphDatabaseService;
            case TINKERGRAPH:
                return tinkerGraphDatabaseService;
            case JANUSGRAPH:
                return janusGraphDatabaseService;
            case AZURE_COSMOSDB:
                return azureCosmosDbDatabaseService;
            default:
                return gremlinServerClientGraphDatabaseService;
        }

    }

    public GraphDatabaseService getGraphDatabaseService(
            GraphDatabaseServiceType graphDatabaseServiceType) {

        switch (graphDatabaseServiceType) {
            case GREMLIN_GRAPH:
                return gremlinGraphDatabaseService;
            case GREMLIN_SERVER_REMOTE_CONNECTION:
                return gremlinServerRemoteConnectionGraphDatabaseService;
            case GREMLIN_SERVER_CLIENT:
                return gremlinServerClientGraphDatabaseService;
            case TINKERGRAPH:
                return tinkerGraphDatabaseService;
            case JANUSGRAPH:
                return janusGraphDatabaseService;
            case AZURE_COSMOSDB:
                return azureCosmosDbDatabaseService;
            default:
                return gremlinServerClientGraphDatabaseService;
        }

    }

}
