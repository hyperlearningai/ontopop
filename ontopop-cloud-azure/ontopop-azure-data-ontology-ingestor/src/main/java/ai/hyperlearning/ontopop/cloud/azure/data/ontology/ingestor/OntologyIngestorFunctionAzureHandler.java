package ai.hyperlearning.ontopop.cloud.azure.data.ontology.ingestor;

import java.util.Map;

import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunctionModel;

/**
 * Ontology Ingestor Cloud Function - Azure Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyIngestorFunctionAzureHandler
        extends FunctionInvoker<OntologyIngestorFunctionModel, Boolean> {

    /**
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
    public HttpResponseMessage ingest(@HttpTrigger(
            name = "ontologyIngestorHttpRequest",
            methods = {HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<?> request,
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

}
