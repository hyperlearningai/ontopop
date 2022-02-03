package ai.hyperlearning.ontopop.apps.data.ontology.pipeline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.pipeline.function.OntologyPipelineFunction;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineValidatorSource;

/**
 * Ontology Post-Ingestion End-to-End ETL Pipeline Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineValidatorSource.class)
public class OntologyPipelineApp {
    
    @Autowired
    private OntologyPipelineFunction ontologyPipelineFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyPipelineApp.class, args);
    }
    
    @StreamListener("ingestedConsumptionChannel")
    public void processIngestedOntology(String payload) {
        
        // Execute the Ontology Pipeline Function
        ontologyPipelineFunction.accept(payload);
    
    }

}
