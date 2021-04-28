package ai.hyperlearning.ontology.services.api.graph.controllers.vocabulary;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import ai.hyperlearning.ontology.services.jpa.repositories.vocabulary.SynonymRepository;
import ai.hyperlearning.ontology.services.jpa.repositories.vocabulary.TermRepository;
import ai.hyperlearning.ontology.services.model.vocabulary.Synonym;
import ai.hyperlearning.ontology.services.security.auth.framework.SynonymManagementAuthorisationFramework;
import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import ai.hyperlearning.ontology.services.security.utils.ResponseUtils;
import ai.hyperlearning.ontology.services.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Node Synonyms Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "synonym", description = "Node Synonyms API")
public class NodeSynonymController {
	
	@Autowired
    private SynonymRepository synonymRepository;
	
	@Autowired
    private TermRepository termRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all synonyms for a given node
	 * @param nodeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get node synonyms",
			description = "Get all synonyms for a given node",
			tags = { "synonym" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/{nodeId}/synonyms", produces = { "application/json" })
	public ResponseEntity<?> getNodeSynonyms(@PathVariable int nodeId, 
			@RequestHeader (required = false, name = "Authorization") String authorization) {
		
		// Get all synonyms for a given node
		Iterable<Synonym> synonyms = synonymRepository.findByNodeId(nodeId);
		return ResponseEntity.ok(synonyms);
		
	}
	
	/**
	 * Get a specific node-level synonym
	 * @param nodeId
	 * @param synonymId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get node synonym",
			description = "Get a specific node-level synonym",
			tags = { "synonym" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/{nodeId}/synonyms/{synonymId}", produces = { "application/json" })
	public ResponseEntity<?> getNodeSynonym(@PathVariable int nodeId, 
			@PathVariable int synonymId, 
			@RequestHeader (required = false, name = "Authorization") String authorization) {
		
		// Get a specific node-level synonym given a synonym ID
		Optional<Synonym> optionalSynonym = synonymRepository
				.findById(synonymId);
		if (!optionalSynonym.isEmpty()) {
			Synonym synonym = optionalSynonym.get();
			if (synonym.getNodeId() == nodeId)
				return ResponseEntity.ok(synonym);
		}
		
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Synonym Not Found"), 
				HttpStatus.NOT_FOUND);
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Create a node-level synonym
	 * @param nodeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Create a node synonym",
			description = "Create a node-level synonym",
			tags = { "synonym" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/graph/nodes/{nodeId}/synonyms/create", produces = { "application/json" })
	public ResponseEntity<?> createSynonym(@PathVariable int nodeId, 
			@RequestBody(required = false) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Create a new synonym
		Synonym synonym = new Synonym();
		synonym.setNodeId(nodeId);
		synonym.setUserId(username);
		synonym.setSynonym("");
		LocalDateTime currentDateTime = DateTimeUtils.getCurrentLocalDateTime();
		synonym.setDateCreated(currentDateTime);
		synonym.setDateLastUpdated(currentDateTime);
		
		// Set the synonym contents if provided
		if ( payload != null ) {
			if ( payload.containsKey("synonym") ) {
				String contents = payload.get("synonym");
				if ( contents.length() > 0 )
					synonym.setSynonym(contents);
			}
		}
		
		// Persist and return the newly created synonym
		synonymRepository.save(synonym);
		
		// Inject synonyms and related terms, and index the vertex
		GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
				GraphAPIStaticObjects.getSearchServiceClient(), 
				GraphAPIStaticObjects.getGraphDatabaseManager(), 
				nodeId, synonymRepository.findByNodeId(nodeId), 
				termRepository.findByNodeId(nodeId));
		
		// Return the synonym object
		return ResponseEntity.ok(synonym);
		
	}
	
	/**************************************************************************
	 * HTTP PATCH REQUESTS
	 *************************************************************************/
	
	/**
	 * Update the contents of a node-level synonym
	 * @param nodeId
	 * @param synonymId
	 * @param payload
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Update node synonym",
			description = "Update the contents of a node-level synonym",
			tags = { "synonym" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/graph/nodes/{nodeId}/synonyms/{synonymId}", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> updateSynonym(@PathVariable int nodeId, 
			@PathVariable int synonymId, 
			@RequestBody(required = true) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific node-level synonym given its synonym ID
		Optional<Synonym> optionalSynonym = synonymRepository
				.findById(synonymId);
		
		// Check whether the user is authorised to take this action
		if ( SynonymManagementAuthorisationFramework
				.isSynonymManagementAuthorized(optionalSynonym, username) ) {
			
			// Check whether the synonym nodeId matches the requested nodeId
			Synonym synonym = optionalSynonym.get();
			if (synonym.getNodeId() == nodeId) {
				
				// Update and persist the synonym contents
				String contents = payload.get("synonym");
				synonym.setSynonym(contents);
				synonym.setDateLastUpdated(
						DateTimeUtils.getCurrentLocalDateTime());
				synonymRepository.save(synonym);
				
				// Inject synonyms and related terms, and index the vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						nodeId, synonymRepository.findByNodeId(nodeId), 
						termRepository.findByNodeId(nodeId));
				
				// Return the synonym object
				return ResponseEntity.ok(synonym);
				
			}
			
		}
			
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Unauthorized Action"), 
				HttpStatus.UNAUTHORIZED);
		
	}
	
	/**************************************************************************
	 * HTTP DELETE REQUESTS
	 *************************************************************************/
	
	/**
	 * Delete a node-level synonym
	 * @param nodeId
	 * @param synonymId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Delete node synonym",
			description = "Delete a node-level synonym",
			tags = { "synonym" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/graph/nodes/{nodeId}/synonyms/{synonymId}", produces = { "application/json" })
	public ResponseEntity<?> deleteSynonym(@PathVariable(required = true) int nodeId, 
			@PathVariable int synonymId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific node-level synonym given its synonym ID
		Optional<Synonym> optionalSynonym = synonymRepository
				.findById(synonymId);
		
		// Check whether the user is authorised to take this action
		if ( SynonymManagementAuthorisationFramework
				.isSynonymManagementAuthorized(optionalSynonym, username) ) {
			
			// Check whether the synonym nodeId matches the requested nodeId
			Synonym synonym = optionalSynonym.get();
			if (synonym.getNodeId() == nodeId) {
				
				// Delete the synonym
				synonymRepository.delete(synonym);
				
				// Inject synonyms and related terms, and index the vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						nodeId, synonymRepository.findByNodeId(nodeId), 
						termRepository.findByNodeId(nodeId));
				
				// Return a response message
				return new ResponseEntity<String>(
						ResponseUtils.createResponseMessage("Synonym Deleted"), 
						HttpStatus.OK);
				
			}
			
		}
		
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Unauthorized Action"), 
				HttpStatus.UNAUTHORIZED);
		
	}

}
