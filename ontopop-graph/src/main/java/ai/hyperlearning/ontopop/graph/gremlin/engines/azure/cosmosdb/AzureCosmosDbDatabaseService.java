package ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.server.client.GremlinServerClientGraphDatabaseService;

/**
 * Microsoft Azure CosmosDB Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.graph.service",
        havingValue = "azure-cosmosdb")
public class AzureCosmosDbDatabaseService
        extends GremlinServerClientGraphDatabaseService {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AzureCosmosDbDatabaseService.class);

    @Autowired
    @Qualifier("azureCosmosDbgremlinServerClient")
    private Client azureCosmosDbgremlinServerClient;

    public AzureCosmosDbDatabaseService() {
        super.supportsUserDefinedIds = true;
        super.supportsNonStringIds = false;
        super.supportsSchema = false;
        super.supportsTransactions = false;
        super.supportsGeoshape = false;
        super.supportsTraversalsBy = false;
    }

    /**************************************************************************
     * GRAPH INSTANCE MANAGEMENT
     *************************************************************************/

    @Override
    public Client openGraph() throws IOException {
        super.client = azureCosmosDbgremlinServerClient;
        return client;
    }
    
    @Override
    public void deleteGraph() {
        client.submit(AzureCosmosDbGremlinRecipes.deleteVertices());
        client.submit(AzureCosmosDbGremlinRecipes.deleteEdges());
    }
    
    @Override
    public void deleteSubGraph(String propertyKey, Object propertyValue) {
        client.submit(AzureCosmosDbGremlinRecipes
                .deleteVertices(propertyKey, propertyValue));
        client.submit(AzureCosmosDbGremlinRecipes
                .deleteEdges(propertyKey, propertyValue));
    }
    
    /**************************************************************************
     * VERTEX MANAGEMENT
     *************************************************************************/
    
    @Override
    public void deleteVertices()
            throws InterruptedException, ExecutionException {
        String query = AzureCosmosDbGremlinRecipes.deleteVertices();
        LOGGER.debug("Gremlin Query - Delete Vertices: {}", query);
        client.submit(query).all().get();
    }

    @Override
    public void deleteVertices(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = AzureCosmosDbGremlinRecipes
                .deleteVertices(propertyKey, propertyValue);
        LOGGER.debug("Gremlin Query - Delete Vertices: {}", query);
        client.submit(query).all().get();
    }
    
    /**************************************************************************
     * EDGE MANAGEMENT
     *************************************************************************/
    
    @Override
    public void deleteEdges() throws InterruptedException, ExecutionException {
        String query = AzureCosmosDbGremlinRecipes.deleteEdges();
        LOGGER.debug("Gremlin Query - Delete Edges: {}", query);
        client.submit(query).all().get();
    }

    @Override
    public void deleteEdges(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = AzureCosmosDbGremlinRecipes
                .deleteEdges(propertyKey, propertyValue);
        LOGGER.debug("Gremlin Query - Delete Edges: {}", query);
        client.submit(query).all().get();
    }

}
