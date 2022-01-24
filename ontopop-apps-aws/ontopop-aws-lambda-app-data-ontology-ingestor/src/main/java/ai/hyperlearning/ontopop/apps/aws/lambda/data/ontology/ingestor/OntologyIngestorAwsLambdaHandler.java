package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.ingestor;

import java.util.Map;

import org.springframework.http.HttpStatus;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunctionModel;

/**
 * Ontology Ingestor Function - AWS Lambda Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyIngestorAwsLambdaHandler 
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent event, Context context) {
        
        // Get the request headers and payload
        Map<String, String> headers = event.getHeaders();
        String payload = event.getBody().toString();
        OntologyIngestorFunctionModel ontologyIngestorFunctionModel =
                new OntologyIngestorFunctionModel(headers, payload);
        
        // Log the HTTP request headers for debugging purposes
        LambdaLogger logger = context.getLogger();
        logger.log("Ontology Ingestor Function triggered: "
                + "New HTTP POST request - Ontology ingestion webhook.");
        headers.forEach((key, value) -> {
            logger.log(String.format("Header '%s' = %s", key, value));
        });
        
        // Log the HTTP request body payload for debugging purposes
        logger.log("Ontology ingestion webhook payload: " + payload);
        
        // Execute the Ontology Ingestion function
        // handleRequest(request, context);
        
        // Return a response
        APIGatewayProxyResponseEvent response = 
                new APIGatewayProxyResponseEvent();
        response.setIsBase64Encoded(false);
        response.setStatusCode(HttpStatus.OK.value());
        response.setBody(ontologyIngestorFunctionModel.toString());
        return response;
        
    }

}
