package ai.hyperlearning.ontopop.apps.data.ontology.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.parser.function.OntologyParserFunction;
import ai.hyperlearning.ontopop.data.ontology.parser.function.OntologyParserFunctionModel;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineParserSource;

/**
 * Ontology Parsing Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(DataPipelineParserSource.class)
public class OntologyParserApp {
    
    @Autowired
    private OntologyParserFunction ontologyParserFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyParserApp.class, args);
    }
    
    @StreamListener("validatedConsumptionChannel")
    public void processValidatedOntology(String payload) {
        
        // Execute the Ontology Parser Function
        OntologyParserFunctionModel ontologyParserFunctionModel = 
                new OntologyParserFunctionModel(payload);
        ontologyParserFunction.apply(ontologyParserFunctionModel);
        
    }

}
