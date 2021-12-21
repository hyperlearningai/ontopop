package ai.hyperlearning.ontopop.graph.gremlin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.jsr223.ConcurrentBindings;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;

/**
 * Gremlin Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GremlinGraphDatabaseService implements GraphDatabaseService {

	@Autowired
	@Qualifier("gremlinGraphTraversalSource")
	private GraphTraversalSource gremlinGraphTraversalSource;
	
	protected GraphTraversalSource g;
	protected GremlinExecutor gremlinExecutor;
	protected ConcurrentBindings bindings = new ConcurrentBindings();
	protected boolean supportsSchema = false;
	protected boolean supportsTransactions = false;
	protected boolean supportsGeoshape = false;
	
	/**************************************************************************
	 * GRAPH INSTANCE MANAGEMENT
	 *************************************************************************/
	
	@Override
	public GraphTraversalSource openGraph() throws IOException {
		this.g = gremlinGraphTraversalSource;
		bindings.putIfAbsent("g", g);
		bindings.putIfAbsent("graph", g.getGraph());
		gremlinExecutor = GremlinExecutor.build()
				.evaluationTimeout(15000L)
				.globalBindings(bindings)
				.create();
		return g;
	}

	@Override
	public void closeGraph() throws Exception {
		if ( g != null )
			g.close();
	}

	@Override
	public void deleteGraph() {
		g.V().drop().iterate();
		g.E().drop().iterate();
	}

	@Override
	public void commit() {
		if ( supportsTransactions )
			g.tx().commit();
	}

	@Override
	public void rollback() {
		if ( supportsTransactions )
			g.tx().rollback();
	}

	@Override
	public void serializeGraph(String filepath) throws IOException {
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
	public Vertex addVertex(String label, Map<String, Object> properties) {
		
		GraphTraversal<Vertex, Vertex> graphTraversal = g.addV(label);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			graphTraversal.property(entry.getKey(), entry.getValue());
			if ( entry.getKey().equalsIgnoreCase("VERTEXID") )
				graphTraversal.property(T.id, entry.getValue());
		}
		final Vertex vertex = graphTraversal.next();
		return vertex;
		
	}

	@Override
	public Vertex updateVertex(long vertexId, 
			String propertyKey, Object propertyValue) {
		
		Vertex vertex = g.V(vertexId).next();
		vertex.property(propertyKey, propertyValue);
		return vertex;
		
	}

	@Override
	public Vertex updateVertex(long vertexId, Map<String, Object> properties) {
		
		Vertex vertex = g.V(vertexId).next();
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			vertex.property(entry.getKey(), entry.getValue());
		}
		return vertex;
		
	}

	@Override
	public Vertex deleteVertex(long vertexId) {
		
		Vertex vertex = g.V(vertexId).next();
		vertex.remove();
		return vertex;
		
	}
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 *************************************************************************/

	@Override
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, 
			String label, Map<String, Object> properties) {
		
		Edge edge = null;
		if ( properties.containsKey("edgeId") ) {
			long edgeId = (long) properties.get("edgeId");
			edge = sourceVertex.addEdge(label, targetVertex, T.id, edgeId);
		} else
			edge = sourceVertex.addEdge(label, targetVertex);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			edge.property(entry.getKey(), entry.getValue());
		}
		return edge;
		
	}

	@Override
	public Edge updateEdge(long edgeId, 
			String propertyKey, Object propertyValue) {
		
		Edge edge = g.E(edgeId).next();
		edge.property(propertyKey, propertyValue);	
		return edge;
		
	}

	@Override
	public Edge updateEdge(long edgeId, Map<String, Object> properties) {
		
		Edge edge = g.E(edgeId).next();
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			edge.property(entry.getKey(), entry.getValue());
		}
		return edge;
		
	}

	@Override
	public Edge deleteEdge(long edgeId) {
		
		Edge edge = g.E(edgeId).next();
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
