package ai.hyperlearning.ontopop.messaging.processors;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Data Pipeline Spring Cloud Stream Source - Modelling
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
public interface DataPipelineModellerGraphSource {

    @Input
    SubscribableChannel parsedConsumptionChannel();

    @Output
    MessageChannel modelledPublicationChannel();

}
