package ai.hyperlearning.ontopop.cloud.azure.api.ontology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Microsoft Azure Ontology API Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(
        basePackages = {"ai.hyperlearning.ontopop"}, 
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                ai.hyperlearning.ontopop.api.ontology.OntologyAPIApp.class
                        })})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyAPIApp {
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyAPIApp.class, args);
    }

}
