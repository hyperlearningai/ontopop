package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.pipeline;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.cloud.aws.utils.amazonmq.AmazonMqUtils;
import ai.hyperlearning.ontopop.data.ontology.indexer.graph.OntologyGraphIndexerService;
import ai.hyperlearning.ontopop.data.ontology.loader.graph.OntologyGraphLoaderService;
import ai.hyperlearning.ontopop.data.ontology.loader.triplestore.OntologyTriplestoreLoaderService;
import ai.hyperlearning.ontopop.data.ontology.modeller.graph.OntologyGraphModellerService;
import ai.hyperlearning.ontopop.data.ontology.parser.OntologyParserService;
import ai.hyperlearning.ontopop.data.ontology.validator.OntologyValidatorService;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineProcessingException;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Post-Ingestion End-to-End ETL Pipeline Cloud Function - AWS AmazonMQ/RabbitMQ Message Consumer
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyPipelineAwsLambdaAmazonMqMessageConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            OntologyPipelineAwsLambdaAmazonMqMessageConsumer.class);
    
    private static final int STAGE_INTERVAL_WAIT_SECONDS = 5;
    
    @Autowired
    private OntologyValidatorService ontologyValidatorService;
    
    @Autowired
    private OntologyTriplestoreLoaderService ontologyTriplestoreLoaderService;
    
    @Autowired
    private OntologyParserService ontologyParserService;
    
    @Autowired
    private OntologyGraphModellerService ontologyGraphModellerService;
    
    @Autowired
    private OntologyGraphLoaderService ontologyGraphLoaderService;
    
    @Autowired
    private OntologyGraphIndexerService ontologyGraphIndexerService;
    
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
        
        // Run the Post-Ingestion End-to-End ETL Pipeline
        if ( ontologyMessage != null ) {
            
            try {
                
                // Run the Ontology Validation Service
                ontologyValidatorService.run(ontologyMessage);
                TimeUnit.SECONDS.sleep(STAGE_INTERVAL_WAIT_SECONDS);
                
                 // Run the Ontology Triplestore Loader Service
                ontologyTriplestoreLoaderService.run(ontologyMessage);
                TimeUnit.SECONDS.sleep(STAGE_INTERVAL_WAIT_SECONDS);
                    
                 // Run the Ontology Parser Service
                ontologyParserService.run(ontologyMessage);
                TimeUnit.SECONDS.sleep(STAGE_INTERVAL_WAIT_SECONDS);
                    
                 // Run the Ontology Graph Modeller Service
                ontologyGraphModellerService.run(ontologyMessage);
                TimeUnit.SECONDS.sleep(STAGE_INTERVAL_WAIT_SECONDS);
                    
                 // Run the Ontology Graph Loader Service
                ontologyGraphLoaderService.run(ontologyMessage);
                TimeUnit.SECONDS.sleep(STAGE_INTERVAL_WAIT_SECONDS);
                    
                 // Run the Ontology Graph Indexer Service
                ontologyGraphIndexerService.run(ontologyMessage);
                
            } catch (OntologyDataPipelineProcessingException e) {
                
                LOGGER.error("The Ontology Data Pipeline encountered "
                        + "an error.", e);
            
            } catch (InterruptedException e) {
                
                LOGGER.error("Ontology Post-Ingestion End-to-End ETL Pipeline "
                        + "was interrupted.", e);
                
            }
            
        } else
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");
        
    }

}
