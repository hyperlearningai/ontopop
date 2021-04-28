package ai.hyperlearning.ontology.services.api.collaboration.controllers;

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

import ai.hyperlearning.ontology.services.jpa.repositories.collaboration.NoteRepository;
import ai.hyperlearning.ontology.services.model.collaboration.Note;
import ai.hyperlearning.ontology.services.model.collaboration.NoteType;
import ai.hyperlearning.ontology.services.security.auth.framework.NoteManagementAuthorisationFramework;
import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import ai.hyperlearning.ontology.services.security.utils.ResponseUtils;
import ai.hyperlearning.ontology.services.utils.DateTimeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Node Note Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "note", description = "Node Notes API")
public class NodeNoteController {
	
	@Autowired
    private NoteRepository noteRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all node-level notes
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get all node notes",
			description = "Get notes across all nodes",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/notes", produces = { "application/json" })
	public ResponseEntity<?> getNotes(
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get all node-level notes
		Iterable<Note> notes = noteRepository.findByType(
				NoteType.NODE.name().toLowerCase());
		return ResponseEntity.ok(notes);
		
	}
	
	/**
	 * Get all notes for a given node
	 * @param nodeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get node notes",
			description = "Get all notes for a given node",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/{nodeId}/notes", produces = { "application/json" })
	public ResponseEntity<?> getNodeNotes(@PathVariable int nodeId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get all notes for a given node
		Iterable<Note> notes = noteRepository.findByTypeAndNodeId(
				NoteType.NODE.name().toLowerCase(), nodeId);
		return ResponseEntity.ok(notes);
		
	}
	
	/**
	 * Get a specific node-level note
	 * @param nodeId
	 * @param noteId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get node note",
			description = "Get a specific node-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/nodes/{nodeId}/notes/{noteId}", produces = { "application/json" })
	public ResponseEntity<?> getNodeNote(@PathVariable int nodeId, 
			@PathVariable int noteId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get a specific node-level note given a note ID
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.isEmpty()) {
			Note note = optionalNote.get();
			if (note.getNodeId() == nodeId)
				return ResponseEntity.ok(note);
		}
		
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Note Not Found"), 
				HttpStatus.NOT_FOUND);
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Create a node-level note
	 * @param nodeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Create a node note",
			description = "Create a node-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/graph/nodes/{nodeId}/notes/create", produces = { "application/json" })
	public ResponseEntity<?> createNote(@PathVariable int nodeId, 
			@RequestBody(required = false) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Create a new note
		Note note = new Note();
		note.setType(NoteType.NODE.name().toLowerCase());
		note.setNodeId(nodeId);
		note.setUserId(username);
		note.setContents("");
		LocalDateTime currentDateTime = DateTimeUtils.getCurrentLocalDateTime();
		note.setDateCreated(currentDateTime);
		note.setDateLastUpdated(currentDateTime);
		
		// Set the note contents if provided
		if ( payload != null ) {
			if ( payload.containsKey("contents") ) {
				String contents = payload.get("contents");
				if ( contents.length() > 0 )
					note.setContents(contents);
			}
		}
		
		// Persist and return the newly created note
		noteRepository.save(note);
		return ResponseEntity.ok(note);
		
	}
	
	/**************************************************************************
	 * HTTP PATCH REQUESTS
	 *************************************************************************/
	
	/**
	 * Update the contents of a node-level note
	 * @param nodeId
	 * @param noteId
	 * @param payload
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Update node note",
			description = "Update the contents of a node-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/graph/nodes/{nodeId}/notes/{noteId}", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> updateNote(@PathVariable int nodeId, 
			@PathVariable int noteId, 
			@RequestBody(required = true) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific graph-level note given its note ID
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		
		// Check whether the user is authorised to take this action
		if ( NoteManagementAuthorisationFramework.isNoteManagementAuthorized(
				optionalNote, username) ) {
			
			// Check whether the note nodeId matches the requested nodeId
			Note note = optionalNote.get();
			if (note.getNodeId() == nodeId) {
				
				// Update and persist the note contents
				String contents = payload.get("contents");
				note.setContents(contents);
				note.setDateLastUpdated(
						DateTimeUtils.getCurrentLocalDateTime());
				noteRepository.save(note);
				return ResponseEntity.ok(note);
				
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
	 * Delete a node-level note
	 * @param nodeId
	 * @param noteId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Delete node note",
			description = "Delete a node-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/graph/nodes/{nodeId}/notes/{noteId}", produces = { "application/json" })
	public ResponseEntity<?> deleteNote(@PathVariable(required = true) int nodeId, 
			@PathVariable int noteId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific graph-level note given its note ID
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		
		// Check whether the user is authorised to take this action
		if ( NoteManagementAuthorisationFramework.isNoteManagementAuthorized(
				optionalNote, username) ) {
			
			// Check whether the note nodeId matches the requested nodeId
			Note note = optionalNote.get();
			if (note.getNodeId() == nodeId) {
				
				// Delete the note
				noteRepository.delete(note);
				return new ResponseEntity<String>(
						ResponseUtils.createResponseMessage("Note Deleted"), 
						HttpStatus.OK);
				
			}
			
		}
		
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Unauthorized Action"), 
				HttpStatus.UNAUTHORIZED);
		
	}

}
