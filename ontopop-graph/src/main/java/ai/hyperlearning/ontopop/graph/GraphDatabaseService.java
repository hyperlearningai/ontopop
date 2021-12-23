package ai.hyperlearning.ontopop.graph;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.TransactionException;

import ai.hyperlearning.ontopop.graph.model.SimpleGraphEdge;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphVertex;

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
	
	public void deleteSubGraph(String propertyKey, Object propertyValue);
	
	public void commit() throws TransactionException;
	
	public void rollback() throws TransactionException;
	
	public void serializeGraph(String filepath) throws IOException;
	
	public void cleanup() throws Exception;
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/
	
	public void createSchema();
	
	/**************************************************************************
	 * VERTEX MANAGEMENT
	 *************************************************************************/
	
	public GraphTraversal<Vertex, Vertex> getVertices();
	
	public GraphTraversal<Vertex, Vertex> getVertices(String label);
	
	public GraphTraversal<Vertex, Vertex> getVertices(
			String label, String propertyKey, Object propertyValue);
	
	public GraphTraversal<Vertex, Vertex> getVertices(
			String propertyKey, Object propertyValue);
	
	public Vertex getVertex(long vertexId) 
			throws NoSuchElementException;
	
	public Vertex getVertex(
			String label, String propertyKey, Object propertyValue);
	
	public Vertex getVertex(
			String propertyKey, Object propertyValue);
	
	public void addVertices(String label, Set<SimpleGraphVertex> vertices);
	
	public void addVertices(String label, List<Map<String, Object>> propertyMaps);
	
	public Vertex addVertex(String label, Map<String, Object> properties);
	
	public Vertex updateVertex(long vertexId, 
			String propertyKey, Object propertyValue) 
					throws NoSuchElementException;
	
	public Vertex updateVertex(long vertexId, Map<String, Object> properties) 
			throws NoSuchElementException;
	
	public Vertex deleteVertex(long vertexId) 
			throws NoSuchElementException;
	
	public void deleteVertices();
	
	public void deleteVertices(String propertyKey, Object propertyValue);
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 *************************************************************************/
	
	public GraphTraversal<Edge, Edge> getEdges();
	
	public GraphTraversal<Edge, Edge> getEdges(String label);
	
	public GraphTraversal<Edge, Edge> getEdges(
			String label, String propertyKey, Object propertyValue);
	
	public GraphTraversal<Edge, Edge> getEdges(
			String propertyKey, Object propertyValue);
	
	public Edge getEdge(long edgeId) 
			throws NoSuchElementException;
	
	public Edge getEdge(
			String label, String propertyKey, Object propertyValue);
	
	public Edge getEdge(
			String propertyKey, Object propertyValue);
	
	public void addEdges(List<SimpleGraphEdge> edges);
	
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, String label, 
			Map<String, Object> properties);
	
	public Edge updateEdge(long edgeId, 
			String propertyKey, Object propertyValue) 
					throws NoSuchElementException;
	
	public Edge updateEdge(long edgeId, Map<String, Object> properties) 
			throws NoSuchElementException;
	
	public Edge deleteEdge(long edgeId) 
			throws NoSuchElementException;
	
	public void deleteEdges();
	
	public void deleteEdges(String propertyKey, Object propertyValue);
	
	/**************************************************************************
	 * QUERY MANAGEMENT
	 *************************************************************************/
	
	public List<Object> query(String gremlinQuery) 
			throws ScriptException, InterruptedException, ExecutionException;
	
}
