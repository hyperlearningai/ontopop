package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.function;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.WebProtegeExporterService;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;

/**
 * WebProtege Exporter Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class WebProtegeExporterFunction implements Consumer<String> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeExporterFunction.class);
    
    @Autowired
    private WebProtegeExporterService webProtegeExporterService;
    
    @Override
    public void accept(String message) {
        
        try {

            // Explicitly check that the string payload
            // models an WebProtegeWebhook object
            ObjectMapper mapper = new ObjectMapper();
            WebProtegeWebhook webProtegeWebhook = mapper.readValue(
                    message, WebProtegeWebhook.class);

            // Log the consumed payload for debugging purposes
            LOGGER.debug("New WebProtege project updated event detected and "
                    + "consumed via the shared messaging service and the "
                    + "webProtegeProjectUpdatedConsumptionChannel channel.");
            LOGGER.debug("WebProtegeWebhook message payload: {}", message);

            // Run the WebProtege Exporter service pipeline
            webProtegeExporterService.run(webProtegeWebhook);

        } catch (JsonProcessingException e) {

            LOGGER.debug("New WebProtege project updated event detected and "
                    + "consumed via the shared messaging service and the "
                    + "webProtegeProjectUpdatedConsumptionChannel channel.");
            LOGGER.info("The ingested object is NOT a WebProtege project "
                    + "updated webhook. Skipping.");

        }
        
    }
    
    public void acceptPojo(WebProtegeWebhook webProtegeWebhook) {
        
        // Run the WebProtege Exporter service pipeline
        webProtegeExporterService.run(webProtegeWebhook);
        
    }

}
