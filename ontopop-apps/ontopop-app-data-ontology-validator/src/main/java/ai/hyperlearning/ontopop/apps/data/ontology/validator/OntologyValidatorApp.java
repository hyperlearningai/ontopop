package ai.hyperlearning.ontopop.apps.data.ontology.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.validator.function.OntologyValidatorFunction;
import ai.hyperlearning.ontopop.data.ontology.validator.function.OntologyValidatorFunctionModel;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineValidatorSource;

/**
 * Ontology Validation Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineValidatorSource.class)
public class OntologyValidatorApp {

    @Autowired
    private OntologyValidatorFunction ontologyValidatorFunction;

    public static void main(String[] args) {
        SpringApplication.run(OntologyValidatorApp.class, args);
    }

    @StreamListener("ingestedConsumptionChannel")
    public void processIngestedOntology(String payload) {

        // Execute the Ontology Validator Function
        OntologyValidatorFunctionModel ontologyValidatorFunctionModel = 
                new OntologyValidatorFunctionModel(payload);
        ontologyValidatorFunction.apply(ontologyValidatorFunctionModel);

    }

}
