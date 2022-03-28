package ai.hyperlearning.ontopop.apps.azure.functions.data.ontology.pipeline;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.AccessRights;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;

/**
 * Ontology Post-Ingestion End-to-End ETL Pipeline Function - Azure Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyPipelineAzureFunctionHandler 
        extends FunctionInvoker<String, Void> {
    
    @FunctionName("ontologyPipelineFunction")
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
        context.getLogger().info("Ontology Pipeline Function triggered: "
                + "New service bus event - Ontology ingestion event.");
        context.getLogger().info("Service bus message payload: " + message);
        
        // Execute the Ontology Pipeline function
        handleRequest(message, context);
        
    }

}
