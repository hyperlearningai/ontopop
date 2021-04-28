package ai.hyperlearning.ontology.services.graphdb.interfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Interface to an underlying Graph Database implementation
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public interface GraphDatabaseService {
	
	/**************************************************************************
	 * GRAPH MANAGEMENT
	 *************************************************************************/
	
	public GraphTraversalSource openGraph() throws Exception;
	
	public void closeGraph() throws Exception;
	
	public void dropGraph();
	
	public void commit();
	
	public void rollback();
	
	public void writeGraph(String filepath) throws Exception;
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/
	
	public void createSchema();
	
	/**************************************************************************
	 * VERTEX MANAGEMENT
	 *************************************************************************/
	
	public boolean isUserDefinedVertex(int vertexId);
	
	public boolean isUserDefinedVertex(Vertex vertex);
	
	public String getUserDefinedVertexUserId(int vertexId);
	
	public String getUserDefinedVertexUserId(Vertex vertex);
	
	public Vertex addVertex(String label, Map<String, Object> propertyMap);
	
	public Vertex addUserDefinedVertex(String label, String userId);
	
	public Vertex updateVertex(int vertexId, 
			String propertyKey, Object propertyValue);
	
	public Vertex updateVertex(int vertexId, Map<String, Object> propertyMap);
	
	public Vertex deleteVertex(int vertexId);
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 *************************************************************************/
	
	public boolean isUserDefinedEdge(int edgeId);
	
	public boolean isUserDefinedEdge(Edge edge);
	
	public String getUserDefinedEdgeUserId(int edgeId);
	
	public String getUserDefinedEdgeUserId(Edge edge);
	
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, String label, 
			Map<String, Object> propertyMap);
	
	public Edge addUserDefinedEdge(String label, 
			int sourceVertexId, int targetVertexId, String userId);
	
	public Edge updateEdge(int edgeId, 
			String propertyKey, Object propertyValue);
	
	public Edge updateEdge(int edgeId, Map<String, Object> propertyMap);
	
	public Edge deleteEdge(int edgeId);
	
	/**************************************************************************
	 * QUERY MANAGEMENT
	 *************************************************************************/
	
	public List<Object> query(String gremlinQuery) 
			throws ScriptException, InterruptedException, ExecutionException;

}
