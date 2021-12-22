package ai.hyperlearning.ontopop.graph.gremlin;

/**
 * Gremlin Recipes
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GremlinRecipes {
	
	private static final String VERTEX_PROPERTY_KEY_ONTOLOGY_ID = 
			"ontologyId";
	private static final String VERTEX_PROPERTY_KEY_IRI = 
			"iri";
	private static final String VERTEX_PROPERTY_KEY_KEY = 
			"key";
	private static final String EDGE_PROPERTY_KEY_ONTOLOGY_ID = 
			"ontologyId";
	
	/**************************************************************************
	 * VERTEX-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all vertices
	 * @return
	 */
	
	public static String getVertices() {
		return "g.V()"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific vertex given a vertex ID
	 * @param id
	 * @return
	 */
	
	public static String getVertex(long vertexId) {
		return "g.V(" + vertexId + ")"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get the bi-directional edges for a given vertex
	 * @param id
	 * @return
	 */
	
	public static String getVertexEdges(long vertexId) {
		return "g.V(" + vertexId + ")"
				+ ".bothE()"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
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
	
	public static String getVertexEdgesOut(long vertexId) {
		return "g.V(" + vertexId + ")"
				+ ".outE()"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
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
	
	public static String getVertexEdgesIn(long vertexId) {
		return "g.V(" + vertexId + ")"
				+ ".inE()"
				+ ".project("
					+ "'edgeId', "
					+ "'role', "
					+ "'edgeProperties', "
					+ "'sourceNodeId', "
					+ "'targetNodeId')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all property keys that exist across all vertices
	 * @return
	 */
	
	public static String getVerticesPropertyKeys() {
		return "g.V()"
				+ ".properties()"
				+ ".key()"
				+ ".dedup()";
	}
	
	/**
	 * Get all unique property values for a given property key that exist
	 * across all vertices
	 * @param propertyKey
	 * @return
	 */
	
	public static String getVerticesPropertyKeyUniqueValues(String propertyKey) {
		return "g.V()"
				+ ".has('" + propertyKey + "')"
				+ ".values('" + propertyKey + "')"
				+ ".dedup()";
	}
	
	/**************************************************************************
	 * VERTEX AND ONTOLOGY-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all vertices belonging to a specific ontology ID
	 * @param ontologyId
	 * @return
	 */
	
	public static String getOntologyVertices(int ontologyId) {
		return "g.V()"
				+ ".has('" + VERTEX_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + ")"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific vertex belonging to a specific ontology ID by IRI
	 * @param ontologyId
	 * @param iri
	 * @return
	 */
	
	public static String getOntologyVertexByIri(int ontologyId, String iri) {
		return "g.V()"
				+ ".and("
					+ "__.has('" + VERTEX_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + "), "
					+ "__.has('" + VERTEX_PROPERTY_KEY_IRI + "', '" + iri + "'))"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get a specific vertex belonging to a specific ontology ID by key
	 * @param ontologyId
	 * @param key
	 * @return
	 */
	
	public static String getOntologyVertexByKey(int ontologyId, String key) {
		return "g.V()"
				+ ".and("
					+ "__.has('" + VERTEX_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + "), "
					+ "__.has('" + VERTEX_PROPERTY_KEY_KEY + "', '" + key + "'))"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**************************************************************************
	 * EDGE-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all edges
	 * @return
	 */
	
	public static String getEdges() {
		return "g.V()"
				+ ".bothE()"
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestWebhookEventId', "
					+ "'relationship')"
				+ ".by(id())"
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
	
	public static String getEdge(long edgeId) {
		return "g.E(" + edgeId + ")"
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestWebhookEventId', "
					+ "'relationship')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**************************************************************************
	 * EDGE AND ONTOLOGY-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all edges belonging to a specific ontology ID
	 * @param ontologyId
	 * @return
	 */
	
	public static String getOntologyEdges(int ontologyId) {
		return "g.V()"
				+ ".bothE()"
					+ ".has('" + EDGE_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + ")"
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestWebhookEventId', "
					+ "'relationship')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}

}
