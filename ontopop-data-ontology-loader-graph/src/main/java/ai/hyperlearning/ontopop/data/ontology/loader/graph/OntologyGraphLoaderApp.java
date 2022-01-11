package ai.hyperlearning.ontopop.data.ontology.loader.graph;

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

import ai.hyperlearning.ontopop.messaging.processors.DataPipelineModelledLoaderSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Graph Loading Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineModelledLoaderSource.class)
public class OntologyGraphLoaderApp {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphLoaderApp.class);

    @Autowired
    private OntologyGraphLoaderService ontologyGraphLoaderService;

    public static void main(String[] args) {
        SpringApplication.run(OntologyGraphLoaderApp.class, args);
    }

    @StreamListener("modelledConsumptionChannel")
    public void processModelledOntology(String payload) {

        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage =
                    mapper.readValue(payload, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology modelled event detected and consumed "
                    + "via the shared messaging service and the "
                    + "modelledConsumptionChannel channel.");
            LOGGER.debug("Ontology modelled message payload: {}", payload);

            // Run the Ontology Graph Loading Service pipeline
            ontologyGraphLoaderService.run(ontologyMessage);

        } catch (JsonProcessingException e) {

            LOGGER.info("New modelled event detected and consumed via "
                    + "the shared messaging service and the "
                    + "modelledConsumptionChannel channel.");
            LOGGER.info("The validated object is NOT an ontology. Skipping.");

        }

    }

}
