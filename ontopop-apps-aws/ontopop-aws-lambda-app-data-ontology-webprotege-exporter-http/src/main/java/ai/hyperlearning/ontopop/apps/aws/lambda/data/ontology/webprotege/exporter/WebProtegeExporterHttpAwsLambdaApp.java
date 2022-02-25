package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.webprotege.exporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * AWS WebProtege Exporter Service Lambda Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class WebProtegeExporterHttpAwsLambdaApp {
    
    public static void main(String[] args) {
        SpringApplication.run(WebProtegeExporterHttpAwsLambdaApp.class, args);
    }

}
