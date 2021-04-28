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
import ai.hyperlearning.ontology.services.model.vocabulary.Term;
import ai.hyperlearning.ontology.services.security.auth.framework.TermManagementAuthorisationFramework;
import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import ai.hyperlearning.ontology.services.security.utils.ResponseUtils;
import ai.hyperlearning.ontology.services.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Node Related Terms Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "term", description = "Node Related Terms API")
public class NodeTermController {
	
	@Autowired
    private SynonymRepository synonymRepository;
	
	@Autowired
    private TermRepository termRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all related terms for a given node
	 * @param nodeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get node related terms",
			description = "Get all related terms for a given node",
			tags = { "term" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/{nodeId}/terms", produces = { "application/json" })
	public ResponseEntity<?> getNodeTerms(@PathVariable int nodeId, 
			@RequestHeader (required = false, name = "Authorization") String authorization) {
		
		// Get all terms for a given node
		Iterable<Term> terms = termRepository.findByNodeId(nodeId);
		return ResponseEntity.ok(terms);
		
	}
	
	/**
	 * Get a specific node-level related term
	 * @param nodeId
	 * @param termId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get node related term",
			description = "Get a specific node-level related term",
			tags = { "term" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/{nodeId}/terms/{termId}", produces = { "application/json" })
	public ResponseEntity<?> getNodeTerm(@PathVariable int nodeId, 
			@PathVariable int termId, 
			@RequestHeader (required = false, name = "Authorization") String authorization) {
		
		// Get a specific node-level term given a term ID
		Optional<Term> optionalTerm = termRepository.findById(termId);
		if (!optionalTerm.isEmpty()) {
			Term term = optionalTerm.get();
			if (term.getNodeId() == nodeId)
				return ResponseEntity.ok(term);
		}
		
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Related Term Not Found"), 
				HttpStatus.NOT_FOUND);
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Create a node-level related term
	 * @param nodeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Create a node related term",
			description = "Create a node-level related term",
			tags = { "term" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/graph/nodes/{nodeId}/terms/create", produces = { "application/json" })
	public ResponseEntity<?> createTerm(@PathVariable int nodeId, 
			@RequestBody(required = false) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Create a new term
		Term term = new Term();
		term.setNodeId(nodeId);
		term.setUserId(username);
		term.setTerm("");
		LocalDateTime currentDateTime = DateTimeUtils.getCurrentLocalDateTime();
		term.setDateCreated(currentDateTime);
		term.setDateLastUpdated(currentDateTime);
		
		// Set the term contents if provided
		if ( payload != null ) {
			if ( payload.containsKey("term") ) {
				String contents = payload.get("term");
				if ( contents.length() > 0 )
					term.setTerm(contents);
			}
		}
		
		// Persist and return the newly created term
		termRepository.save(term);
		
		// Inject synonyms and related terms, and index the vertex
		GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
				GraphAPIStaticObjects.getSearchServiceClient(), 
				GraphAPIStaticObjects.getGraphDatabaseManager(), 
				nodeId, synonymRepository.findByNodeId(nodeId), 
				termRepository.findByNodeId(nodeId));
		
		// Return the term object
		return ResponseEntity.ok(term);
		
	}
	
	/**************************************************************************
	 * HTTP PATCH REQUESTS
	 *************************************************************************/
	
	/**
	 * Update the contents of a node-level related term
	 * @param nodeId
	 * @param termId
	 * @param payload
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Update node related term",
			description = "Update the contents of a node-level related term",
			tags = { "term" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/graph/nodes/{nodeId}/terms/{termId}", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> updateTerm(@PathVariable int nodeId, 
			@PathVariable int termId, 
			@RequestBody(required = true) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific node-level term given its term ID
		Optional<Term> optionalTerm = termRepository.findById(termId);
		
		// Check whether the user is authorised to take this action
		if ( TermManagementAuthorisationFramework
				.isTermManagementAuthorized(optionalTerm, username) ) {
			
			// Check whether the term nodeId matches the requested nodeId
			Term term = optionalTerm.get();
			if (term.getNodeId() == nodeId) {
				
				// Update and persist the term contents
				String contents = payload.get("term");
				term.setTerm(contents);
				term.setDateLastUpdated(
						DateTimeUtils.getCurrentLocalDateTime());
				termRepository.save(term);
				
				// Inject synonyms and related terms, and index the vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						nodeId, synonymRepository.findByNodeId(nodeId), 
						termRepository.findByNodeId(nodeId));
				
				// Return the term object
				return ResponseEntity.ok(term);
				
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
	 * Delete a node-level related term
	 * @param nodeId
	 * @param termId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Delete node related term",
			description = "Delete a node-level related term",
			tags = { "term" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/graph/nodes/{nodeId}/terms/{termId}", produces = { "application/json" })
	public ResponseEntity<?> deleteTerm(@PathVariable(required = true) int nodeId, 
			@PathVariable int termId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific node-level term given its term ID
		Optional<Term> optionalTerm = termRepository.findById(termId);
		
		// Check whether the user is authorised to take this action
		if ( TermManagementAuthorisationFramework
				.isTermManagementAuthorized(optionalTerm, username) ) {
			
			// Check whether the term nodeId matches the requested nodeId
			Term term = optionalTerm.get();
			if (term.getNodeId() == nodeId) {
				
				// Delete the term
				termRepository.delete(term);
				
				// Inject synonyms and related terms, and index the vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						nodeId, synonymRepository.findByNodeId(nodeId), 
						termRepository.findByNodeId(nodeId));
				
				// Return a response message
				return new ResponseEntity<String>(
						ResponseUtils.createResponseMessage(
								"Related Term Deleted"), 
						HttpStatus.OK);
				
			}
			
		}
		
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Unauthorized Action"), 
				HttpStatus.UNAUTHORIZED);
		
	}

}
