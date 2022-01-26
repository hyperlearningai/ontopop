package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.modeller.graph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * AWS Ontology Graph Modeller Service Lambda Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyGraphModellerAwsLambdaApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyGraphModellerAwsLambdaApp.class, args);
    }

}
