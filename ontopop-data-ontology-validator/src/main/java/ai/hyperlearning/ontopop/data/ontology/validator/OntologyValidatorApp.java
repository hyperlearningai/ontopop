package ai.hyperlearning.ontopop.data.ontology.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineValidatorSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Validation Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineValidatorSource.class)
public class OntologyValidatorApp {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyValidatorApp.class);

    @Autowired
    private OntologyValidatorService ontologyValidatorService;

    public static void main(String[] args) {
        SpringApplication.run(OntologyValidatorApp.class, args);
    }

    @StreamListener("ingestedConsumptionChannel")
    public void processIngestedOntology(String payload) {

        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage =
                    mapper.readValue(payload, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology ingestion event detected and consumed "
                    + "via the shared messaging service and the "
                    + "ingestedConsumptionChannel channel.");
            LOGGER.debug("Ontology ingestion message payload: {}", payload);

            // Run the Ontology Validation Service pipeline
            ontologyValidatorService.run(ontologyMessage);

        } catch (JsonProcessingException e) {

            LOGGER.info("New ingestion event detected and consumed via "
                    + "the shared messaging service and the "
                    + "ingestedConsumptionChannel channel.");
            LOGGER.info("The ingested object is NOT an ontology. Skipping.");

        }

    }

}
