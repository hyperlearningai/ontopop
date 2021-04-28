package ai.hyperlearning.ontology.services.ui;

import java.util.List;

/**
 * Graph Response Model Transformation Interface
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public interface IGraphResponseModel {
	
	/**
	 * Transform a vertex-centric graph traversal result 
	 * to a JSON-friendly string
	 * @param result
	 * @return
	 */
	
	public String transformVertexCentricGraphTraversalResult(
			List<Object> result);
	
	/**
	 * Transform an edge-centric graph traversal result 
	 * to a JSON-friendly string
	 * @param result
	 * @return
	 */
	
	public String transformEdgeCentricGraphTraversalResult(
			List<Object> result);

}
