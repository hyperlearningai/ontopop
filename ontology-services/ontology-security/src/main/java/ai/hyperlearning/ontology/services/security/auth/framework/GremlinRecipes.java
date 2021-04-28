package ai.hyperlearning.ontology.services.security.auth.framework;

/**
 * Gremlin Recipes
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class GremlinRecipes {
	
	public static final String PROPERTY_KEY_USER_DEFINED = 
			"userDefined";
	public static final String PROPERTY_KEY_USER_ID = 
			"userId";
	public static final String PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION = 
			"objectPropertyRdfsLabel";
	
	/**************************************************************************
	 * VERTEX-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all vertices
	 * @return
	 */
	
	private static String getNodes() {
		return "g.V()"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all authorised vertices
	 * @param userId
	 * @return
	 */
	
	public static String getNodes(String userId) {
		if (userId == null)
			return getNodes();
		else
			return "g.V()"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific vertex given a vertex ID
	 * @param id
	 * @return
	 */
	
	private static String getNode(int id) {
		return "g.V(" + id + ")"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific authorised vertex given a vertex ID
	 * @param id
	 * @return
	 */
	
	public static String getNode(int id, String userId) {
		if (userId == null)
			return getNode(id);
		else 
			return "g.V(" + id + ")"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the bi-directional edges for a given vertex
	 * @param id
	 * @return
	 */
	
	private static String getNodeEdges(int id) {
		return "g.V(" + id + ")"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".bothE()"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the authorised bi-directional edges for a given vertex 
	 * @param id
	 * @param userId
	 * @return
	 */
	
	public static String getNodeEdges(int id, String userId) {
		if (userId == null)
			return getNodeEdges(id);
		else 
			return "g.V(" + id + ")"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".bothE()"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the outgoing edges from a given vertex
	 * @param id
	 * @return
	 */
	
	private static String getNodeEdgesOut(int id) {
		return "g.V(" + id + ")"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".outE()"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the authorised outgoing edges from a given vertex
	 * @param id
	 * @param userId
	 * @return
	 */
	
	public static String getNodeEdgesOut(int id, String userId) {
		if (userId == null)
			return getNodeEdgesOut(id);
		else
			return "g.V(" + id + ")"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".outE()"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the incoming edges into a given vertex
	 * @param id
	 * @return
	 */
	
	private static String getNodeEdgesIn(int id) {
		return "g.V(" + id + ")"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".inE()"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the authorised incoming edges into a given vertex
	 * @param id
	 * @param userId
	 * @return
	 */
	
	public static String getNodeEdgesIn(int id, String userId) {
		if (userId == null)
			return getNodeEdgesIn(id);
		else
			return "g.V(" + id + ")"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".inE()"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all node-centric property keys
	 * @return
	 */
	
	public static String getNodesPropertyKeys() {
		return "g.V()"
				+ ".properties()"
				+ ".key()"
				+ ".dedup()";
	}
	
	/**
	 * Get all node-centric unique property values for a given property key
	 * @param propertyKey
	 * @return
	 */
	
	public static String getNodesPropertyKeyUniqueValues(String propertyKey) {
		return "g.V()"
				+ ".has('" + propertyKey + "')"
				+ ".values('" + propertyKey + "')"
				+ ".dedup()";
	}
	
	/**************************************************************************
	 * EDGE-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all edges
	 * @return
	 */
	
	private static String getEdges() {
		return "g.V()"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".bothE()"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all authorised edges
	 * @param userId
	 * @return
	 */
	
	public static String getEdges(String userId) {
		if (userId == null)
			return getEdges();
		return "g.V()"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".bothE()"
					+ ".or("
						+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
						+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific edge given an edge ID
	 * @param id
	 * @return
	 */
	
	private static String getEdge(int id) {
		return "g.E(" + id + ")"
				+ ".has('" + PROPERTY_KEY_USER_DEFINED + "', false)"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific authorised edge given an edge ID
	 * @param id
	 * @return
	 */
	
	public static String getEdge(int id, String userId) {
		if (userId == null)
			return getEdge(id);
		else
			return "g.E(" + id + ")"
				+ ".or("
					+ "__.has('" + PROPERTY_KEY_USER_DEFINED + "', false), "
					+ "__.has('" + PROPERTY_KEY_USER_ID + "', '" + userId + "'))"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}

}
