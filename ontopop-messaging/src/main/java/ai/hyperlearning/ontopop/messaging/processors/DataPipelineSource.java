package ai.hyperlearning.ontopop.messaging.processors;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Data Pipeline Spring Cloud Stream Source
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
public interface DataPipelineSource {
	
	@Output
	MessageChannel ingestedPublicationChannel();
	
	@Input
	SubscribableChannel ingestedConsumptionChannel();
	
	@Output
	MessageChannel validatedPublicationChannel();
	
	@Input
	SubscribableChannel validatedConsumptionChannel();
	
	@Output
	MessageChannel parsedPublicationChannel();
	
	@Input
	SubscribableChannel parsedConsumptionChannel();
	
	@Output
	MessageChannel modelledPublicationChannel();
	
	@Input
	SubscribableChannel modelledConsumptionChannel();
	
	@Output
	MessageChannel loadedPublicationChannel();
	
	@Input
	SubscribableChannel loadedConsumptionChannel();

}
