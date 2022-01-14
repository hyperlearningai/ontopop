package ai.hyperlearning.ontopop.apps.azure.api.ontology.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Microsoft Azure Ontology CRUD API Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyCRUDAPIAzureApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyCRUDAPIAzureApp.class, args);
    }

}
