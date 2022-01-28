package ai.hyperlearning.ontopop.apps.aws.beanstalk.api.ontology.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * AWS Beanstalk Ontology Management API Service Web Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyManagementApiAwsBeanstalkApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyManagementApiAwsBeanstalkApp.class, args);
    }

}
