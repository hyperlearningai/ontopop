package ai.hyperlearning.ontopop.apps.data.ontology.webprotege.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.function.WebProtegeExporterFunction;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineWebProtegeExporterSource;

/**
 * WebProtege Exporter Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
@EnableBinding(DataPipelineWebProtegeExporterSource.class)
public class WebProtegeExporterApp {
    
    @Autowired
    private WebProtegeExporterFunction webProtegeExporterFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(WebProtegeExporterApp.class, args);
    }
    
    @StreamListener("webProtegeProjectUpdatedConsumptionChannel")
    public void processIngestedWebProtegeProjectUpdatedWebhook(
            String payload) {

        // Execute the WebProtege Exporter Function
        webProtegeExporterFunction.accept(payload);

    }

}
