package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.webprotege.exporter;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.WebProtegeExporterService;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;

/**
 * WebProtege Exporter Cloud Function - AWS API Gateway Proxy Request Event Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class WebProtegeExporterHttpAwsLambdaApiGatewayProxyRequestEventConsumer 
        implements Consumer<APIGatewayProxyRequestEvent> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            WebProtegeExporterHttpAwsLambdaApiGatewayProxyRequestEventConsumer.class);
    
    @Autowired
    private WebProtegeExporterService webProtegeExporterService;
    
    @Override
    public void accept(APIGatewayProxyRequestEvent event) {
        
        try {
            
            // Explicitly check that the string payload
            // models an WebProtegeWebhook object
            ObjectMapper mapper = new ObjectMapper();
            WebProtegeWebhook webProtegeWebhook = mapper.readValue(
                    event.getBody(), WebProtegeWebhook.class);
            
            // Log the consumed payload for debugging purposes
            LOGGER.debug("New WebProtege project updated event detected.");
            LOGGER.debug("WebProtegeWebhook message payload: {}", 
                    event.getBody());
            
            // Run the WebProtege Exporter Service pipeline
            if ( webProtegeWebhook != null ) {
                LOGGER.info("Running the WebProtege exporter service: {}", 
                        webProtegeWebhook);
                webProtegeExporterService.run(webProtegeWebhook);
            }  
            
        } catch (JsonProcessingException e) {   
            LOGGER.info("The ingested object is NOT a WebProtegeWebhook "
                    + "project updated webhook. Skipping.");
        }   
        
    }

}
