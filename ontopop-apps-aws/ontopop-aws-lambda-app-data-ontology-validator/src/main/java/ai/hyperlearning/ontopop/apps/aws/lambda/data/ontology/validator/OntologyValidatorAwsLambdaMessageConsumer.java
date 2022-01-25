package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.validator;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq.AmazonMqUtils;
import ai.hyperlearning.ontopop.data.ontology.validator.OntologyValidatorService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Validator Cloud Function - AWS AmazonMQ/RabbitMQ Message Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyValidatorAwsLambdaMessageConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OntologyValidatorAwsLambdaMessageConsumer.class);

    @Autowired
    private OntologyValidatorService ontologyValidatorService;
    
    @Override
    public void accept(String message) {
        
        // Log the consumed payload for debugging purposes
        LOGGER.info("New ontology ingestion event detected and consumed "
                + "via the shared messaging service and the "
                + "ingestedConsumptionChannel channel.");
        LOGGER.debug("Ontology ingestion message payload: {}", message);
        
        // Extract the OntologyMessage object from the message
        OntologyMessage ontologyMessage = AmazonMqUtils
                .getOntologyMessage(message, 0);
        LOGGER.info("Extracted Ontology Message: {}", ontologyMessage);
        
        // Run the Ontology Validation Service pipeline
        if ( ontologyMessage != null )
            ontologyValidatorService.run(ontologyMessage);
        else
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");
        
    }

}
