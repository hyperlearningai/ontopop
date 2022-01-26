package ai.hyperlearning.ontopop.apps.aws.beanstalk.api.ontology.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * AWS Beanstalk Ontology CRUD API Service Web Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyCrudApiAwsBeanstalkApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyCrudApiAwsBeanstalkApp.class, args);
    }

}
