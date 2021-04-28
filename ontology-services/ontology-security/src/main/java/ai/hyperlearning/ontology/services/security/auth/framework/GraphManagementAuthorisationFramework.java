package ai.hyperlearning.ontology.services.security.auth.framework;

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;

/**
 * Graph Management Authorisation Framework
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class GraphManagementAuthorisationFramework {

	/**
	 * Check whether the given user is authorised to take
	 * graph management actions on the given vertex
	 * @param graphDatabaseManager
	 * @param vertexId
	 * @param userId
	 * @return
	 */
	
	public static boolean isVertexManagementAuthorized(
			GraphDatabaseManager graphDatabaseManager, 
			int vertexId, String userId) {
		String userDefinedVertexUserId = graphDatabaseManager
				.getUserDefinedVertexUserId(vertexId);
		if (userDefinedVertexUserId == null)
			return false;
		else
			return (userDefinedVertexUserId.equals(userId)) ? 
					true : false;
	}
	
	/**
	 * Check whether the given user is authorised to take
	 * graph management actions on the given edge
	 * @param graphDatabaseManager
	 * @param edgeId
	 * @param userId
	 * @return
	 */
	
	public static boolean isEdgeManagementAuthorized(
			GraphDatabaseManager graphDatabaseManager, 
			int edgeId, String userId) {
		String userDefinedEdgeUserId = graphDatabaseManager
				.getUserDefinedEdgeUserId(edgeId);
		if (userDefinedEdgeUserId == null)
			return false;
		else
			return (userDefinedEdgeUserId.equals(userId)) ? 
					true : false;
	}
	
}
