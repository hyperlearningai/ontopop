package ai.hyperlearning.ontopop.data.ingestors.ontology.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.model.ontology.Ontology;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ETL Ingest - REST Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/services/etl")
@Tag(name = "ingest", description = "ETL Ingest API")
public class IngestController {
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	@PostMapping(
			value = "/ingest",
			consumes = "application/json",
			produces = { "application/json" })
	public ResponseEntity<?> ingest(
			@RequestBody(required = true) Ontology ontology) {
		
		return null;
		
	}

}
