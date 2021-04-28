package ai.hyperlearning.ontology.services.api.graph.controllers;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Graph Query Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/graph")
@Tag(name = "query", description = "Knowledge Graph Query API")
public class QueryController {
	
	@Operation(
			summary = "Gremlin graph traversal query",
			description = "Submit a Gremlin graph traversal query",
			tags = { "query" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/query", consumes = "application/json", produces = { "application/json" })
	public Object query(@RequestBody Map<String, String> payload, 
			@RequestHeader (required = false, name = "Authorization") String authorization) 
			throws ScriptException, InterruptedException, ExecutionException {
		String gremlinQuery = payload.get("query");
		return GraphAPIStaticObjects.getGraphDatabaseManager()
				.query(gremlinQuery);
	}

}
