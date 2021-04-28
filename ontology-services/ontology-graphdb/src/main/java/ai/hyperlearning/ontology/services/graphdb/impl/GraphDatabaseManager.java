package ai.hyperlearning.ontology.services.graphdb.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.jsr223.ConcurrentBindings;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.hyperlearning.ontology.services.graphdb.apps.GraphDatabaseLoader;
import ai.hyperlearning.ontology.services.graphdb.interfaces.GraphDatabaseService;

/**
 * Apache TinkerPop-compliant Graph Database Manager
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class GraphDatabaseManager implements GraphDatabaseService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(
			GraphDatabaseManager.class);
	protected String configurationFilename;
	protected Configuration configuration;
	protected Graph graph;
	protected GraphTraversalSource g;
	protected GremlinExecutor gremlinExecutor;
	protected ConcurrentBindings bindings;
	protected boolean supportsTransactions;
    protected boolean supportsSchema;
    protected boolean supportsGeoshape;

	public GraphDatabaseManager(final String configurationFilename) {
		this.configurationFilename = configurationFilename;
	}
	
	// User defined property keys
	public static final String PROPERTY_KEY_USER_DEFINED = "userDefined";
	public static final String PROPERTY_KEY_USER_ID = "userId";
	public static final String PROPERTY_KEY_UPPER_ONTOLOGY = "upperOntology";
	public static final Set<String> PROPERTY_KEYS_VERTEX_COMMON = 
			new HashSet<String>(Arrays.asList(
					"id", 
					"nodeId", 
					"label", 
					PROPERTY_KEY_UPPER_ONTOLOGY, 
					PROPERTY_KEY_USER_DEFINED, 
					PROPERTY_KEY_USER_ID));
	public static final Set<String> PROPERTY_KEYS_EDGE_COMMON = 
			new HashSet<String>(Arrays.asList(
					"id", 
					"edgeId", 
					"label", 
					PROPERTY_KEY_USER_DEFINED, 
					PROPERTY_KEY_USER_ID, 
					"edgeId", 
					"from", 
					"to"));
	
	/**************************************************************************
	 * GRAPH MANAGEMENT
	 *************************************************************************/
	
	@Override
	public GraphTraversalSource openGraph() throws Exception {
		
		LOGGER.info("Opening an Apache TinkerPop-compliant graph engine");
		configuration = new PropertiesConfiguration(configurationFilename);
		graph = GraphFactory.open(configuration);
		g = graph.traversal();
		
		LOGGER.info("Opening a Gremlin Executor Engine");
		bindings = new ConcurrentBindings();
		bindings.putIfAbsent("graph", graph);
		bindings.putIfAbsent("g", g);
		gremlinExecutor = GremlinExecutor.build()
				.evaluationTimeout(15000L)
				.globalBindings(bindings)
				.create();
		return g;
	
	}

	@Override
	public void closeGraph() throws Exception {
		
		LOGGER.info("Closing the graph engine");
		try {
			if (gremlinExecutor != null)
				gremlinExecutor.close();
            if (g != null)
                g.close();
            if (graph != null)
                graph.close();
        } catch (Exception e) {
        	
        } finally {
        	gremlinExecutor = null;
        	g = null;
            graph = null;
        }
		
	}

	@Override
	public void dropGraph() {
		g.V().drop().iterate();
	}
	
	@Override
	public void commit() {
		if (supportsTransactions) {
			g.tx().commit();
		}
	}

	@Override
	public void rollback() {
		if (supportsTransactions) {
			g.tx().rollback();
		}
	}
	
	@Override
	public void writeGraph(String filepath) throws Exception {
		g.io(filepath).with(IO.writer, IO.gryo).write().iterate();
	}
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/

	@Override
	public void createSchema() {
		
	}
	
	/**************************************************************************
	 * VERTEX MANAGEMENT
	 *************************************************************************/
	
	@Override
	public boolean isUserDefinedVertex(int vertexId) {
		Vertex vertex = g.V(vertexId).next();
		return (boolean) vertex.value(PROPERTY_KEY_USER_DEFINED);
	}
	
	@Override
	public boolean isUserDefinedVertex(Vertex vertex) {
		return (boolean) vertex.value(PROPERTY_KEY_USER_DEFINED);
	}
	
	@Override
	public String getUserDefinedVertexUserId(int vertexId) {
		Vertex vertex = g.V(vertexId).next();
		if ((boolean) vertex.value(PROPERTY_KEY_USER_DEFINED))
			return (String) vertex.value(PROPERTY_KEY_USER_ID);
		else
			return null;
	}
	
	@Override
	public String getUserDefinedVertexUserId(Vertex vertex) {
		if ((boolean) vertex.value(PROPERTY_KEY_USER_DEFINED))
			return (String) vertex.value(PROPERTY_KEY_USER_ID);
		else
			return null;
	}
	
	@Override
	public Vertex addVertex(String label, Map<String, Object> propertyMap) {
		
		GraphTraversal<Vertex, Vertex> graphTraversal = g.addV(label);
		graphTraversal.property(PROPERTY_KEY_USER_DEFINED, false);
		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			graphTraversal.property(entry.getKey(), entry.getValue());
			if ( entry.getKey().equalsIgnoreCase("NODEID") )
				graphTraversal.property(T.id, entry.getValue());
		}
		final Vertex vertex = graphTraversal.next();
		return vertex;
		
	}
	
	@Override
	public Vertex addUserDefinedVertex(String label, String userId) {
		
		GraphTraversal<Vertex, Vertex> graphTraversal = g.addV(label);
		graphTraversal.property(PROPERTY_KEY_USER_DEFINED, true);
		graphTraversal.property(PROPERTY_KEY_USER_ID, userId);
		graphTraversal.property(PROPERTY_KEY_UPPER_ONTOLOGY, false);
		final Vertex vertex = graphTraversal.next();
		return vertex;
		
	}
	
	@Override
	public Vertex updateVertex(int vertexId, 
			String propertyKey, Object propertyValue) {
		
		Vertex vertex = g.V(vertexId).next();
		if (isUserDefinedVertex(vertex))
			vertex.property(propertyKey, propertyValue);
		return vertex;
		
	}
	
	@Override
	public Vertex updateVertex(int vertexId, Map<String, Object> propertyMap) {
		
		Vertex vertex = g.V(vertexId).next();
		if (isUserDefinedVertex(vertex)) {
			for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
				if ( !PROPERTY_KEYS_VERTEX_COMMON.contains(entry.getKey()) )
					vertex.property(entry.getKey(), entry.getValue());
			}
		}
		
		return vertex;
		
	}
	
	@Override
	public Vertex deleteVertex(int vertexId) {
		
		Vertex vertex = g.V(vertexId).next();
		if (isUserDefinedVertex(vertex))
			vertex.remove();
		return vertex;
		
	}
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 *************************************************************************/
	
	@Override
	public boolean isUserDefinedEdge(int edgeId) {
		Edge edge = g.E(edgeId).next();
		return (boolean) edge.value(PROPERTY_KEY_USER_DEFINED);
	}
	
	@Override
	public boolean isUserDefinedEdge(Edge edge) {
		return (boolean) edge.value(PROPERTY_KEY_USER_DEFINED);
	}
	
	@Override
	public String getUserDefinedEdgeUserId(int edgeId) {
		Edge edge = g.E(edgeId).next();
		if ((boolean) edge.value(PROPERTY_KEY_USER_DEFINED))
			return (String) edge.value(PROPERTY_KEY_USER_ID);
		else
			return null;
	}
	
	@Override
	public String getUserDefinedEdgeUserId(Edge edge) {
		if ((boolean) edge.value(PROPERTY_KEY_USER_DEFINED))
			return (String) edge.value(PROPERTY_KEY_USER_ID);
		else
			return null;
	}
	
	@Override
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, String label, 
			Map<String, Object> propertyMap) {
		
		Edge edge = null;
		if ( propertyMap.containsKey("edgeId") ) {
			int edgeId = (int) propertyMap.get("edgeId");
			edge = sourceVertex.addEdge(label, targetVertex, T.id, edgeId);
		} else
			edge = sourceVertex.addEdge(label, targetVertex);
		edge.property(PROPERTY_KEY_USER_DEFINED, false);
		for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
			edge.property(entry.getKey(), entry.getValue());
		}
		return edge;
		
	}
	
	@Override
	public Edge addUserDefinedEdge(String label, 
			int sourceVertexId, int targetVertexId, String userId) {
		
		Vertex sourceVertex = g.V(sourceVertexId).next();
		Vertex targetVertex = g.V(targetVertexId).next();
		Edge edge = sourceVertex.addEdge(label, targetVertex);
		edge.property("objectPropertyRdfsLabel", 
				GraphDatabaseLoader.DEFAULT_EDGE_LABEL_CAPTION);
		edge.property(PROPERTY_KEY_USER_DEFINED, true);
		edge.property(PROPERTY_KEY_USER_ID, userId);
		return edge;
		
	}
	
	@Override
	public Edge updateEdge(int edgeId, 
			String propertyKey, Object propertyValue) {
		
		Edge edge = g.E(edgeId).next();
		if (isUserDefinedEdge(edge)) {
			edge.property(propertyKey, propertyValue);
			if (propertyKey.equals("rdfsLabel"))
				edge.property("objectPropertyRdfsLabel", propertyValue);
			if (propertyKey.equals("rdfAbout"))
				edge.property("objectPropertyRdfAbout", propertyValue);
		}
			
		return edge;
		
	}
	
	@Override
	public Edge updateEdge(int edgeId, Map<String, Object> propertyMap) {
		
		Edge edge = g.E(edgeId).next();
		if (isUserDefinedEdge(edge)) {
			for (Map.Entry<String, Object> entry : propertyMap.entrySet()) {
				if ( !PROPERTY_KEYS_EDGE_COMMON.contains(entry.getKey()) ) {
					edge.property(entry.getKey(), entry.getValue());
					if (entry.getKey().equals("rdfsLabel"))
						edge.property("objectPropertyRdfsLabel", 
								entry.getValue());
					if (entry.getKey().equals("rdfAbout"))
						edge.property("objectPropertyRdfAbout", 
								entry.getValue());
				}
			}
		}
		
		return edge;
		
	}
	
	@Override
	public Edge deleteEdge(int edgeId) {
		
		Edge edge = g.E(edgeId).next();
		if (isUserDefinedEdge(edge))
			edge.remove();
		return edge;
		
	}
	
	/**************************************************************************
	 * QUERY MANAGEMENT
	 *************************************************************************/

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> query(String gremlinQuery) 
			throws ScriptException, InterruptedException, ExecutionException {
		
		CompletableFuture<Object> evalResult = gremlinExecutor
				.eval(gremlinQuery);
		GraphTraversal<?, ?> actualResult = 
				(GraphTraversal<?, ?>) evalResult.get();
		return (List<Object>) actualResult.toList();
		
	}

}
