package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.webprotege.exporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.scheduler.WebProtegeExporterSchedulerService;

/**
 * AWS WebProtege Exporter Scheduler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class WebProtegeExporterScheduler implements Runnable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(
            WebProtegeExporterScheduler.class);
    
    @Autowired
    private WebProtegeExporterSchedulerService webProtegeExporterSchedulerService;

    @Override
    public void run() {
        LOGGER.info("Running the WebProtege Exporter Scheduler Service");
        webProtegeExporterSchedulerService.runScheduledWebProtegeExporterService();
    }
    
}
