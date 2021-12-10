package ai.hyperlearning.ontopop.messaging.processors;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Ontology Processor
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
public interface OntologyProcessor {
	
	@Output("ontologyIngestedChannel")
	MessageChannel publishIngestedOntology();
	
	@Input("ontologyIngestedChannel")
	SubscribableChannel consumeIngestedOntology();
	
	@Output("ontologyValidatedChannel")
	MessageChannel publishValidatedOntology();
	
	@Input("ontologyIngestedChannel")
	SubscribableChannel consumeValidatedOntology();
	
	@Output("ontologyParsedChannel")
	MessageChannel publishParsedOntology();
	
	@Input("ontologyIngestedChannel")
	SubscribableChannel consumeParsedOntology();
	
	@Output("ontologyModelledChannel")
	MessageChannel publishModelledOntology();
	
	@Input("ontologyIngestedChannel")
	SubscribableChannel consumeModelledOntology();
	
	@Output("ontologyLoadedChannel")
	MessageChannel publishLoadedOntology();
	
	@Input("ontologyIngestedChannel")
	SubscribableChannel consumeLoadedOntology();

}
