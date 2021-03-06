package ai.hyperlearning.ontopop.apps.data.ontology.indexer.graph;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.indexer.graph.function.OntologyGraphIndexerFunction;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineIndexerGraphSource;

/**
 * Ontology Graph Indexer Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineIndexerGraphSource.class)
public class OntologyGraphIndexerApp {
    
    @Autowired
    private OntologyGraphIndexerFunction ontologyGraphIndexerFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyGraphIndexerApp.class, args);
    }
    
    @StreamListener("modelledGraphIndexerConsumptionChannel")
    public void processModelledOntology(String payload) {
        
        // Execute the Ontology Graph Indexer Function
        ontologyGraphIndexerFunction.accept(payload);
        
    }

}
