package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.loader.graph;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq.AmazonMqUtils;
import ai.hyperlearning.ontopop.data.ontology.loader.graph.OntologyGraphLoaderService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Graph Loader Cloud Function - AWS AmazonMQ/RabbitMQ Message Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyGraphLoaderAwsLambdaAmazonMqMessageConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            OntologyGraphLoaderAwsLambdaAmazonMqMessageConsumer.class);
    
    @Autowired
    private OntologyGraphLoaderService ontologyGraphLoaderService;

    @Override
    public void accept(String message) {
        
        // Log the consumed payload for debugging purposes
        LOGGER.info("New ontology modelled event detected and consumed "
                + "via the shared messaging service and the "
                + "modelledConsumptionChannel channel.");
        LOGGER.debug("Ontology modelled message payload: {}", message);
        
        // Extract the OntologyMessage object from the message
        OntologyMessage ontologyMessage = AmazonMqUtils
                .getOntologyMessage(message, 0);
        LOGGER.info("Extracted Ontology Message: {}", ontologyMessage);
        
        // Run the Ontology Graph Loading Service pipeline
        if ( ontologyMessage != null )
            ontologyGraphLoaderService.run(ontologyMessage);
        else
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");
        
    }

}
