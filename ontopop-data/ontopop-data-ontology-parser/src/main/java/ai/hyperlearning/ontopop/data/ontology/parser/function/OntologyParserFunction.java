package ai.hyperlearning.ontopop.data.ontology.parser.function;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.ontology.parser.OntologyParserService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Parser Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyParserFunction implements Consumer<String> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyParserFunction.class);
    
    @Autowired
    private OntologyParserService ontologyParserService;
    
    @Override
    public void accept(String message) {
        
        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage = mapper.readValue(
                    message, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology validation event detected and consumed "
                    + "via the shared messaging service and the "
                    + "validatedConsumptionChannel channel.");
            LOGGER.debug("Ontology validation message payload: {}", message);

            // Run the Ontology Parsing Service pipeline
            ontologyParserService.run(ontologyMessage);

        } catch (JsonProcessingException e) {

            LOGGER.info("New validation event detected and consumed via "
                    + "the shared messaging service and the "
                    + "validatedConsumptionChannel channel.");
            LOGGER.info("The validated object is NOT an ontology. Skipping.");

        }
        
    }

}
