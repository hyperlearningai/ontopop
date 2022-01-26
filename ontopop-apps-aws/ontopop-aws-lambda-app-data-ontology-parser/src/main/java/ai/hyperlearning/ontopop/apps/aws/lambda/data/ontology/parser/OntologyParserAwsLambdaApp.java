package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * AWS Ontology Parser Service Lambda Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyParserAwsLambdaApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyParserAwsLambdaApp.class, args);
    }

}
