package ai.hyperlearning.ontology.services.api.graph.controllers;

import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import ai.hyperlearning.ontology.services.graphdb.utils.GremlinTraversalUnits;
import ai.hyperlearning.ontology.services.security.auth.framework.GremlinRecipes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Graph Node Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/graph")
@Tag(name = "node", description = "Knowledge Graph Node Property API")
public class NodePropertyController {
	
	/**
	 * Get all values for a given property key
	 * @param propertyKey
	 * @param authorization
	 * @param unique
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Get property key values",
			description = "Get all values for a given property key",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/nodes/properties/{propertyKey}/values", produces = { "application/json" })
	public Object getNodesPropertyKeyUniqueValues(
			@PathVariable String propertyKey, 
			@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "unique", required = true, defaultValue = "true") boolean unique) 
					throws ScriptException, InterruptedException, ExecutionException {
		
		// Get all the unique values for the given property key
		String gremlinQuery = GremlinRecipes
				.getNodesPropertyKeyUniqueValues(propertyKey);
		return GremlinTraversalUnits.nodesPropertyKeyUniqueValuesToSet(
				propertyKey, GraphAPIStaticObjects.getGraphDatabaseManager()
				.query(gremlinQuery));
		
	}

}
