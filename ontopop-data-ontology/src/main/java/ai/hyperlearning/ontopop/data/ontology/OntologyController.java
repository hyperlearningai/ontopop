package ai.hyperlearning.ontopop.data.ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.model.ontology.Ontology;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Service - Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "ontology", description = "Ontology Service REST API")
public class OntologyController {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyController.class);
	
	@Autowired
	private OntologyService ontologyService;
	
	/**************************************************************************
	 * 1. POST - Create Ontology
	 *************************************************************************/
	
	@Operation(
			summary = "Create an ontology",
			description = "Create an ontology",
			tags = { "ontology" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "201",
	        		description = "Ontology created"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontology creation unauthorized")})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(produces = { "application/json" })
	public Ontology newOntology(@RequestBody Ontology newOntology) {
		return ontologyService.create(newOntology);
	}
	
	/**************************************************************************
	 * 2.1. GET - Get Ontologies
	 *************************************************************************/
	
	
	
	/**************************************************************************
	 * 2.2. GET - Get Ontology
	 *************************************************************************/
	
	
	
	
	/**************************************************************************
	 * 3. PATCH - Update Ontology
	 *************************************************************************/
	
	
	/**************************************************************************
	 * 4. DELETE - Delete Ontology
	 *************************************************************************/
	
	

}
