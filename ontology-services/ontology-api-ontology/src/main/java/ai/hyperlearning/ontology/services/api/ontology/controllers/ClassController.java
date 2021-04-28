package ai.hyperlearning.ontology.services.api.ontology.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.ontology.statics.OntologyAPIStaticObjects;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Class Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/ontology")
@Tag(name = "class", description = "Ontology Class API")
public class ClassController {
	
	/**
	 * Get all classes
	 * @return
	 */
	
	@Operation(
			summary = "Get all classes",
			description = "Get all ontological classes",
			tags = { "class" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/classes", produces = { "application/json" })
	public Map<String, RDFOwlClass> getClasses() {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlClassMap();
	}
	
	/**
	 * Get a specific class given its (generated) class ID
	 * @param id
	 * @return
	 */
	
	@Operation(
			summary = "Get class by ID",
			description = "Get a specific ontological class given its generated class ID",
			tags = { "class" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFOwlClass.class)))) })
	@GetMapping(value = "/classes/{id}", produces = { "application/json" })
	public RDFOwlClass getClassById(@PathVariable int id) {
		String rdfAbout = OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlClassIdMap().get(id);
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlClassMap().get(rdfAbout);
	}
	
	/**
	 * Get a specific class given its IRI
	 * @param id
	 * @return
	 */
	
	@Operation(
			summary = "Get class by IRI",
			description = "Get a specific ontological class given its IRI",
			tags = { "class" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFOwlClass.class)))) })
	@GetMapping(value = "/classes/class", produces = { "application/json" })
	public RDFOwlClass getClassByIri(@RequestParam(name = "iri", required = true) String iri) {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlClassMap().get(iri);
	}

}
