package ai.hyperlearning.ontopop.data.ontology.modeller.graph.function;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.ontology.modeller.graph.OntologyGraphModellerService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Graph Modeller Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyGraphModellerFunction implements Consumer<String> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphModellerFunction.class);
    
    @Autowired
    private OntologyGraphModellerService ontologyGraphModellerService;
    
    @Override
    public void accept(String message) {
        
        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage = mapper.readValue(
                    message, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology parsed event detected and consumed "
                    + "via the shared messaging service and the "
                    + "parsedConsumptionChannel channel.");
            LOGGER.debug("Ontology parsed message payload: {}", message);

            // Run the Ontology Property Graph modelling service pipeline
            ontologyGraphModellerService.run(ontologyMessage);

        } catch (JsonProcessingException e) {

            LOGGER.info("New parsed event detected and consumed via "
                    + "the shared messaging service and the "
                    + "parsedConsumptionChannel channel.");
            LOGGER.info("The parsed object is NOT an ontology. Skipping.");

        }
        
    }

}
