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
 * Graph Note Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "note", description = "Graph Notes API")
public class GraphNoteController {
	
	@Autowired
    private NoteRepository noteRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all graph-level notes
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get graph notes",
			description = "Get all graph-level notes",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/graph/notes", produces = { "application/json" })
	public ResponseEntity<?> getNotes(
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get all graph-level notes
		Iterable<Note> notes = noteRepository.findByType(
				NoteType.GRAPH.name().toLowerCase());
		return ResponseEntity.ok(notes);
		
	}
	
	/**
	 * Get a specific graph-level note given a note ID
	 * @param id
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get graph note",
			description = "Get a specific graph-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Unauthorized"), 
	        @ApiResponse(
	        		responseCode = "404",
	        		description = "Not Found")})
	@GetMapping(value = "/graph/notes/{id}", produces = { "application/json" })
	public ResponseEntity<?> getNote(@PathVariable int id, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get a specific graph-level note given a note ID
		Optional<Note> note = noteRepository.findById(id);
		if (!note.isEmpty())
			return ResponseEntity.ok(note);
		else
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Note Not Found"), 
					HttpStatus.NOT_FOUND);
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Create an empty graph-level note
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Create a graph note",
			description = "Create an empty graph-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/graph/notes/create", produces = { "application/json" })
	public ResponseEntity<?> createNote(
			@RequestBody(required = false) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Create a new empty note
		Note note = new Note();
		note.setType(NoteType.GRAPH.name().toLowerCase());
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
	 * Update the contents of a graph-level note
	 * @param id
	 * @param payload
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Update graph note",
			description = "Update the contents of a graph-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/graph/notes/{id}", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> updateNote(@PathVariable(required = true) int id, 
			@RequestBody(required = true) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific graph-level note given its note ID
		Optional<Note> optionalNote = noteRepository.findById(id);
		
		// Check whether the user is authorised to take this action
		if ( NoteManagementAuthorisationFramework.isNoteManagementAuthorized(
				optionalNote, username) ) {
			
			// Update and persist the note contents
			String contents = payload.get("contents");
			Note note = optionalNote.get();
			note.setContents(contents);
			note.setDateLastUpdated(DateTimeUtils.getCurrentLocalDateTime());
			noteRepository.save(note);
			return ResponseEntity.ok(note);
			
		} else
			
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Unauthorized Action"), 
					HttpStatus.UNAUTHORIZED);
		
	}
	
	/**************************************************************************
	 * HTTP DELETE REQUESTS
	 *************************************************************************/
	
	/**
	 * Delete a graph-level note
	 * @param id
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Delete graph note",
			description = "Delete a graph-level note",
			tags = { "note" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/graph/notes/{id}", produces = { "application/json" })
	public ResponseEntity<?> deleteNote(@PathVariable(required = true) int id, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the specific graph-level note given its note ID
		Optional<Note> optionalNote = noteRepository.findById(id);
		
		// Check whether the user is authorised to take this action
		if ( NoteManagementAuthorisationFramework.isNoteManagementAuthorized(
				optionalNote, username) ) {
			
			// Delete the note
			Note note = optionalNote.get();
			noteRepository.delete(note);
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Note Deleted"), 
					HttpStatus.OK);
			
		} else
			
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Unauthorized Action"), 
					HttpStatus.UNAUTHORIZED);
		
	}

}
