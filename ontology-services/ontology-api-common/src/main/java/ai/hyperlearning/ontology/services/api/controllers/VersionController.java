package ai.hyperlearning.ontology.services.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ai.hyperlearning.ontology.services.utils.GlobalProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Version Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "version", description = "Version API")
public class VersionController {
	
	@Autowired
	private GlobalProperties globalProperties;
	
	/**
	 * Get the API version number
	 * @return
	 * @throws JsonProcessingException
	 */
	
	@Operation(
			summary = "Get API version",
			description = "Get the API version",
			tags = { "version" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/version", produces = { "application/json" })
	public Object getVersion() throws JsonProcessingException {
		
		// Generate a JSON response with the API version
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.putPOJO("apiVersion", 
				globalProperties.getOntologyFrameworkVersion());
		return objectMapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(objectNode);
		
	}

}
