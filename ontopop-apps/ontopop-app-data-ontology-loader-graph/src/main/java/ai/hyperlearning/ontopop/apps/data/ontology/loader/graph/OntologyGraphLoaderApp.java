package ai.hyperlearning.ontopop.apps.data.ontology.loader.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.loader.graph.function.OntologyGraphLoaderFunction;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineLoaderGraphSource;

/**
 * Ontology Graph Loading Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineLoaderGraphSource.class)
public class OntologyGraphLoaderApp {
    
    @Autowired
    private OntologyGraphLoaderFunction ontologyGraphLoaderFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyGraphLoaderApp.class, args);
    }
    
    @StreamListener("modelledGraphLoaderConsumptionChannel")
    public void processModelledOntology(String payload) {
        
        // Execute the Ontology Graph Loader Function
        ontologyGraphLoaderFunction.accept(payload);
        
    }

}
