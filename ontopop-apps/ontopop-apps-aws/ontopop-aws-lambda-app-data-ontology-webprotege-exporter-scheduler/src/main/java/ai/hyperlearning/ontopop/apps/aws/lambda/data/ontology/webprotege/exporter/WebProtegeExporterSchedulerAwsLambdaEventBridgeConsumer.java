package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.webprotege.exporter;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.scheduler.WebProtegeExporterSchedulerService;

/**
 * AWS WebProtege Exporter Scheduler Event Bridge Consumer
 * The event emitted from AWS Event Bridge is consumed as a Map
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class WebProtegeExporterSchedulerAwsLambdaEventBridgeConsumer 
        implements Consumer<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            WebProtegeExporterSchedulerAwsLambdaEventBridgeConsumer.class);
    
    @Autowired
    private WebProtegeExporterSchedulerService webProtegeExporterSchedulerService;

    @Override
    public void accept(String event) {
        LOGGER.debug("WebProtege Exporter Scheduler Service event: {}", event);
        LOGGER.info("Running the WebProtege Exporter Scheduler Service.");
        webProtegeExporterSchedulerService.runScheduledWebProtegeExporterService();
    }
    
}
