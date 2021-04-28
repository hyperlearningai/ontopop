package ai.hyperlearning.ontology.services.api.graph.controllers;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.structure.Edge;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.security.auth.framework.GraphManagementAuthorisationFramework;
import ai.hyperlearning.ontology.services.security.auth.framework.GremlinRecipes;
import ai.hyperlearning.ontology.services.security.utils.JWTUtils;
import ai.hyperlearning.ontology.services.security.utils.RequestUtils;
import ai.hyperlearning.ontology.services.security.utils.ResponseUtils;
import ai.hyperlearning.ontology.services.ui.GraphResponseModelCoordinator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Graph Edge Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/graph")
@Tag(name = "edge", description = "Knowledge Graph Edge API")
public class EdgeController {
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all edges
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ScriptException 
	 */
	
	@Operation(
			summary = "Get all edges",
			description = "Get all edges",
			tags = { "edge" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/edges", produces = { "application/json" })
	public Object getEdges(@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get edges
		String gremlinQuery = GremlinRecipes.getEdges(username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Edge.class, modelVersion);
		
	}
	
	/**
	 * Get a specific edge given its (generated) edge ID
	 * @param id
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ScriptException 
	 */
	
	@Operation(
			summary = "Get edge",
			description = "Get a specific edge given its generated edge ID",
			tags = { "edge" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/edges/{id}", produces = { "application/json" })
	public Object getEdge(@PathVariable int id, 
			@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get edge
		String gremlinQuery = GremlinRecipes.getEdge(id, username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery),
				Edge.class, modelVersion);
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Create an edge with a given label between a given source vertex ID and given target vertex ID
	 * @param token
	 * @param payload
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Create edge",
			description = "Create an edge with a given label between a given source vertex ID and given target vertex ID",
			tags = { "edge" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/edges/create", consumes = "application/json", produces = { "application/json" })
	public Object createEdge(@RequestBody(required = true) Map<String, String> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Create edge
		String edgeLabel = payload.get("edgeLabel");
		int sourceVertexId = Integer.valueOf(payload.get("sourceVertexId"));
		int targetVertexId = Integer.valueOf(payload.get("targetVertexId"));
		Edge edge = GraphAPIStaticObjects.getGraphDatabaseManager()
				.addUserDefinedEdge(edgeLabel, sourceVertexId, targetVertexId, 
						username);
		
		// Add optional edge property key/value pairs
		if (payload.containsKey("rdfsLabel")) {
			String rdfsLabel = payload.get("rdfsLabel");
			GraphAPIStaticObjects.getGraphDatabaseManager()
					.updateEdge((int) edge.id(), 
							"objectPropertyRdfsLabel", rdfsLabel);
		}
		
		if (payload.containsKey("rdfAbout")) {
			String rdfAbout = payload.get("rdfAbout");
			GraphAPIStaticObjects.getGraphDatabaseManager()
			.updateEdge((int) edge.id(), 
					"objectPropertyRdfAbout", rdfAbout);
		}
		
		// Return the created edge object
		String gremlinQuery = GremlinRecipes.getEdge(
				(int) edge.id(), username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Edge.class, modelVersion);
		
	}
	
	/**************************************************************************
	 * HTTP PATCH REQUESTS
	 *************************************************************************/
	
	/**
	 * Update an edge given a property key and property value
	 * @param id
	 * @param token
	 * @param payload
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Update edge",
			description = "Update an edge given a property key and property value",
			tags = { "edge" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/edges/{id}", consumes = "application/json", produces = { "application/json" })
	public Object updateEdge(@PathVariable(required = true) int id, 
			@RequestBody(required = true) Map<String, Object> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Check whether the user is authorised to take this action
		if (GraphManagementAuthorisationFramework.isEdgeManagementAuthorized(
				GraphAPIStaticObjects.getGraphDatabaseManager(), id, username)) {
			
			// Check whether an edge property key/value pair has been provided
			if ( payload.size() == 2 
					&& payload.containsKey("propertyKey") 
					&& payload.containsKey("propertyValue")) {
		
				// Update edge property key/value pair
				String propertyKey = payload.get("propertyKey").toString();
				Object propertyValue = payload.get("propertyValue");
				Edge edge = GraphAPIStaticObjects.getGraphDatabaseManager()
						.updateEdge(id, propertyKey, propertyValue);
				
				// Return the updated edge object
				String gremlinQuery = GremlinRecipes.getEdge(
						(int) edge.id(), username);
				return GraphResponseModelCoordinator.transform(
						GraphAPIStaticObjects.getGraphDatabaseManager()
							.query(gremlinQuery), 
						Edge.class, modelVersion);
				
			} 
			
			// Or whether the full vertex object has been provided i.e. request
			// body contains at least id, label, userDefined and userId
			else if ( payload.size() >= 4 
					&& payload.containsKey("id") 
					&& payload.containsKey("label") 
					&& payload.containsKey(GraphDatabaseManager
							.PROPERTY_KEY_USER_DEFINED)
					&& payload.containsKey(GraphDatabaseManager
							.PROPERTY_KEY_USER_ID) ) {
				
				// Update edge set of property key/value pairs
				Edge edge = GraphAPIStaticObjects.getGraphDatabaseManager()
						.updateEdge(id, payload);
				
				// Return the updated edge object
				String gremlinQuery = GremlinRecipes.getEdge(
						(int) edge.id(), username);
				return GraphResponseModelCoordinator.transform(
						GraphAPIStaticObjects.getGraphDatabaseManager()
							.query(gremlinQuery), 
						Edge.class, modelVersion);
				
			}
			
		}
			
		return new ResponseEntity<String>(
				ResponseUtils.createResponseMessage("Unauthorized Action"), 
				HttpStatus.UNAUTHORIZED);
		
	}
	
	/**************************************************************************
	 * HTTP DELETE REQUESTS
	 *************************************************************************/
	
	@Operation(
			summary = "Delete edge",
			description = "Delete an edge by ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/edges/{id}", produces = { "application/json" })
	public Object deleteEdge(@PathVariable(required = true) int id, 
			@RequestHeader (required = true, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Check whether the user is authorised to take this action
		if (GraphManagementAuthorisationFramework.isEdgeManagementAuthorized(
				GraphAPIStaticObjects.getGraphDatabaseManager(), id, username)) {
		
			// Get the edge to be deleted
			String gremlinQuery = GremlinRecipes.getEdge(id, username);
			Object response = GraphResponseModelCoordinator.transform(
					GraphAPIStaticObjects.getGraphDatabaseManager()
						.query(gremlinQuery), 
					Edge.class, modelVersion);
			
			// Delete the edge
			GraphAPIStaticObjects.getGraphDatabaseManager().deleteEdge(id);
			return response;
			
		} else
			
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Unauthorized Action"), 
					HttpStatus.UNAUTHORIZED);
		
	}

}
