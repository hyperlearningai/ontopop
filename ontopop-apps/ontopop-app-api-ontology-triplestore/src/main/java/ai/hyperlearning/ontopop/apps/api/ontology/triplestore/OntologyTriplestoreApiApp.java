package ai.hyperlearning.ontopop.apps.api.ontology.triplestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Ontology Triplestore API Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyTriplestoreApiApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyTriplestoreApiApp.class, args);
    }

}
