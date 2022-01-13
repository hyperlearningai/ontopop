package ai.hyperlearning.ontopop.cloud.azure.data.ontology.validator;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;
import ai.hyperlearning.ontopop.data.ontology.validator.function.OntologyValidatorFunctionModel;

/**
 * Ontology Validator Cloud Function - Azure Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyValidatorFunctionAzureHandler 
        extends FunctionInvoker<OntologyValidatorFunctionModel, Boolean> {
    
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
                    name = "ontologyValidatorServiceBusTopicTrigger",
                    topicName = "%TOPIC_NAME%",
                    subscriptionName = "%SUBSCRIPTION_NAME%",
                    connection = "AZURE_SERVICEBUS_CONNECTION_STRING") 
                String message,
        final ExecutionContext context) {
        
        // Execute the Ontology Validation function
        OntologyValidatorFunctionModel ontologyValidatorFunctionModel = 
                new OntologyValidatorFunctionModel(message);
        handleRequest(ontologyValidatorFunctionModel, context);
        
    }
    

}
