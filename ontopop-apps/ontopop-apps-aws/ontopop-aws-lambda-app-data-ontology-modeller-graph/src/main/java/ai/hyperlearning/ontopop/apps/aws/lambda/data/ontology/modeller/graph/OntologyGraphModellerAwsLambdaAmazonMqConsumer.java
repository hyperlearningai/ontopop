package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.modeller.graph;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq.AmazonMqUtils;
import ai.hyperlearning.ontopop.data.ontology.modeller.graph.OntologyGraphModellerService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Graph Modeller Cloud Function - AWS AmazonMQ/RabbitMQ Message Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyGraphModellerAwsLambdaAmazonMqConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            OntologyGraphModellerAwsLambdaAmazonMqConsumer.class);
    
    @Autowired
    private OntologyGraphModellerService ontologyGraphModellerService;

    @Override
    public void accept(String message) {
        
        // Log the consumed payload for debugging purposes
        LOGGER.info("New ontology parsed event detected and consumed "
                + "via the shared messaging service and the "
                + "parsedConsumptionChannel channel.");
        LOGGER.debug("Ontology parsed message payload: {}", message);
        
        // Extract the OntologyMessage object from the message
        OntologyMessage ontologyMessage = AmazonMqUtils
                .getOntologyMessage(message, 0);
        LOGGER.info("Extracted Ontology Message: {}", ontologyMessage);
        
        // Run the Ontology Property Graph modelling service pipeline
        if ( ontologyMessage != null )
            ontologyGraphModellerService.run(ontologyMessage);
        else
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");
        
    }

}
