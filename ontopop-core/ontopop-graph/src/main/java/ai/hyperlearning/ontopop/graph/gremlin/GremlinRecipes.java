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
			"vertexKey";
	private static final String EDGE_PROPERTY_KEY_ONTOLOGY_ID = 
			"ontologyId";
	private static final String PROPERTY_KEY_ONTOLOGY_LABEL = 
			"label";
	private static final String PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT = 
			"rdfsLabel";
	
	private GremlinRecipes() {
		throw new IllegalStateException("Gremlin Recipes utility class "
				+ "cannot be instantiated.");
	}
	
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
	 * GREMLIN QUERY RESOLVERS
	 *************************************************************************/
	
	/**
	 * Resolve has filter property value
	 * @param propertyValue
	 * @return
	 */
	
	public static String resolveHasPropertyValue(Object propertyValue) {
		return propertyValue instanceof String ? 
				"'" + propertyValue.toString()
						.replace("'", "")
						.replace("\n", " ")
						.trim() + "'" : 
					propertyValue.toString(); 
	}
	
	/**
	 * Resolve vertex ID filter
	 * @param vertexId
	 * @param supportsNonStringIds
	 * @return
	 */
	
	public static String resolveVertexId(
			long vertexId, boolean supportsNonStringIds) {
		return supportsNonStringIds ? 
				"g.V(" + vertexId + ")" : 
					"g.V('" + vertexId + "')";
	}
	
	/**
	 * Resolve edge ID filter
	 * @param edgeId
	 * @param supportsNonStringIds
	 * @return
	 */
	
	public static String resolveEdgeId(
			long edgeId, boolean supportsNonStringIds) {
		return supportsNonStringIds ? 
				"g.E(" + edgeId + ")" :
					"g.E('" + edgeId + "')";
	}
	
	/**************************************************************************
	 * GREMLIN QUERY TRAVERSAL OPTIONS
	 *************************************************************************/
	
	/**
	 * Unfold value maps given support for .by traversals
	 * @param supportsTraversalsBy
	 * @return
	 */
	
	public static String unfoldValueMap(boolean supportsTraversalsBy) {
		return supportsTraversalsBy ? 
				".by(unfold())" : ".unfold()";
	}
	
	/**
	 * Execute a traversal
	 * https://tinkerpop.apache.org/docs/current/tutorials/the-gremlin-console/#result-iteration
	 * @param iterate
	 * @return
	 */
	
	public static String iterateTraversal(boolean iterate) {
	    return iterate ? ".iterate()" : "";
	}
	
	/**************************************************************************
	 * VERTEX-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all vertices
	 * @return
	 */
	
	public static String getVertices(boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".valueMap()";
	}
	
	/**
	 * Get all vertices of a given label
	 * @param label
	 * @return
	 */
	
	public static String getVertices(
			String label, boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".hasLabel('" + label + "')"
				+ ".valueMap()";
	}
	
	/**
	 * Get all vertices of a given label with a given property key/value pair
	 * @param label
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String getVertices(String label, 
			String propertyKey, Object propertyValue, 
			boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".hasLabel('" + label + "')"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".valueMap()";
	}
	
	/**
	 * Get all vertices with a given property key/value pair
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String getVertices(
			String propertyKey, Object propertyValue, 
			boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".valueMap()";
	}
	
	/**
	 * Get a specific vertex given a vertex ID
	 * @param id
	 * @return
	 */
	
	public static String getVertex(
			long vertexId, boolean supportsNonStringIds, 
			boolean supportsTraversalsBy) {
		return resolveVertexId(vertexId, supportsNonStringIds)
				+ ".valueMap()";
	}
	
	/**
	 * Get the bi-directional edges for a given vertex
	 * @param id
	 * @return
	 */
	
	public static String getVertexEdges(
			long vertexId, boolean supportsNonStringIds, 
			boolean supportsTraversalsBy) {
		return resolveVertexId(vertexId, supportsNonStringIds)
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
	
	public static String getVertexEdgesOut(
			long vertexId, boolean supportsNonStringIds, 
			boolean supportsTraversalsBy) {
		return resolveVertexId(vertexId, supportsNonStringIds)
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
	
	public static String getVertexEdgesIn(
			long vertexId, boolean supportsNonStringIds, 
			boolean supportsTraversalsBy) {
		return resolveVertexId(vertexId, supportsNonStringIds)
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
	
	public static String getVerticesPropertyKeyUniqueValues(
			String propertyKey) {
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
			String label, long vertexId, Map<String, Object> properties, 
			boolean supportsNonStringIds, boolean supportsUserDefinedIds, 
			boolean iterate) {
		StringBuilder query = new StringBuilder("g.addV('" + label + "')");
		if ( supportsUserDefinedIds )
	        query.append(supportsNonStringIds ? 
	                ".property('id', " + vertexId + ")" : 
	                    ".property('id', '" + vertexId + "')");
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : entry.getKey();
			query.append(
					".property('" + key + "', " 
							+ resolveHasPropertyValue(entry.getValue()) + ")");
		}
		return query.toString() + iterateTraversal(iterate);
	}
	
	/**
	 * Update a vertex property key/value pair
	 * @param vertexId
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String updateVertex(
			long vertexId, String propertyKey, Object propertyValue, 
			boolean supportsNonStringIds, boolean iterate) {
		String key = propertyKey.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
				PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : propertyKey;
		return resolveVertexId(vertexId, supportsNonStringIds)
				+ ".property('" + key + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ iterateTraversal(iterate);
	}
	
	/**
	 * Update vertex property key/value pairs
	 * @param vertexId
	 * @param properties
	 * @return
	 */
	
	public static String updateVertex(
			long vertexId, Map<String, Object> properties, 
			boolean supportsNonStringIds, boolean iterate) {
		StringBuilder query = new StringBuilder(
				resolveVertexId(vertexId, supportsNonStringIds));
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : entry.getKey();
			query.append(
					".property('" + key + "', " 
							+ resolveHasPropertyValue(entry.getValue()) + ")");
		}
		return query.toString() + iterateTraversal(iterate);
	}
	
	/**
	 * Delete all vertices
	 * @return
	 */
	
	public static String deleteVertices(boolean iterate) {
		return "g.V()"
				+ ".drop()"
		        + iterateTraversal(iterate);
	}
	
	/**
	 * Delete all vertices with a given property key/value pair
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String deleteVertices(String propertyKey, 
			Object propertyValue, boolean iterate) {
		return "g.V()"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".drop()"
				+ iterateTraversal(iterate);
	}
	
	/**
	 * Delete a vertex given its vertex ID
	 * @param vertexId
	 * @return
	 */
	
	public static String deleteVertex(
			long vertexId, boolean supportsNonStringIds, boolean iterate) {
		return resolveVertexId(vertexId, supportsNonStringIds)
				+ ".drop()"
				+ iterateTraversal(iterate);
	}
	
	/**************************************************************************
	 * VERTEX AND ONTOLOGY-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all vertices belonging to a specific ontology ID
	 * @param ontologyId
	 * @return
	 */
	
	public static String getOntologyVertices(
			int ontologyId, boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".has('" + VERTEX_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + ")"
				+ ".valueMap()";
	}
	
	/**
	 * Get a specific vertex belonging to a specific ontology ID by IRI
	 * @param ontologyId
	 * @param iri
	 * @return
	 */
	
	public static String getOntologyVertexByIri(
			int ontologyId, String iri, boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".and("
					+ "__.has('" + VERTEX_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + "), "
					+ "__.has('" + VERTEX_PROPERTY_KEY_IRI + "', '" + iri + "'))"
				+ ".valueMap()";
	}
	
	/**
	 * Get a specific vertex belonging to a specific ontology ID by key
	 * @param ontologyId
	 * @param key
	 * @return
	 */
	
	public static String getOntologyVertexByKey(
			int ontologyId, String key, boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".and("
					+ "__.has('" + VERTEX_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + "), "
					+ "__.has('" + VERTEX_PROPERTY_KEY_KEY + "', '" + key + "'))"
				+ ".valueMap()";
	}
	
	/**************************************************************************
	 * EDGE-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all edges
	 * @return
	 */
	
	public static String getEdges(
			boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".bothE()"
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestGitWebhookId', "
					+ "'relationship')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all edges given an edge label
	 * @return
	 */
	
	public static String getEdges(
			String label, boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".bothE()"
				+ ".hasLabel('" + label + "')"
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestGitWebhookId', "
					+ "'relationship')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Get all edges given an edge label and a property key/value pair
	 * @return
	 */
	
	public static String getEdges(
			String label, String propertyKey, Object propertyValue, 
			boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".bothE()"
				+ ".hasLabel('" + label + "')"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".valueMap()";
	}
	
	/**
	 * Get all edges given a property key/value pair
	 * @return
	 */
	
	public static String getEdges(
			String propertyKey, Object propertyValue, 
			boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".bothE()"
				+ ".has('" + propertyKey + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ ".valueMap()";
	}
	
	/**
	 * Get a specific edge given an edge ID
	 * @param id
	 * @return
	 */
	
	public static String getEdge(long edgeId, boolean supportsNonStringIds, 
			boolean supportsTraversalsBy) {
		return resolveEdgeId(edgeId, supportsNonStringIds)
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestGitWebhookId', "
					+ "'relationship')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}
	
	/**
	 * Add an edge given the source vertex ID, target vertex ID, 
	 * edge label and edge property map
	 * @param sourceVertexId
	 * @param targetVertexId
	 * @param label
	 * @param properties
	 */
	
	public static String addEdge(long sourceVertexId, long targetVertexId, 
			String label, Map<String, Object> properties, 
			boolean supportsNonStringIds, 
			boolean supportsUserDefinedIds, 
			boolean iterate) {
		
	    // Generate the edge builder query
	    StringBuilder query = null;
	    if ( supportsUserDefinedIds ) {
	        
	        query = new StringBuilder(supportsNonStringIds ? 
	                "g.V(" + sourceVertexId + ").as('a')"
	                    + ".V(" + targetVertexId + ")"
	                    + ".addE('" + label + "')"
	                    + ".from('a')" : 
	                        "g.V('" + sourceVertexId + "').as('a')"
	                        + ".V('" + targetVertexId + "')"
	                        + ".addE('" + label + "')"
	                        + ".from('a')");
	        
	    } else {
	        
	        query = new StringBuilder(
	                "g.V().has('vertexId', " + sourceVertexId + ").as('a')"
                        + ".V().has('vertexId', " + targetVertexId + ")"
                        + ".addE('" + label + "')"
                        + ".from('a')");
	        
	    }
		
		// Add edge properties to the edge builder query
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : entry.getKey();
			query.append(".property('" + key + "', " 
					+ resolveHasPropertyValue(entry.getValue()) + ")");
		}
		return query.toString() + iterateTraversal(iterate);
		
	}
	
	/**
	 * Update an edge property key/value pair
	 * @param edgeId
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String updateEdge(long edgeId, 
			String propertyKey, Object propertyValue, 
			boolean supportsNonStringIds, boolean iterate) {
		String key = propertyKey.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
				PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : propertyKey;
		return resolveEdgeId(edgeId, supportsNonStringIds)
				+ ".property('" + key + "', " 
					+ resolveHasPropertyValue(propertyValue) + ")"
				+ iterateTraversal(iterate);
	}
	
	/**
	 * Update edge property key/value pairs
	 * @param edgeId
	 * @param properties
	 * @return
	 */
	
	public static String updateEdge(
			long edgeId, Map<String, Object> properties, 
			boolean supportsNonStringIds, boolean iterate) {
		StringBuilder query = new StringBuilder(
				resolveEdgeId(edgeId, supportsNonStringIds));
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : entry.getKey();
			query.append(".property('" + key + "', " 
					+ resolveHasPropertyValue(entry.getValue()) + ")");
		}
		return query.toString() + iterateTraversal(iterate);
	}
	
	/**
	 * Delete all edges
	 * @return
	 */
	
	public static String deleteEdges(boolean iterate) {
		return "g.E()"
				+ ".drop()"
		        + iterateTraversal(iterate);
	}
	
	/**
	 * Delete all edges with a given property key/value pair
	 * @param propertyKey
	 * @param propertyValue
	 * @return
	 */
	
	public static String deleteEdges(String propertyKey, 
			Object propertyValue, boolean iterate) {
		String hasPropertyValue = propertyValue instanceof String ? 
				"'" + propertyValue + "'" : propertyValue.toString(); 
		return "g.E()"
				+ ".has('" + propertyKey + "', " + hasPropertyValue + ")"
				+ ".drop()"
				+ iterateTraversal(iterate);
	}
	
	/**
	 * Delete a specific edge given an edge ID
	 * @param edgeId
	 * @return
	 */
	
	public static String deleteEdge(
			long edgeId, boolean supportsNonStringIds, boolean iterate) {
		return resolveEdgeId(edgeId, supportsNonStringIds)
				+ ".drop()"
				+ iterateTraversal(iterate);
	}
	
	/**************************************************************************
	 * EDGE AND ONTOLOGY-CENTRIC GREMLIN RECIPES
	 *************************************************************************/
	
	/**
	 * Get all edges belonging to a specific ontology ID
	 * @param ontologyId
	 * @return
	 */
	
	public static String getOntologyEdges(
			int ontologyId, boolean supportsTraversalsBy) {
		return "g.V()"
				+ ".bothE()"
					+ ".has('" + EDGE_PROPERTY_KEY_ONTOLOGY_ID + "', " + ontologyId + ")"
				+ ".project("
					+ "'sourceVertexKey', "
					+ "'sourceVertexId', "
					+ "'targetVertexKey', "
					+ "'targetVertexId', "
					+ "'ontologyId', "
					+ "'latestGitWebhookId', "
					+ "'relationship')"
				+ ".by(id())"
				+ ".by(valueMap(true))"
				+ ".by(outV().id())"
				+ ".by(inV().id())"
				+ ".by(unfold())";
	}

}
