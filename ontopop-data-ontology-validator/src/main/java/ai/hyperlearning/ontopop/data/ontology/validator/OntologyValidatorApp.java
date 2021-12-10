package ai.hyperlearning.ontopop.data.ontology.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.messaging.processors.OntologyProcessor;

/**
 * Ontology Validation Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@SpringBootApplication
@EnableBinding(OntologyProcessor.class)
public class OntologyValidatorApp {
	
	public static void main(String[] args) {
        SpringApplication.run(OntologyValidatorApp.class, args);
	}
	
	@StreamListener("consumeIngestedOntology")
	public void processIngestedOntology(String webhookEvent) {
		System.out.println("Webhook Event Registered by Client " + webhookEvent);
	}

}
