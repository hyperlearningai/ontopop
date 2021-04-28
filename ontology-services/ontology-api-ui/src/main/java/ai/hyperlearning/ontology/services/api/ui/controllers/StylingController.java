package ai.hyperlearning.ontology.services.api.ui.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import ai.hyperlearning.ontology.services.jpa.repositories.ui.StylingRepository;
import ai.hyperlearning.ontology.services.model.ui.Styling;
import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * UI Styling Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/ui")
@Tag(name = "styling", description = "Styling API")
public class StylingController {
	
	@Autowired
    private StylingRepository stylingRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get the user-specific styling configuration object
	 * from the underlying RDBMS. If no styling has been persisted
	 * for the given user, then return an empty JSON object.
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Get styling",
			description = "Get user-specific UI styling configuration",
			tags = { "styling" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@GetMapping(value = "/styling", produces = { "application/json" })
	public ResponseEntity<?> getStyling(@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Get the styling configuration for this user
		Optional<Styling> styling = stylingRepository.findById(username);
		if (!styling.isEmpty()) {
			
			// Return the persisted arbitrary JSON string as a JSON object
			Styling userStyling = styling.get();
			userStyling.setConfiguration(userStyling.getStringConfiguration());
			return ResponseEntity.ok(userStyling);
		
		} else  {
			
			// Create and persist a new styling object for this user
			Styling newStyling = new Styling(username, null);
			stylingRepository.save(newStyling);
			return ResponseEntity.ok(newStyling);
			
		}
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Persist the user-specific styling configuration object
	 * to the underlying RDBMS. 
	 * @param configuration
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Create or update styling",
			description = "Create or update user-specific UI styling configuration",
			tags = { "styling" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/styling", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> createOrUpdateStyling(@RequestBody(required = true) JsonNode configuration, 
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Persist the styling configuration for this user
		Styling styling = new Styling(username, configuration);
		stylingRepository.save(styling);
		
		// Response with the styling object
		return ResponseEntity.ok(styling);
		
	}
	
	/**************************************************************************
	 * HTTP DELETE REQUESTS
	 *************************************************************************/
	
	/**
	 * Delete user-specific styling by persisting the user-specific styling 
	 * configuration object to the underlying RDBMS with an empty
	 * configuration property. 
	 * @param authorization
	 * @return
	 */
	
	@Operation(
			summary = "Delete styling",
			description = "Delete user-specific UI styling configuration",
			tags = { "styling" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/styling", produces = { "application/json" })
	public ResponseEntity<?> deleteStyling(
			@RequestHeader (required = true, name = "Authorization") String authorization) {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Persist the styling configuration for this user
		// with an empty configuration string
		Styling styling = new Styling(username, null);
		stylingRepository.save(styling);
		
		// Response with the styling object
		return ResponseEntity.ok(styling);
		
	}

}
