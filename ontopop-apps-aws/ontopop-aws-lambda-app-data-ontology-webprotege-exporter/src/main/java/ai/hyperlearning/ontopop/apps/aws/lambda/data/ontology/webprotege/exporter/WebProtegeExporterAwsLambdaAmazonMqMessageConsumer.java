package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.webprotege.exporter;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq.AmazonMqUtils;
import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.WebProtegeExporterService;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;

/**
 * WebProtege Exporter Cloud Function - AWS AmazonMQ/RabbitMQ Message Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class WebProtegeExporterAwsLambdaAmazonMqMessageConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            WebProtegeExporterAwsLambdaAmazonMqMessageConsumer.class);
    
    @Autowired
    private WebProtegeExporterService webProtegeExporterService;
    
    @Override
    public void accept(String message) {
        
        LOGGER.info("New WebProtege project updated event detected and "
                + "consumed via the shared messaging service and the "
                + "webProtegeProjectUpdatedConsumptionChannel channel.");
        LOGGER.info("WebProtegeWebhook message payload: {}", message);
        
        // Extract the WebProtegeWebhook object from the message
        WebProtegeWebhook webProtegeWebhook = AmazonMqUtils
                .getWebProtegeWebhookMessage(message, 0);
        LOGGER.info("Extracted WebProtegeWebhook message: {}", 
                webProtegeWebhook);
        
        // Run the WebProtege Exporter Service pipeline
        if ( webProtegeWebhook != null )
            webProtegeExporterService.run(webProtegeWebhook);
        else
            LOGGER.info("The ingested object is NOT a WebProtegeWebhook "
                    + "project updated webhook. Skipping.");
        
    }

}
