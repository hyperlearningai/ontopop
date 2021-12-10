package ai.hyperlearning.ontopop.messaging.processors;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Ontology Processor
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
public interface OntologyProcessor {
	
	@Output("ontologyIngestedChannel")
	MessageChannel ontologyIngested();
	
	@Output("ontologyValidatedChannel")
	MessageChannel ontologyValidated();
	
	@Output("ontologyParsedChannel")
	MessageChannel ontologyParsed();

}
