package ai.hyperlearning.ontology.services.api.graph.controllers;

import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import ai.hyperlearning.ontology.services.security.auth.framework.GremlinRecipes;
import ai.hyperlearning.ontology.services.security.utils.RequestUtils;
import ai.hyperlearning.ontology.services.ui.GraphResponseModelCoordinator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Graph Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api")
@Tag(name = "graph", description = "Knowledge Graph API")
public class GraphController {
	
	/**
	 * Get the entire graph
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ScriptException 
	 * @throws JsonProcessingException 
	 */
	
	@Operation(
			summary = "Get graph",
			description = "Get the entire graph",
			tags = { "graph" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/graph", produces = { "application/json" })
	public Object getGraph(@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException, 
			JsonProcessingException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get all nodes
		String gremlinQueryAllNodes = GremlinRecipes.getNodes(username);
		String nodes = GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQueryAllNodes), 
				Vertex.class, modelVersion);
		
		// Get all edges
		String gremlinQueryAllEdges = GremlinRecipes.getEdges(username);
		String edges = GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQueryAllEdges), 
				Edge.class, modelVersion);
		
		// Generate a JSON model of the entire graph
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode objectNode = objectMapper.createObjectNode();
		objectNode.putPOJO("nodes", nodes);
		objectNode.putPOJO("edges", edges);
		String json = objectMapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(objectNode);
		return json;
		
	}
	
}
