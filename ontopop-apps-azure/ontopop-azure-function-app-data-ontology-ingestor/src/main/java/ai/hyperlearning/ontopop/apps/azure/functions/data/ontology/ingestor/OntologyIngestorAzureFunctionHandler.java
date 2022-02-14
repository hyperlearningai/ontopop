package ai.hyperlearning.ontopop.apps.azure.functions.data.ontology.ingestor;

import java.util.Map;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AccessRights;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;

import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunctionModel;

/**
 * Ontology Ingestor Function - Azure Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyIngestorAzureFunctionHandler
        extends FunctionInvoker<OntologyIngestorFunctionModel, Void> {
    
    private static final String PAYLOAD_HEADERS_KEY = "headers";

    /**
     * HTTP TRIGGER
     * Azure handler for the Ontology Ingestor cloud function. FunctionInvoker
     * links the Azure Function with the Spring Cloud Function. FunctionInvoker
     * also provides the handleRequest() method. Finally the function name
     * annotation uses the name of the OntologyIngestorFunction component which
     * is a Spring Bean with the same class name but starting with a lowercase
     * character.
     * 
     * @param request
     * @param context
     * @return
     */

    @FunctionName("ontologyIngestorFunction")
    public HttpResponseMessage ingest(
            @HttpTrigger(
                    name = "request",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS) 
                HttpRequestMessage<String> request,
            ExecutionContext context) {

        // Get the request headers and payload
        Map<String, String> headers = request.getHeaders();
        String payload = request.getBody().toString();
        OntologyIngestorFunctionModel ontologyIngestorFunctionModel =
                new OntologyIngestorFunctionModel(headers, payload);

        // Log the HTTP request headers for debugging purposes
        context.getLogger().info("Ontology Ingestor Function triggered: "
                + "New HTTP POST request - Ontology ingestion webhook.");
        headers.forEach((key, value) -> {
            context.getLogger()
                    .info(String.format("Header '%s' = %s", key, value));
        });

        // Log the HTTP request body payload for debugging purposes
        context.getLogger()
                .info("Ontology ingestion webhook payload: " + payload);

        // Execute the Ontology Ingestion function
        handleRequest(ontologyIngestorFunctionModel, context);

        // Return a response entity
        return request.createResponseBuilder(HttpStatus.OK)
                .body(ontologyIngestorFunctionModel)
                .header("Content-Type", "application/json").build();

    }
    
    /**
     * AZURE SERVICE BUS TRIGGER
     * @param message
     * @param context
     */
    
    @FunctionName("ontologyIngestorFunction")
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
        context.getLogger().info("Ontology Ingestor Function triggered: "
                + "New service bus event - Git repository update event.");
        context.getLogger().info("Service bus message payload: " + message);
        
        // Extract the headers map from the payload
        Gson gson = new Gson();
        JsonObject payloadJson = gson.fromJson(message, JsonElement.class)
                .getAsJsonObject();
        @SuppressWarnings("unchecked")
        Map<String, String> headers = gson.fromJson(
                payloadJson.get(PAYLOAD_HEADERS_KEY), Map.class);
        
        // Construct an OntologyIngestorFunctionModel object
        OntologyIngestorFunctionModel ontologyIngestorFunctionModel = 
                new OntologyIngestorFunctionModel(headers, message);
        
        // Execute the Ontology Ingestion function
        handleRequest(ontologyIngestorFunctionModel, context);
        
    }

}
