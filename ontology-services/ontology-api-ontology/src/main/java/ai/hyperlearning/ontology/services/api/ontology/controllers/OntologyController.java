package ai.hyperlearning.ontology.services.api.ontology.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.ontology.statics.OntologyAPIStaticObjects;
import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "ontology", description = "Ontology API")
public class OntologyController {
	
	private static final DateTimeFormatter DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("yyyyMMdd");
	
	/**
	 * Get the entire ontology
	 * @return
	 */
	
	@Operation(
			summary = "Get ontology",
			description = "Get the entire ontology",
			tags = { "ontology" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParsedRDFOwlOntology.class)))) })
	@GetMapping(value = "/ontology", produces = { "application/json" })
	public ParsedRDFOwlOntology getOntology() {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology();
	}
	
	/**
	 * Export the entire ontology
	 * @param type
	 * @return
	 * @throws IOException
	 */
	
	@Operation(
			summary = "Export ontology",
			description = "Export the entire ontology",
			tags = { "ontology" })
	@GetMapping(value = "/ontology/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody HttpEntity<byte[]> exportOntology(
			@RequestParam(name = "type", required = true) String type) throws IOException {
		
		// Set the default filename
		ContentDisposition contentDisposition = ContentDisposition
				.builder("inline")
				.filename("highways-england-ontology-" + LocalDate.now().format(
						DATE_TIME_FORMATTER) + ".owl")
				.build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		
		// Get the ontology and return a byte array
		InputStream inputStream = getClass()
			      .getResourceAsStream("/he-ontology-latest.owl");
		return new HttpEntity<byte[]>(
				IOUtils.toByteArray(inputStream), headers);
		
	}

}
