package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.ingestor.invoker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Ontology Ingestor Invoker Function - AWS Lambda Handler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Deprecated(since = "2.0.0", forRemoval = true)
public class OntologyIngestorInvokerAwsLambdaHandler 
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String ENV_VARIABLE_NAME_ONTOLOGY_INGESTOR_URL = 
            "ONTOLOGY_INGESTOR_URL";
    private static final Set<String> HEADER_PATTERNS_TO_KEEP = Stream.of(
            "x-github", "x-hub")
            .collect(Collectors.toCollection(HashSet::new));
    private final ExecutorService executorService = 
            Executors.newFixedThreadPool(2);
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .executor(executorService)
            .build();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent event, Context context) {
        
        // Get the request headers and payload
        Map<String, String> headers = event.getHeaders();
        String payload = event.getBody().toString();
        
        // Log the HTTP request headers for debugging purposes
        LambdaLogger logger = context.getLogger();
        logger.log("Ontology Ingestor Function triggered: "
                + "New HTTP POST request - Ontology ingestion webhook.");
        headers.forEach((key, value) -> {
            logger.log(String.format("Header '%s' = %s", key, value));
        });
        
        // Log the HTTP request body payload for debugging purposes
        logger.log("Ontology ingestion webhook payload: " + payload);
        
        // Send a HTTP POST request to the Ontology Ingestor Lambda Function
        // Build the HTTP POST request
        String ontologyIngestorUrl = 
                System.getenv(ENV_VARIABLE_NAME_ONTOLOGY_INGESTOR_URL);
        logger.log("Identified Ontology Ingestor URL: " + ontologyIngestorUrl);
        Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .uri(URI.create(ontologyIngestorUrl))
                .header("Content-Type", "application/json");
        for (String headerPattern : HEADER_PATTERNS_TO_KEEP) {
            for (var entry : headers.entrySet()) {
                if ( entry.getKey().startsWith(headerPattern) )
                    requestBuilder.setHeader(entry.getKey(), entry.getValue());
            }
        }
        HttpRequest request = requestBuilder.build();
        logger.log("Sending Headers: " + request.headers().map());
        
        // Asynchronously send the HTTP POST request
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        logger.log("Sent async HTTP POST request to: " + request.uri());
        
        // Return a response
        APIGatewayProxyResponseEvent response = 
                new APIGatewayProxyResponseEvent();
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        return response;
        
    }

}
