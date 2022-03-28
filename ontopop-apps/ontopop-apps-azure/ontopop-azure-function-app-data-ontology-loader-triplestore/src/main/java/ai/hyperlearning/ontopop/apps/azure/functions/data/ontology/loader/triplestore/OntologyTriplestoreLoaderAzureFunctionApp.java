package ai.hyperlearning.ontopop.apps.azure.functions.data.ontology.loader.triplestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Microsoft Azure Ontology Triplestore Loader Service Serverless Function - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class OntologyTriplestoreLoaderAzureFunctionApp {
    
    public static void main(String[] args) {
        SpringApplication.run(
                OntologyTriplestoreLoaderAzureFunctionApp.class, args);
    }

}
