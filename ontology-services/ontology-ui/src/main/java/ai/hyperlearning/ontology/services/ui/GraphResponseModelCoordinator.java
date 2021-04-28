package ai.hyperlearning.ontology.services.ui;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Graph Response Model Coordinator
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class GraphResponseModelCoordinator {
	
	private static final String DEFAULT_GRAPH_RESPONSE_MODEL_CLASS_NAME = 
			"ai.hyperlearning.ontology.services.ui.GraphResponseModel";
	private static final String TRANSFORM_VERTEX_CENTRIX_GRAPH_TRAVERSAL_RESULT_METHOD_NAME = 
			"transformVertexCentricGraphTraversalResult";
	private static final String TRANSFORM_EDGE_CENTRIX_GRAPH_TRAVERSAL_RESULT_METHOD_NAME = 
			"transformEdgeCentricGraphTraversalResult";
	
	/**
	 * Transform the graph response model given a model version number
	 * @param response
	 * @param centricObjectClass
	 * @param modelVersion
	 * @return
	 */
	
	@SuppressWarnings("deprecation")
	public static String transform(List<Object> response, 
			Class<?> centricObjectClass, int modelVersion) {
		
		try {
			
			// Ascertain the graph response model version to use
			String graphResponseModelClassName = 
					DEFAULT_GRAPH_RESPONSE_MODEL_CLASS_NAME;
			if (modelVersion > 0)
				graphResponseModelClassName = graphResponseModelClassName 
					+ modelVersion;
			
			// Instantiate a new instance of the relevant graph response model
			Class<?> transformerClass = 
					Class.forName(graphResponseModelClassName);
			Object transformerInstance = transformerClass.newInstance();
			
			// Execute the graph response model transformation
			Method transformerMethod = (centricObjectClass == Vertex.class) ? 
							transformerClass.getDeclaredMethod(
									TRANSFORM_VERTEX_CENTRIX_GRAPH_TRAVERSAL_RESULT_METHOD_NAME, 
									List.class) : 
							transformerClass.getDeclaredMethod(
									TRANSFORM_EDGE_CENTRIX_GRAPH_TRAVERSAL_RESULT_METHOD_NAME, 
									List.class);
			
			// Return the transformed graph response model
			return (String) transformerMethod.invoke(
					transformerInstance, response);
			
		} catch (Exception e) {
			return response.toString();
		}
		
	}

}
