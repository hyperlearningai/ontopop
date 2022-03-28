package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.pipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * AWS Ontology Post-Ingestion End-to-End ETL Pipeline Service Lambda Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyPipelineAwsLambdaApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyPipelineAwsLambdaApp.class, args);
    }

}
