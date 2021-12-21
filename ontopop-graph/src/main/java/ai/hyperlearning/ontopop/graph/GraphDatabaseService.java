package ai.hyperlearning.ontopop.graph;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Graph Database Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface GraphDatabaseService {
	
	/**************************************************************************
	 * GRAPH INSTANCE MANAGEMENT
	 *************************************************************************/

	public GraphTraversalSource openGraph() throws IOException;
	
	public void closeGraph() throws Exception;
	
	public void deleteGraph();
	
	public void commit();
	
	public void rollback();
	
	public void serializeGraph(String filepath) throws IOException;
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/
	
	public void createSchema();
	
	/**************************************************************************
	 * VERTEX MANAGEMENT
	 *************************************************************************/
	
	public Vertex addVertex(String label, Map<String, Object> properties);
	
	public Vertex updateVertex(long vertexId, 
			String propertyKey, Object propertyValue);
	
	public Vertex updateVertex(long vertexId, Map<String, Object> properties);
	
	public Vertex deleteVertex(long vertexId);
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 *************************************************************************/
	
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, String label, 
			Map<String, Object> properties);
	
	public Edge updateEdge(long edgeId, 
			String propertyKey, Object propertyValue);
	
	public Edge updateEdge(long edgeId, Map<String, Object> properties);
	
	public Edge deleteEdge(long edgeId);
	
	/**************************************************************************
	 * QUERY MANAGEMENT
	 *************************************************************************/
	
	public List<Object> query(String gremlinQuery) 
			throws ScriptException, InterruptedException, ExecutionException;
	
}
