package ai.hyperlearning.ontopop.apps.azure.functions.data.ontology.indexer.graph;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.AccessRights;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;

/**
 * Ontology Graph Indexer Function - Azure Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyGraphIndexerAzureFunctionHandler 
        extends FunctionInvoker<String, Void> {
    
    @FunctionName("ontologyGraphIndexerFunction")
    public void run(
            @ServiceBusTopicTrigger(
                    name = "message",
                    topicName = "%TOPIC_NAME%",
                    subscriptionName = "%SUBSCRIPTION_NAME%",
                    connection = "AZURE_SERVICEBUS_CONNECTION_STRING", 
                    access = AccessRights.LISTEN, 
                    dataType = "string", 
                    cardinality = Cardinality.ONE, 
                    isSessionsEnabled = false) 
                String message,
            final ExecutionContext context) {
        
        // Log the service bus trigger and message for debugging purposes
        context.getLogger().info("Ontology Graph Indexer Function triggered: "
                + "New service bus event - Ontology graph modelled event.");
        context.getLogger().info("Service bus message payload: " + message);
        
        // Execute the Ontology Graph Indexer function
        handleRequest(message, context);
        
    }

}
