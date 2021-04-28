package ai.hyperlearning.ontology.services.api.graph.controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.jpa.repositories.vocabulary.SynonymRepository;
import ai.hyperlearning.ontology.services.jpa.repositories.vocabulary.TermRepository;
import ai.hyperlearning.ontology.services.model.vocabulary.Synonym;
import ai.hyperlearning.ontology.services.model.vocabulary.Term;
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
 * Graph Node Controller
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@RestController
@RequestMapping("/api/graph")
@Tag(name = "node", description = "Knowledge Graph Node API")
public class NodeController {
	
	@Autowired
    private SynonymRepository synonymRepository;
	
	@Autowired
    private TermRepository termRepository;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	/**
	 * Get all nodes
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ScriptException 
	 */
	
	@Operation(
			summary = "Get all nodes",
			description = "Get all nodes",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/nodes", produces = { "application/json" })
	public Object getNodes(@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get all nodes
		String gremlinQuery = GremlinRecipes.getNodes(username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Vertex.class, modelVersion);
		
	}
	
	/**
	 * Get a specific node given its (generated) node ID
	 * @param id
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws ScriptException 
	 */
	
	@Operation(
			summary = "Get node",
			description = "Get a specific node given its generated node ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/nodes/{id}", produces = { "application/json" })
	public Object getNode(@PathVariable int id, 
			@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get node
		String gremlinQuery = GremlinRecipes.getNode(id, username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Vertex.class, modelVersion);
		
	}
	
	/**
	 * Get the edges, in both directions, for a specific node given its (generated) node ID
	 * @param id
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Get node bi-directional edges",
			description = "Get the edges, in both directions, for a specific node given its generated node ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/nodes/{id}/edges", produces = { "application/json" })
	public Object getNodeEdges(@PathVariable int id, 
			@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get node bi-directional edges
		String gremlinQuery = GremlinRecipes.getNodeEdges(id, username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Edge.class, modelVersion);
		
	}
	
	/**
	 * Get the outgoing edges for a specific node given its (generated) node ID
	 * @param id
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Get node outgoing edges",
			description = "Get the outgoing edges for a specific node given its generated node ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/nodes/{id}/edges/out", produces = { "application/json" })
	public Object getNodeEdgesOut(@PathVariable int id, 
			@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get node outgoing edges
		String gremlinQuery = GremlinRecipes.getNodeEdgesOut(id, username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Edge.class, modelVersion);
		
	}
	
	/**
	 * Get the incoming edges for a specific node given its (generated) node ID
	 * @param id
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Get node incoming edges",
			description = "Get the incoming edges for a specific node given its generated node ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success") })
	@GetMapping(value = "/nodes/{id}/edges/in", produces = { "application/json" })
	public Object getNodeEdgesIn(@PathVariable int id, 
			@RequestHeader (required = false, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (optional)
		String username = RequestUtils
				.getUsernameFromAuthorizationHeader(authorization);
		
		// Get node incoming edges
		String gremlinQuery = GremlinRecipes.getNodeEdgesIn(id, username);
		return GraphResponseModelCoordinator.transform(
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.query(gremlinQuery), 
				Edge.class, modelVersion);
		
	}
	
	/**************************************************************************
	 * HTTP POST REQUESTS
	 *************************************************************************/
	
	/**
	 * Create a user-defined vertex
	 * @param token
	 * @param payload
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Create node",
			description = "Create a node with a given label",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/nodes/create", consumes = "application/json", produces = { "application/json" })
	public Object createNode(@RequestBody(required = true) Map<String, Object> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Check that the payload includes a label property key as a minimum
		if ( payload.containsKey("label")  ) {
			
			// Create vertex
			String label = payload.get("label").toString();
			Vertex vertex =  GraphAPIStaticObjects.getGraphDatabaseManager()
					.addUserDefinedVertex(label, username);
			
			// Check whether further vertex property key/value pairs
			// have been provided
			if ( payload.size() >= 2 ) {
				
				// Update vertex set of property key/value pairs
				Vertex updatedVertex = GraphAPIStaticObjects
						.getGraphDatabaseManager()
						.updateVertex((int) vertex.id(), payload);
				
				// Index the updated vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						(int) updatedVertex.id());
				
				// Return the updated vertex object
				String gremlinQuery = GremlinRecipes.getNode(
						(int) updatedVertex.id(), username);
				return GraphResponseModelCoordinator.transform(
						GraphAPIStaticObjects.getGraphDatabaseManager()
							.query(gremlinQuery), 
						Vertex.class, modelVersion);
				
			} else {
				
				// Index the created vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						(int) vertex.id());
				
				// Return the created vertex object
				String gremlinQuery = GremlinRecipes.getNode(
						(int) vertex.id(), username);
				return GraphResponseModelCoordinator.transform(
						GraphAPIStaticObjects.getGraphDatabaseManager()
							.query(gremlinQuery), 
						Vertex.class, modelVersion);
				
			}
			
		} else 
			
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Missing label"), 
					HttpStatus.BAD_REQUEST);
		
	}
	
	/**
	 * Index a node
	 * @param id
	 * @param authorization
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Index node",
			description = "Index, or re-index, a node given its node ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PostMapping(value = "/nodes/{id}/index", produces = { "application/json" })
	public Object indexNode(@PathVariable int id, 
			@RequestHeader (required = true, name = "Authorization") String authorization) 
					throws ScriptException, InterruptedException, ExecutionException {
		
		// Get all synonyms associated with this node
		List<Synonym> synonyms = synonymRepository.findByNodeId(id);

		// Get all related terms associated with this node
		List<Term> terms = termRepository.findByNodeId(id);

		// Inject synonyms and related terms, and index the vertex
		String vertexJsonDocument = GraphAPIStaticObjects.getSearchIndexer()
				.indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						id, synonyms, terms);
		
		// Return the indexed object
		return vertexJsonDocument;
		
	}
	
	/**************************************************************************
	 * HTTP PATCH REQUESTS
	 *************************************************************************/
	
	/**
	 * Update a vertex property key and property value
	 * @param id
	 * @param token
	 * @param payload
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Update node",
			description = "Update a node given a property key and property value",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@PatchMapping(value = "/nodes/{id}", consumes = "application/json", produces = { "application/json" })
	public Object updateNode(@PathVariable(required = true) int id, 
			@RequestBody(required = true) Map<String, Object> payload, 
			@RequestHeader (required = true, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Check whether the user is authorised to take this action
		if (GraphManagementAuthorisationFramework.isVertexManagementAuthorized(
				GraphAPIStaticObjects.getGraphDatabaseManager(), id, username)) {
		
			// Check whether a vertex property key/value pair has been provided
			if ( payload.size() == 2 
					&& payload.containsKey("propertyKey") 
					&& payload.containsKey("propertyValue")) {
				
				// Update vertex property key/value pair
				String propertyKey = payload.get("propertyKey").toString();
				Object propertyValue = payload.get("propertyValue");
				Vertex vertex = GraphAPIStaticObjects.getGraphDatabaseManager()
						.updateVertex(id, propertyKey, propertyValue);
				
				// Index the vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						(int) vertex.id());
				
				// Return the updated vertex object
				String gremlinQuery = GremlinRecipes.getNode(
						(int) vertex.id(), username);
				return GraphResponseModelCoordinator.transform(
						GraphAPIStaticObjects.getGraphDatabaseManager()
							.query(gremlinQuery), 
						Vertex.class, modelVersion);
				
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
				
				// Update vertex set of property key/value pairs
				Vertex vertex = GraphAPIStaticObjects.getGraphDatabaseManager()
						.updateVertex(id, payload);
				
				// Index the vertex
				GraphAPIStaticObjects.getSearchIndexer().indexVertexAsDocument(
						GraphAPIStaticObjects.getSearchServiceClient(), 
						GraphAPIStaticObjects.getGraphDatabaseManager(), 
						(int) vertex.id());
				
				// Return the updated vertex object
				String gremlinQuery = GremlinRecipes.getNode(
						(int) vertex.id(), username);
				return GraphResponseModelCoordinator.transform(
						GraphAPIStaticObjects.getGraphDatabaseManager()
							.query(gremlinQuery), 
						Vertex.class, modelVersion);
				
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
	 * Delete a vertex given its ID
	 * @param id
	 * @param token
	 * @return
	 * @throws ScriptException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	@Operation(
			summary = "Delete node",
			description = "Delete a node by ID",
			tags = { "node" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "success"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "unauthorized")})
	@DeleteMapping(value = "/nodes/{id}", produces = { "application/json" })
	public Object deleteNode(@PathVariable(required = true) int id, 
			@RequestHeader (required = true, name = "Authorization") String authorization, 
			@RequestParam(name = "model", required = false, defaultValue = "0") int modelVersion) 
			throws Exception {
		
		// Get the username from the authorisation bearer token (required)
		String username = JWTUtils.getJWTSubject(JWTUtils
				.getJWTFromRequestHeaderAuthorization(authorization));
		
		// Check whether the user is authorised to take this action
		if (GraphManagementAuthorisationFramework.isVertexManagementAuthorized(
				GraphAPIStaticObjects.getGraphDatabaseManager(), id, username)) {
		
			// Get the vertex to be deleted
			String gremlinQuery = GremlinRecipes.getNode(id, username);
			Object response = GraphResponseModelCoordinator.transform(
					GraphAPIStaticObjects.getGraphDatabaseManager()
						.query(gremlinQuery), 
					Vertex.class, modelVersion);
			
			// Delete the vertex from the index
			GraphAPIStaticObjects.getSearchServiceClient().deleteDocument(id);
			
			// Delete the vertex
			GraphAPIStaticObjects.getGraphDatabaseManager().deleteVertex(id);
			return response;
			
		} else
			
			return new ResponseEntity<String>(
					ResponseUtils.createResponseMessage("Unauthorized Action"), 
					HttpStatus.UNAUTHORIZED);
		
	}
	
}
