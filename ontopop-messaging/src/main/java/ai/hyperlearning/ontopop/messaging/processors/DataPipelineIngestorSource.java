package ai.hyperlearning.ontopop.messaging.processors;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Data Pipeline Spring Cloud Stream Source - Ingestion
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
public interface DataPipelineIngestorSource {
	
	@Output
	MessageChannel ingestedPublicationChannel();

}
