package ai.hyperlearning.ontology.services.ui;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default Graph Response Model Transformations
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class GraphResponseModel implements IGraphResponseModel {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	/**
	 * Write the vertex-centric graph traversal result as a string
	 * with no transformations
	 */
	
	@Override
	public String transformVertexCentricGraphTraversalResult(
			List<Object> result) {
		try {
			return MAPPER.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			return result.toString();
		}
		
	}

	/**
	 * Write the edge-centric graph traversal result as a string
	 * with no transformations
	 */
	
	@Override
	public String transformEdgeCentricGraphTraversalResult(
			List<Object> result) {
		try {
			return MAPPER.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			return result.toString();
		}
	}

}
