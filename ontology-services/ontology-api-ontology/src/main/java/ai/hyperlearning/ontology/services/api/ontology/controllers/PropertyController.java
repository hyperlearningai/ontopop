package ai.hyperlearning.ontology.services.api.ontology.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.ontology.statics.OntologyAPIStaticObjects;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlObjectProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Property Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/ontology")
@Tag(name = "property", description = "Ontology Properties API")
public class PropertyController {
	
	/**
	 * Get all annotation properties
	 * @return
	 */
	
	@Operation(
			summary = "Get all annotation property keys",
			description = "Get all ontological annotation properties",
			tags = { "property", "annotation" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/properties/annotations", produces = { "application/json" })
	public Map<String, RDFOwlAnnotationProperty> getAnnotationProperties() {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlAnnotationPropertyMap();
	}
	
	/**
	 * Get a specific annotation property given its 
	 * (generated) annotation property ID 
	 * @param id
	 * @return
	 */
	
	@Operation(
			summary = "Get annotation property key by ID",
			description = "Get a specific ontological annotation property given its generated annotation property ID",
			tags = { "property", "annotation" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFOwlAnnotationProperty.class)))) })
	@GetMapping(value = "/properties/annotations/{id}", produces = { "application/json" })
	public RDFOwlAnnotationProperty getAnnotationPropertyById(
			@PathVariable int id) {
		String rdfAbout = OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlAnnotationPropertyIdMap().get(id);
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlAnnotationPropertyMap().get(rdfAbout);
	}
	
	/**
	 * Get a specific annotation property given its IRI
	 * @param iri
	 * @return
	 */
	
	@Operation(
			summary = "Get annotation property key by IRI",
			description = "Get a specific ontological annotation property given its IRI",
			tags = { "property", "annotation" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFOwlAnnotationProperty.class)))) })
	@GetMapping(value = "/properties/annotations/annotation", produces = { "application/json" })
	public RDFOwlAnnotationProperty getAnnotationPropertyByIri(@RequestParam(name = "iri", required = true) String iri) {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlAnnotationPropertyMap().get(iri);
	}
	
	/**
	 * Get all object properties
	 * @return
	 */
	
	@Operation(
			summary = "Get all object property keys",
			description = "Get all ontological object properties",
			tags = { "property", "object" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/properties/objects", produces = { "application/json" })
	public Map<String, RDFOwlObjectProperty> getObjectProperties() {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlObjectPropertyMap();
	}
	
	/**
	 * Get a specific object property given its 
	 * (generated) object property ID 
	 * @param id
	 * @return
	 */
	
	@Operation(
			summary = "Get object property key by ID",
			description = "Get a specific ontological object property given its generated object property ID",
			tags = { "property", "object" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFOwlObjectProperty.class)))) })
	@GetMapping(value = "/properties/objects/{id}", produces = { "application/json" })
	public RDFOwlObjectProperty getObjectPropertyById(@PathVariable int id) {
		String rdfAbout = OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlObjectPropertyIdMap().get(id);
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlObjectPropertyMap().get(rdfAbout);
	}
	
	/**
	 * Get a specific object property given its IRI
	 * @param id
	 * @return
	 */
	
	@Operation(
			summary = "Get object property key by IRI",
			description = "Get a specific ontological object property given its IRI",
			tags = { "property", "object" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success", 
	                content = @Content(array = @ArraySchema(schema = @Schema(implementation = RDFOwlObjectProperty.class)))) })
	@GetMapping(value = "/properties/objects/object", produces = { "application/json" })
	public RDFOwlObjectProperty getObjectPropertyByIri(@RequestParam(name = "iri", required = true) String iri) {
		return OntologyAPIStaticObjects.getParsedRDFOwlOntology()
				.getOwlObjectPropertyMap().get(iri);
	}

}
