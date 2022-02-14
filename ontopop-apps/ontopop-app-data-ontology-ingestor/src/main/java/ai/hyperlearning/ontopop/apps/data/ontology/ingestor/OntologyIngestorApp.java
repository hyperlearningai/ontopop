package ai.hyperlearning.ontopop.apps.data.ontology.ingestor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.ComponentScan;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunction;
import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunctionModel;
import ai.hyperlearning.ontopop.messaging.processors.DataPipelineIngestorSource;

/**
 * Ontology Ingestion Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
@EnableBinding(DataPipelineIngestorSource.class)
public class OntologyIngestorApp {
    
    private static final String PAYLOAD_HEADERS_KEY = "headers";
    
    @Autowired
    private OntologyIngestorFunction ontologyIngestorFunction;
    
    public static void main(String[] args) {
        SpringApplication.run(OntologyIngestorApp.class, args);
    }
    
    @SuppressWarnings("unchecked")
    @StreamListener("gitRepositoryUpdatedConsumptionChannel")
    public void processIngestedGitHubWebhook(String payload) {
        
        // Extract the headers map from the payload
        Gson gson = new Gson();
        JsonObject payloadJson = gson.fromJson(payload, JsonElement.class)
                .getAsJsonObject();
        Map<String, String> headers = gson.fromJson(
                payloadJson.get(PAYLOAD_HEADERS_KEY), Map.class);
        
        // Construct an OntologyIngestorFunctionModel object
        OntologyIngestorFunctionModel ontologyIngestorFunctionModel = 
                new OntologyIngestorFunctionModel(headers, payload);
        
        // Execute the Ontology ingestion service pipeline
        ontologyIngestorFunction.accept(ontologyIngestorFunctionModel);
        
    }

}
