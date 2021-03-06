package ai.hyperlearning.ontopop.apps.data.ontology.loader.triplestore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.loader.triplestore.function.OntologyTriplestoreLoaderFunction;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineLoaderTriplestoreSource;

/**
 * Ontology Triplestore Loading Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineLoaderTriplestoreSource.class)
public class OntologyTriplestoreLoaderApp {
    
    @Autowired
    private OntologyTriplestoreLoaderFunction ontologyTriplestoreLoaderFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyTriplestoreLoaderApp.class, args);
    }
    
    @StreamListener("validatedTriplestoreLoaderConsumptionChannel")
    public void processValidatedOntology(String payload) {
        
        // Execute the Ontology Triplestore Loading Function
        ontologyTriplestoreLoaderFunction.accept(payload);
        
    }

}
