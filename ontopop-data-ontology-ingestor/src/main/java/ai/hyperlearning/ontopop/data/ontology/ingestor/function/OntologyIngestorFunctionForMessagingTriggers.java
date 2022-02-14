package ai.hyperlearning.ontopop.data.ontology.ingestor.function;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontopop.data.ontology.ingestor.OntologyIngestorService;

/**
 * Ontology Ingestor Cloud Function
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyIngestorFunctionForMessagingTriggers 
        implements Consumer<OntologyIngestorFunctionModel> {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyIngestorFunctionForMessagingTriggers.class);
    
    @Autowired
    private OntologyIngestorService ontologyIngestorService;

    @Override
    public void accept(
            OntologyIngestorFunctionModel ontologyIngestorFunctionModel) {

        // Log the request headers for debugging purposes
        LOGGER.debug("New HTTP POST request via messaging system: Git webhook.");
        ontologyIngestorFunctionModel.getHeaders().forEach((key, value) -> {
            LOGGER.debug(String.format("Header '%s' = %s", key, value));
        });

        // Log the request body payload for debugging purposes
        LOGGER.debug("Git webhook payload: {}",
                ontologyIngestorFunctionModel.getPayload());

        // Run the Ontology Ingestion Service pipeline
        ontologyIngestorService.run(ontologyIngestorFunctionModel.getHeaders(),
                ontologyIngestorFunctionModel.getPayload());

    }

}
