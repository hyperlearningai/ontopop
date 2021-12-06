package ai.hyperlearning.ontopop.data.ontology;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyNonSecretData;
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
    private OntologyRepository ontologyRepository;
	
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
	        		description = "Ontology creation request unauthorized")})
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Ontology newOntology(@RequestBody Ontology newOntology) {
		LOGGER.debug("New HTTP POST request: Create ontology.");
		return ontologyService.create(newOntology);
	}
	
	/**************************************************************************
	 * 2.1. GET - Get Ontologies
	 *************************************************************************/
	
	@Operation(
			summary = "Get all ontologies",
			description = "Get all ontologies",
			tags = { "ontology" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Ontologies successfully retrieved"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontologies retrieval request unauthorized")})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Ontology> getOntologies() {
		LOGGER.debug("New HTTP GET request: Get all ontologies.");
		return (List<Ontology>) ontologyRepository.findAll();
	}
	
	/**************************************************************************
	 * 2.2. GET - Get Ontology
	 *************************************************************************/
	
	@Operation(
			summary = "Get ontology",
			description = "Get an ontology by ontology ID",
			tags = { "ontology" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Ontology successfully retrieved"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontology retrieval request unauthorized"), 
	        @ApiResponse(
	        		responseCode = "404",
	        		description = "Ontology not found")})
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Ontology getOntology(@PathVariable(required = true) int id) {
		LOGGER.debug("New HTTP GET request: Get ontology by ID.");
		return ontologyRepository.findById(id)
                .orElseThrow(() -> new OntologyNotFoundException(id));
	}
	
	/**************************************************************************
	 * 3.1. PATCH - Update Ontology (non-sensitive attributes)
	 *************************************************************************/
	
	@Operation(
			summary = "Update ontology",
			description = "Update an ontology by ID (non-sensitive attributes)",
			tags = { "ontology" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Ontology successfully updated"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontology update request unauthorized")})
	@PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Ontology updateOntology(@PathVariable(required = true) int id, 
			@RequestBody OntologyNonSecretData ontologyNonSecretData) {
		LOGGER.debug("New HTTP PATCH request: Update ontology by ID.");
		return ontologyService.update(ontologyNonSecretData);
	}
	
	/**************************************************************************
	 * 3.2. PATCH - Update Ontology (repository access token)
	 *************************************************************************/
	
	
	/**************************************************************************
	 * 3.3. PATCH - Update Ontology (webhook secret)
	 *************************************************************************/
	
	
	/**************************************************************************
	 * 4. DELETE - Delete Ontology
	 *************************************************************************/
	
	@Operation(
			summary = "Delete ontology",
			description = "Delete an ontology by ontology ID",
			tags = { "ontology" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Ontology successfully deleted"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontology deletion request unauthorized"), 
	        @ApiResponse(
	        		responseCode = "404",
	        		description = "Ontology not found")})
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteOntology(
			@PathVariable(required = true) int id)	{
		LOGGER.debug("New HTTP DELETE request: Delete ontology by ID.");
		ontologyService.delete(id);
		return new ResponseEntity<>(
				"Ontology deletion request processed", HttpStatus.OK);
	}

}
