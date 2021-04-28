package ai.hyperlearning.ontology.services.api.ontology.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.ontology.statics.OntologyAPIStaticObjects;
import ai.hyperlearning.ontology.services.model.ontology.RDFTriple;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Triple Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/ontology")
@Tag(name = "triple", description = "Ontology Triple API")
public class TripleController {
	
	/**
	 * Get all triples
	 * @return
	 */
	
	@Operation(
			summary = "Get all triples",
			description = "Get all semantic triples",
			tags = { "triple" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/triples", produces = { "application/json" })
	public Map<Integer, RDFTriple> getTriples() {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlTripleMap();
	}
	
	/**
	 * Get a specific triple given its (generated) edge ID
	 * @param id
	 * @return
	 */
	
	@Operation(
			summary = "Get triple",
			description = "Get a specific semantic triple given its generated edge ID",
			tags = { "triple" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFTriple.class)))) })
	@GetMapping(value = "/triples/{id}", produces = { "application/json" })
	public RDFTriple getTriple(@PathVariable int id) {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlTripleMap().get(id);
	}

}
