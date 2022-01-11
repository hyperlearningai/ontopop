package ai.hyperlearning.ontopop.data.ontology.ingestor.function;

import java.util.function.Function;

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
public class OntologyIngestorFunction implements Function<OntologyIngestorFunctionModel, Boolean> {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyIngestorFunction.class);
	
	@Autowired
	private OntologyIngestorService ontologyIngestorService;
	
	@Override
	public Boolean apply(
			OntologyIngestorFunctionModel ontologyIngestorFunctionModel) {
		
		// Log the HTTP request headers for debugging purposes
		LOGGER.debug("New HTTP POST request: Ontology ingestion webhook.");
		ontologyIngestorFunctionModel.getHeaders().forEach((key, value) -> {
	        LOGGER.debug(String.format("Header '%s' = %s", key, value));
	    });
		
		// Log the HTTP request body payload for debugging purposes
		LOGGER.debug("Ontology ingestion webhook payload: {}", 
				ontologyIngestorFunctionModel.getPayload());
		
		// Run the Ontology Ingestion Service pipeline
		ontologyIngestorService.run(
				ontologyIngestorFunctionModel.getHeaders(), 
				ontologyIngestorFunctionModel.getPayload());
		
		return true;
		
	}

}
