package ai.hyperlearning.ontopop.apps.azure.functions.data.ontology.validator;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.AccessRights;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;

/**
 * Ontology Validator Function - Azure Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyValidatorAzureFunctionHandler 
        extends FunctionInvoker<String, Void> {
    
    /**
     * Azure handler for the Ontology Validator cloud function.
     * Note that %TOPIC_NAME% and %SUBSCRIPTION_NAME% must be set as 
     * environmental variables or Azure Function application settings. 
     * The connection annotation value is the name of a variable
     * in the Azure Function application settings, so does not need to be
     * surrounded with % symbols. Note that Azure Functions expects 
     * environmental variable references to be surrounded with % symbols 
     * regardless of the target runtime environment (i.e. Windows or Linux).
     * Finally, note that to test this function locally, 
     * you can define TOPIC_NAME, SUBSCRIPTION_NAME and 
     * AZURE_SERVICEBUS_CONNECTION_STRING as variables in 
     * src/main/azure/local.settings.json.
     * Reference: https://docs.microsoft.com/en-us/azure/azure-functions/functions-bindings-service-bus-trigger
     * Reference: https://docs.microsoft.com/en-us/java/api/com.microsoft.azure.functions.annotation.servicebustopictrigger
     * @param message
     * @param context
     */
    
    @FunctionName("ontologyValidatorFunction")
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
        context.getLogger().info("Ontology Validator Function triggered: "
                + "New service bus event - Ontology ingestion event.");
        context.getLogger().info("Service bus message payload: " + message);
        
        // Execute the Ontology Validation function
        handleRequest(message, context);
        
    }

}
