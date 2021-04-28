package ai.hyperlearning.ontology.services.security.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import ai.hyperlearning.ontology.services.security.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Authentication Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/auth")
@Tag(name = "authentication", description = "Authentication API")
public class AuthenticationController {
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	/**
	 * Login and respond with a JWT token
	 * @param payload
	 * @return
	 * @throws JsonProcessingException
	 */
	
	@Operation(
			summary = "Login",
			description = "Login and authentication token generation",
			tags = { "login" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/login", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> login(@RequestBody(required = true) Map<String, String> payload) 
			throws JsonProcessingException {
		
		try {
			
			String username = payload.get("username");
			String password = payload.get("password");
			authenticate(username, password);
			String jwt = JWTUtils.createJWT(username);
			return ResponseEntity.ok(
					JWTUtils.getJWTPayloadAndSignatureAsJson(jwt));
			
		} catch (DisabledException e) {
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Invalid Credentials"), 
					HttpStatus.UNAUTHORIZED);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Invalid Credentials"), 
					HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Invalid Credentials"), 
					HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	/**
	 * Generate a new JWT token to replace an expired one
	 * @param payload
	 * @return
	 */
	
	@Operation(
			summary = "Refresh Token",
			description = "Refresh an expired token",
			tags = { "refresh", "token" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/token/refresh", consumes = "application/json", produces = { "application/json" })
	public ResponseEntity<?> refreshToken(@RequestBody(required = true) Map<String, String> payload) {
		
		try {
			
			String username = payload.get("username");
			String oldToken = payload.get("token");
			if ( JWTUtils.isExpired(oldToken) && 
					JWTUtils.getJWTSubjectFromExpiredToken(
							oldToken).equals(username) ) {
				String jwt = JWTUtils.createJWT(username);
				return ResponseEntity.ok(
						JWTUtils.getJWTPayloadAndSignatureAsJson(jwt));
			} else 
				return new ResponseEntity<String>(
						ResponseUtils.createResponseMessage(
								"Invalid Expired Authentication Token"), 
						HttpStatus.UNAUTHORIZED);
			
		} catch (Exception e) {
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage(
							"Invalid Username or Invalid Expired "
							+ "Authentication Token"), 
					HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	/**
	 * Authenticate credentials using the Spring Authentication Manager
	 * @param username
	 * @param password
	 */
	
	private void authenticate(String username, String password) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						username, password));
	}

}
