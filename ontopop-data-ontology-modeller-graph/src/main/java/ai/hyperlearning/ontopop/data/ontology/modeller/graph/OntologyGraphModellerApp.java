package ai.hyperlearning.ontopop.data.ontology.modeller.graph;

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
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineModellerSource;
import ai.hyperlearning.ontopop.model.ontology.OntologyMessage;

/**
 * Ontology Property Graph Modelling Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineModellerSource.class)
public class OntologyGraphModellerApp {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphModellerApp.class);

    @Autowired
    private OntologyGraphModellerService ontologyGraphModellerService;

    public static void main(String[] args) {
        SpringApplication.run(OntologyGraphModellerApp.class, args);
    }

    @StreamListener("parsedConsumptionChannel")
    public void processParsedOntology(String payload) {

        try {

            // Explicitly check that the string payload
            // models an OntologyMessage object
            ObjectMapper mapper = new ObjectMapper();
            OntologyMessage ontologyMessage =
                    mapper.readValue(payload, OntologyMessage.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New ontology parsed event detected and consumed "
                    + "via the shared messaging service and the "
                    + "parsedConsumptionChannel channel.");
            LOGGER.debug("Ontology parsed message payload: {}", payload);

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
