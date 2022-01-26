package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.indexer.graph;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq.AmazonMqUtils;
import ai.hyperlearning.ontopop.data.ontology.indexer.graph.OntologyGraphIndexerService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Graph Indexer Cloud Function - AWS AmazonMQ/RabbitMQ Message Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyGraphIndexerAwsLambdaAmazonMqMessageConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            OntologyGraphIndexerAwsLambdaAmazonMqMessageConsumer.class);
    
    @Autowired
    private OntologyGraphIndexerService ontologyGraphIndexerService;

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
        
        // Run the Ontology Indexing Service pipeline
        if ( ontologyMessage != null )
            ontologyGraphIndexerService.run(ontologyMessage);
        else
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");
        
    }

}
