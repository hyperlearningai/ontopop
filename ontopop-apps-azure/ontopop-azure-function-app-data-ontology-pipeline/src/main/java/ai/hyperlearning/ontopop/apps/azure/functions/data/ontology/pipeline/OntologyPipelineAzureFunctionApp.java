package ai.hyperlearning.ontopop.apps.azure.functions.data.ontology.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Microsoft Azure Ontology Post-Ingestion End-to-End ETL Pipeline Service Serverless Function - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyPipelineAzureFunctionApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyPipelineAzureFunctionApp.class, args);
    }

}
