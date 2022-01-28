package ai.hyperlearning.ontopop.apps.data.ontology.modeller.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.modeller.graph.function.OntologyGraphModellerFunction;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineModellerSource;

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
    
    @Autowired
    private OntologyGraphModellerFunction ontologyGraphModellerFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyGraphModellerApp.class, args);
    }
    
    @StreamListener("parsedConsumptionChannel")
    public void processParsedOntology(String payload) {
        
        // Execute the Ontology Graph Modeller Function
        ontologyGraphModellerFunction.accept(payload);
        
    }

}
