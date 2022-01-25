package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.ingestor;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import ai.hyperlearning.ontopop.data.ontology.ingestor.OntologyIngestorService;

/**
 * Ontology Ingestor Cloud Function - AWS API Gateway Proxy Request Event Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyIngestorAwsLambdaEventConsumer 
        implements Consumer<APIGatewayProxyRequestEvent> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OntologyIngestorAwsLambdaEventConsumer.class);
    
    @Autowired
    private OntologyIngestorService ontologyIngestorService;

    @Override
    public void accept(APIGatewayProxyRequestEvent event) {
        
        // Log the HTTP request headers for debugging purposes
        LOGGER.debug("New HTTP POST request: Ontology ingestion webhook.");
        event.getHeaders().forEach((key, value) -> {
            LOGGER.debug(String.format("Header '%s' = %s", key, value));
        });

        // Log the HTTP request body payload for debugging purposes
        LOGGER.debug("Ontology ingestion webhook payload: {}", event.getBody());

        // Run the Ontology Ingestion Service pipeline
        ontologyIngestorService.run(event.getHeaders(), event.getBody());
        
    }

}
