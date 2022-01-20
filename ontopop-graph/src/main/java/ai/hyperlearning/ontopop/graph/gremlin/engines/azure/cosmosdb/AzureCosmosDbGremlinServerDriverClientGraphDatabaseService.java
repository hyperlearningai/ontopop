package ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.server.driver.GremlinServerDriverClientGraphDatabaseService;

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
public class AzureCosmosDbGremlinServerDriverClientGraphDatabaseService
        extends GremlinServerDriverClientGraphDatabaseService {

    @Autowired
    @Qualifier("azureCosmosDbGremlinServerDriverClient")
    private Client azureCosmosDbGremlinServerDriverClient;

    public AzureCosmosDbGremlinServerDriverClientGraphDatabaseService() {
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
        super.client = azureCosmosDbGremlinServerDriverClient;
        return client;
    }

}
