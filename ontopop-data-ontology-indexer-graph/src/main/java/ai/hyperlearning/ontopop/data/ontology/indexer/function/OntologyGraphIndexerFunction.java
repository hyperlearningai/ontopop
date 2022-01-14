package ai.hyperlearning.ontopop.data.ontology.indexer.function;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.ontology.indexer.OntologyGraphIndexerService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Graph Indexer Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyGraphIndexerFunction 
        implements Function<OntologyGraphIndexerFunctionModel, Boolean> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphIndexerFunction.class);
    
    @Autowired
    private OntologyGraphIndexerService ontologyGraphIndexerService;
    
    @Override
    public Boolean apply(
            OntologyGraphIndexerFunctionModel ontologyGraphIndexerFunctionModel) {
        
        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage =
                    mapper.readValue(
                            ontologyGraphIndexerFunctionModel.getPayload(), 
                            OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology modelled event detected and consumed "
                    + "via the shared messaging service and the "
                    + "modelledConsumptionChannel channel.");
            LOGGER.debug("Ontology modelled message payload: {}", 
                    ontologyGraphIndexerFunctionModel.getPayload());

            // Run the Ontology Indexing Service pipeline
            ontologyGraphIndexerService.run(ontologyMessage);

        } catch (JsonProcessingException e) {

            LOGGER.info("New modelled event detected and consumed via "
                    + "the shared messaging service and the "
                    + "modelledConsumptionChannel channel.");
            LOGGER.info("The validated object is NOT an ontology. Skipping.");

        }
        
        return true;
        
    }

}
