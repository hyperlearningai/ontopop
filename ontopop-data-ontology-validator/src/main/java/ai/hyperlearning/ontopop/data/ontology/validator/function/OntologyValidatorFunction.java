package ai.hyperlearning.ontopop.data.ontology.validator.function;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ai.hyperlearning.ontopop.data.ontology.validator.OntologyValidatorService;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Validator Cloud Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyValidatorFunction 
        implements Function<OntologyValidatorFunctionModel, Boolean> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyValidatorFunction.class);
    
    @Autowired
    private OntologyValidatorService ontologyValidatorService;

    @Override
    public Boolean apply(
            OntologyValidatorFunctionModel ontologyValidatorFunctionModel) {
        
        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage =
                    mapper.readValue(
                            ontologyValidatorFunctionModel.getPayload(), 
                            OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology ingestion event detected and consumed "
                    + "via the cloud-based shared messaging service and the "
                    + "ingestedConsumptionChannel channel.");
            LOGGER.debug("Ontology ingestion message payload: {}", 
                    ontologyValidatorFunctionModel.getPayload());

            // Run the Ontology Validation Service pipeline
            ontologyValidatorService.run(ontologyMessage);
            return true;

        } catch (JsonProcessingException e) {

            LOGGER.info("New ingestion event detected and consumed via "
                    + "the cloud-based shared messaging service and the "
                    + "ingestedConsumptionChannel channel.");
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");

        }
        
        return false;
        
    }

}
