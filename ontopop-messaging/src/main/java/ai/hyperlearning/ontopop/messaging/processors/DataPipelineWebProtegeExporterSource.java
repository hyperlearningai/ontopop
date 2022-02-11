package ai.hyperlearning.ontopop.messaging.processors;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Data Pipeline Spring Cloud Stream Source - WebProtege Exporter
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
public interface DataPipelineWebProtegeExporterSource {
    
    @Input
    SubscribableChannel webProtegeProjectUpdatedConsumptionChannel();

}
