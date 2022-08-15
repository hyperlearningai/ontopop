package ai.hyperlearning.ontopop.data.ontology.pipeline.function;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.ontology.indexer.graph.OntologyGraphIndexerService;
import ai.hyperlearning.ontopop.data.ontology.loader.graph.OntologyGraphLoaderService;
import ai.hyperlearning.ontopop.data.ontology.loader.triplestore.OntologyTriplestoreLoaderService;
import ai.hyperlearning.ontopop.data.ontology.modeller.graph.OntologyGraphModellerService;
import ai.hyperlearning.ontopop.data.ontology.parser.OntologyParserService;
import ai.hyperlearning.ontopop.data.ontology.validator.OntologyValidatorService;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineException;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Post-Ingestion End-to-End ETL Pipeline Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyPipelineFunction implements Consumer<String> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyPipelineFunction.class);
    
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
        
        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage = mapper.readValue(
                    message, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology ingestion event detected and consumed "
                    + "via the shared messaging service and the "
                    + "ingestedConsumptionChannel channel.");
            LOGGER.debug("Ontology ingestion message payload: {}", message);
            
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

        } catch (OntologyDataPipelineException e) {
            
            LOGGER.error("The Ontology Data Pipeline encountered an error.", e);
        
        } catch (JsonProcessingException e) {

            LOGGER.info("New ingestion event detected and consumed via "
                    + "the shared messaging service and the "
                    + "ingestedConsumptionChannel channel.");
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");

        } catch (InterruptedException e) {
            
            LOGGER.error("Ontology Post-Ingestion End-to-End ETL Pipeline "
                    + "was interrupted.", e);
            
        }
        
    }

}
