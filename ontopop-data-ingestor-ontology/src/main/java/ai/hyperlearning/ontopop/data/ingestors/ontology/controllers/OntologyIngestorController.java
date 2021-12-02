package ai.hyperlearning.ontopop.data.ingestors.ontology.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.ingestors.ontology.OntologyIngestorPipeline;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Ingestion Service - Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "ingest", description = "Ontology Ingestion Service API")
public class OntologyIngestorController {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyIngestorController.class);
	
	@Autowired
	private OntologyIngestorPipeline ontologyIngestorPipeline;
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	@PostMapping(
			value = "/webhooks",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> ingest(
			@RequestHeader Map<String, String> headers, 
			@RequestBody(required = true) String payload) {
		
		// Log the HTTP request headers for debugging purposes
		headers.forEach((key, value) -> {
	        LOGGER.debug(String.format("Header '%s' = %s", key, value));
	    });
		
		// Log the HTTP request body payload for debugging purposes
		LOGGER.debug(payload);
		
		// Run the Ontology Ingestion Service pipeline
		ontologyIngestorPipeline.run(headers, payload);
		
		// Return a response entity
		return new ResponseEntity<String>(
				"Ontology ingestion request accepted.", HttpStatus.OK);
		
	}

}
