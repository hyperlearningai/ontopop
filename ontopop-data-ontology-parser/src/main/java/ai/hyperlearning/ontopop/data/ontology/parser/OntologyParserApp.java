package ai.hyperlearning.ontopop.data.ontology.parser;

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

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineParserSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Parsing Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineParserSource.class)
public class OntologyParserApp {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyParserApp.class);

    @Autowired
    private OntologyParserService ontologyParserService;

    public static void main(String[] args) {
        SpringApplication.run(OntologyParserApp.class, args);
    }

    @StreamListener("validatedConsumptionChannel")
    public void processValidatedOntology(String payload) {

        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage =
                    mapper.readValue(payload, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology validation event detected and consumed "
                    + "via the shared messaging service and the "
                    + "validatedConsumptionChannel channel.");
            LOGGER.debug("Ontology validation message payload: {}", payload);

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
