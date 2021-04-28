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
 * Edge Note Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "note", description = "Edge Notes API")
public class EdgeNoteController {
	
	@Autowired
    private NoteRepository noteRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all edge-level notes
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get all edge notes",
			description = "Get notes across all edges",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/edges/notes", produces = { "application/json" })
	public ResponseEntity<?> getNotes(
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get all edge-level notes
		Iterable<Note> notes = noteRepository.findByType(
				NoteType.EDGE.name().toLowerCase());
		return ResponseEntity.ok(notes);
		
	}
	
	/**
	 * Get all notes for a given edge
	 * @param edgeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get edge notes",
			description = "Get all notes for a given edge",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/edges/{edgeId}/notes", produces = { "application/json" })
	public ResponseEntity<?> getEdgeNotes(@PathVariable int edgeId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get all notes for a given edge
		Iterable<Note> notes = noteRepository.findByTypeAndEdgeId(
				NoteType.EDGE.name().toLowerCase(), edgeId);
		return ResponseEntity.ok(notes);
		
	}
	
	/**
	 * Get a specific edge-level note
	 * @param edgeId
	 * @param noteId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get edge note",
			description = "Get a specific edge-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/edges/{edgeId}/notes/{noteId}", produces = { "application/json" })
	public ResponseEntity<?> getEdgeNote(@PathVariable int edgeId, 
			@PathVariable int noteId, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get a specific edge-level note given a note ID
		Optional<Note> optionalNote = noteRepository.findById(noteId);
		if (!optionalNote.isEmpty()) {
			Note note = optionalNote.get();
			if (note.getEdgeId() == edgeId)
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
	 * Create an empty edge-level note
	 * @param edgeId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Create an edge note",
			description = "Create an empty edge-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/graph/edges/{edgeId}/notes/create", produces = { "application/json" })
	public ResponseEntity<?> createNote(@PathVariable int edgeId, 
			@RequestBody(required = false) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Create a new empty note
		Note note = new Note();
		note.setType(NoteType.EDGE.name().toLowerCase());
		note.setEdgeId(edgeId);
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
	 * Update the contents of an edge-level note
	 * @param edgeId
	 * @param noteId
	 * @param payload
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Update edge note",
			description = "Update the contents of an edge-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/graph/edges/{edgeId}/notes/{noteId}", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> updateNote(@PathVariable int edgeId, 
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
			
			// Check whether the note edgeId matches the requested edgeId
			Note note = optionalNote.get();
			if (note.getEdgeId() == edgeId) {
				
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
	 * Delete an edge-level note
	 * @param edgeId
	 * @param noteId
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Delete edge note",
			description = "Delete an edge-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/graph/edges/{edgeId}/notes/{noteId}", produces = { "application/json" })
	public ResponseEntity<?> deleteNote(@PathVariable(required = true) int edgeId, 
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
			
			// Check whether the note edgeId matches the requested edgeId
			Note note = optionalNote.get();
			if (note.getEdgeId() == edgeId) {
				
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
