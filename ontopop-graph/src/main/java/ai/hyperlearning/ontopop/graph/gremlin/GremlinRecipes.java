package ai.hyperlearning.ontopop.graph.gremlin;

import java.util.Map;

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
	 * GRAPH-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Export graph data to disk. This supports IO.graphson, IO.graphml and
	 * IO.gryo formats.
	 * @param filePath
	 * @param format
	 * @return
	 */
	
	public static String writeGraph(String filePath, String format) {
		return "g.io(" + filePath + ")"
				+ ".with(IO.writer,IO." + format + ")"
				+ ".write()"
				+ ".iterate()";
	}
	
	/**************************************************************************
	 * GREMLIN QUERY PROPERTY VALUE RESOLVER
	 *************************************************************************/
	
	private static String resolveHasPropertyValue(Object propertyValue) {
		return propertyValue instanceof String ? 
				"'" + propertyValue + "'" : propertyValue.toString(); 
	}
	
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
	 * Get all vertices of a given label
	 * @param label
	 * @return
	 */
	
	public static String getVertices(String label) {
		return "g.V()"
				+ ".hasLabel('" + label + "')"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all vertices of a given label with a given property key/value pair
	 * @param label
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String getVertices(String label, 
			String propertyKey, Object propertyValue) {
		return "g.V()"
				+ ".hasLabel('" + label + "')"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".valueMap(true)"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all vertices with a given property key/value pair
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String getVertices(
			String propertyKey, Object propertyValue) {
		return "g.V()"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
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
	
	
	/**
	 * Add a new vertex given the vertex label, vertex ID and 
	 * map of property key/value pairs 
	 * @param label
	 * @param properties
	 * @return
	 */
	
	public static String addVertex(
			String label, long vertexId, Map<String, Object> properties) {
		StringBuffer query = new StringBuffer(
				"g.addV('" + label + "')"
					+ ".property('id', " + vertexId + ")");
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			query.append(
					".property('" + entry.getKey() + "', " 
							+ resolveHasPropertyValue(entry.getValue()) + ")");
		}
		return query.toString();
	}
	
	/**
	 * Update a vertex property key/value pair
	 * @param vertexId
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String updateVertex(
			long vertexId, String propertyKey, Object propertyValue) {
		return "g.V(" + vertexId + ")"
				+ ".property('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")";
	}
	
	/**
	 * Update vertex property key/value pairs
	 * @param vertexId
	 * @param properties
	 * @return
	 */
	
	public static String updateVertex(
			long vertexId, Map<String, Object> properties) {
		StringBuffer query = new StringBuffer(
				"g.V(" + vertexId + ")");
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			query.append(
					".property('" + entry.getKey() + "', " 
							+ resolveHasPropertyValue(entry.getValue()) + ")");
		}
		return query.toString();
	}
	
	/**
	 * Delete all vertices
	 * @return
	 */
	
	public static String deleteVertices() {
		return "g.V()"
				+ ".drop()";
	}
	
	/**
	 * Delete all vertices with a given property key/value pair
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String deleteVertices(String propertyKey, 
			Object propertyValue) {
		return "g.V()"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".drop()";
	}
	
	/**
	 * Delete a vertex given its vertex ID
	 * @param vertexId
	 * @return
	 */
	
	public static String deleteVertex(long vertexId) {
		return "g.V(" + vertexId + ")"
				+ ".drop()";
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
	
	/**
	 * Delete all edges
	 * @return
	 */
	
	public static String deleteEdges() {
		return "g.E()"
				+ ".drop()";
	}
	
	/**
	 * Delete all edges with a given property key/value pair
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String deleteEdges(String propertyKey, 
			Object propertyValue) {
		String hasPropertyValue = propertyValue instanceof String ? 
				"'" + propertyValue + "'" : propertyValue.toString(); 
		return "g.E()"
				+ ".has('" + propertyKey + "', " + hasPropertyValue + ")"
				+ ".drop()";
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
