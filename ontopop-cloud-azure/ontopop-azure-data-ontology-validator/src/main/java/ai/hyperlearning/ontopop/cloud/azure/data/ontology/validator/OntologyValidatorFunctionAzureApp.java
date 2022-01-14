package ai.hyperlearning.ontopop.cloud.azure.data.ontology.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Microsoft Azure Ontology Validation Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyValidatorFunctionAzureApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyValidatorFunctionAzureApp.class, args);
    }

}
