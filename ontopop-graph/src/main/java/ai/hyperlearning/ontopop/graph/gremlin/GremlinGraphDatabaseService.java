package ai.hyperlearning.ontopop.graph.gremlin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
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
import org.apache.tinkerpop.gremlin.structure.util.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphEdge;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphVertex;

/**
 * Gremlin Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnExpression(
        "'${storage.graph.service}'.equals('gremlin-graph') or "
        + "'${storage.graph.service}'.equals('gremlin-server-remote-connection') or "
        + "'${storage.graph.service}'.equals('janusgraph') or "
        + "'${storage.graph.service}'.equals('tinkergraph')")
public class GremlinGraphDatabaseService implements GraphDatabaseService {

	private static final String VERTEX_ID_PROPERTY_KEY = "vertexId";
	private static final String EDGE_ID_PROPERTY_KEY = "edgeId";
	private static final int BULK_LOAD_PARTITION_SIZE = 100;
	private static final String PROPERTY_KEY_ONTOLOGY_LABEL = 
			"label";
	private static final String PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT = 
			"rdfsLabel";
	
	@Autowired(required=false)
	@Qualifier("gremlinGraphTraversalSource")
	private GraphTraversalSource gremlinGraphTraversalSource;
	
	protected GraphTraversalSource g;
	protected GremlinExecutor gremlinExecutor;
	protected ConcurrentBindings bindings = new ConcurrentBindings();
	
	@Value("${storage.graph.engine.supportsUserDefinedIds}")
	protected boolean supportsUserDefinedIds;
	
	@Value("${storage.graph.engine.supportsNonStringIds}")
	protected boolean supportsNonStringIds;
	
	@Value("${storage.graph.engine.supportsSchema}")
	protected boolean supportsSchema;
	
	@Value("${storage.graph.engine.supportsTransactions}")
	protected boolean supportsTransactions;
	
	@Value("${storage.graph.engine.supportsGeoshape}")
	protected boolean supportsGeoshape;
	
	@Value("${storage.graph.engine.supportsTraversals.by}")
	protected boolean supportsTraversalsBy;
	
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
	public void deleteSubGraph(String propertyKey, Object propertyValue) {
		g.V().has(propertyKey, propertyValue).drop().iterate();
		g.E().has(propertyKey, propertyValue).drop().iterate();
	}

	@Override
	public void commit() throws TransactionException {
		if ( supportsTransactions )
			g.tx().commit();
	}

	@Override
	public void rollback() throws TransactionException {
		if ( supportsTransactions )
			g.tx().rollback();
	}

	@Override
	public void serializeGraph(String filepath) throws IOException {
		g.io(filepath).with(IO.writer, IO.gryo).write().iterate();
	}
	
	@Override
	public void cleanup() throws Exception {
		closeGraph();
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
	public GraphTraversal<Vertex, Vertex> getVertices() {
		return g.V();
	}
	
	@Override
	public GraphTraversal<Vertex, Vertex> getVertices(String label) {
		return g.V().hasLabel(label);
	}
	
	@Override
	public GraphTraversal<Vertex, Vertex> getVertices(
			String label, String propertyKey, Object propertyValue) {
		return g.V().has(label, propertyKey, propertyValue);
	}
	
	@Override
	public GraphTraversal<Vertex, Vertex> getVertices(
			String propertyKey, Object propertyValue) {
		return g.V().has(propertyKey, propertyValue);
	}
	
	@Override
	public Vertex getVertex(long vertexId) throws NoSuchElementException {
		return g.V(vertexId).next();
	}
	
	@Override
	public Vertex getVertex(
			String label, String propertyKey, Object propertyValue) {
		return g.V().has(label, propertyKey, propertyValue).next();
	}
	
	@Override
	public Vertex getVertex(
			String propertyKey, Object propertyValue) {
		return g.V().has(propertyKey, propertyValue).next();
	}
	
	@Override
	public void addVertices(String label, Set<SimpleGraphVertex> vertices) {
		
		if ( !vertices.isEmpty() ) {
			
			// Partition the set of vertices
			Iterable<List<SimpleGraphVertex>> verticesSubLists = 
					Iterables.partition(vertices, BULK_LOAD_PARTITION_SIZE);
			for (List<SimpleGraphVertex> verticesSubList : verticesSubLists) {
				
				// Bulk load the vertices in this vertices sublist
				GraphTraversal<Vertex, Vertex> graphTraversal = g.addV(label);
				int counter = 0;
				for (SimpleGraphVertex vertex : verticesSubList) {
					if ( counter > 0 )
						graphTraversal.addV(label);
					if ( supportsUserDefinedIds )
					    graphTraversal.property(T.id, vertex.getVertexId());
					for (Map.Entry<String, Object> entry : vertex.getProperties().entrySet()) {
						String key = entry.getKey()
								.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
								PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : 
									entry.getKey();
						graphTraversal.property(key, entry.getValue());
					}
					counter++;
				}
				graphTraversal.iterate();
				
			}
			
		}
		
	}
	
	@Override
	public void addVertices(String label, 
	        List<Map<String, Object>> propertyMaps) {
		
		if ( !propertyMaps.isEmpty() ) {
		
			// Partition the list of property maps
			List<List<Map<String, Object>>> propertyMapsSubLists = 
					Lists.partition(propertyMaps, BULK_LOAD_PARTITION_SIZE);
			for (List<Map<String, Object>> propertyMapsSubList : propertyMapsSubLists) {
				
				// Bulk load the vertices in this property map sublist
				GraphTraversal<Vertex, Vertex> graphTraversal = g.addV(label);
				int counter = 0;
				for (Map<String, Object> vertexPropertyMap : propertyMapsSubList) {
					if ( counter > 0 )
						graphTraversal.addV(label);
					for (Map.Entry<String, Object> entry : vertexPropertyMap.entrySet()) {
						String key = entry.getKey()
								.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
								PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : 
									entry.getKey();
						graphTraversal.property(key, entry.getValue());
						if ( entry.getKey().equalsIgnoreCase(VERTEX_ID_PROPERTY_KEY) 
						        && supportsUserDefinedIds )
							graphTraversal.property(T.id, entry.getValue());
					}
					counter++;
				}
				graphTraversal.iterate();
				
			}
			
		}
		
	}
	
	@Override
	public Vertex addVertex(String label, Map<String, Object> properties) {
		
		GraphTraversal<Vertex, Vertex> graphTraversal = g.addV(label);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : 
						entry.getKey();
			graphTraversal.property(key, entry.getValue());
			if ( entry.getKey().equalsIgnoreCase(VERTEX_ID_PROPERTY_KEY) 
			        && supportsUserDefinedIds)
				graphTraversal.property(T.id, entry.getValue());
		}
		final Vertex vertex = graphTraversal.next();
		return vertex;
		
	}

	@Override
	public Vertex updateVertex(long vertexId, 
			String propertyKey, Object propertyValue) 
					throws NoSuchElementException {
		
		String key = propertyKey.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
				PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : propertyKey;
		GraphTraversal<Vertex, Vertex> vertex = 
		        g.V(vertexId).property(key, propertyValue);
		return vertex.next();
		
	}

	@Override
	public Vertex updateVertex(long vertexId, Map<String, Object> properties) 
			throws NoSuchElementException {
		
	    GraphTraversal<Vertex, Vertex> vertex = g.V(vertexId);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : 
						entry.getKey();
			vertex.property(key, entry.getValue());
		}
		return vertex.next();
		
	}

	@Override
	public void deleteVertex(long vertexId) throws NoSuchElementException {
		g.V(vertexId).remove();
	}
	
	@Override
	public void deleteVertices() {
		g.V().drop().iterate();
	}
	
	@Override
	public void deleteVertices(String propertyKey, Object propertyValue) {
		g.V().has(propertyKey, propertyValue).drop().iterate();
	}
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 *************************************************************************/

	@Override
	public GraphTraversal<Edge, Edge> getEdges() {
		return g.E();
	}
	
	@Override
	public GraphTraversal<Edge, Edge> getEdges(String label) {
		return g.E().hasLabel(label);
	}
	
	@Override
	public GraphTraversal<Edge, Edge> getEdges(
			String label, String propertyKey, Object propertyValue) {
		return g.E().has(label, propertyKey, propertyValue);
	}
	
	@Override
	public GraphTraversal<Edge, Edge> getEdges(
			String propertyKey, Object propertyValue) {
		return g.E().has(propertyKey, propertyValue);
	}
	
	@Override
	public Edge getEdge(long edgeId) throws NoSuchElementException {
		return g.E(edgeId).next();
	}
	
	@Override
	public Edge getEdge(
			String label, String propertyKey, Object propertyValue) {
		return g.E().has(label, propertyKey, propertyValue).next();
	}
	
	@Override
	public Edge getEdge(
			String propertyKey, Object propertyValue) {
		return g.E().has(propertyKey, propertyValue).next();
	}
	
	@Override
	public void addEdges(List<SimpleGraphEdge> edges) {
		if ( !edges.isEmpty() ) {
			for (SimpleGraphEdge edge : edges) {
				addEdge(edge.getSourceVertex(), 
						edge.getTargetVertex(), 
						edge.getLabel(), 
						edge.getProperties());
			}
		}
	}
	
	@Override
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, 
			String label, Map<String, Object> properties) {
		
	    GraphTraversal<Vertex,Edge> edge = g.V(sourceVertex.id()).as("a")
	            .V(targetVertex.id())
	            .addE(label)
	            .from("a");
		if ( properties.containsKey(EDGE_ID_PROPERTY_KEY) 
		        && supportsUserDefinedIds ) {
			long edgeId = (long) properties.get(EDGE_ID_PROPERTY_KEY);
			edge.property(T.id, edgeId);
		}
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : 
						entry.getKey();
			edge.property(key, entry.getValue());
		}
		return edge.next();
		
	}

	@Override
	public Edge updateEdge(long edgeId, 
			String propertyKey, Object propertyValue) 
					throws NoSuchElementException {
	    
		String key = propertyKey.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
				PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : propertyKey;
		GraphTraversal<Edge,Edge> edge = g.E(edgeId)
		        .property(key, propertyValue);
		edge.property(key, propertyValue);	
		return edge.next();
		
	}

	@Override
	public Edge updateEdge(long edgeId, Map<String, Object> properties) 
			throws NoSuchElementException {
		
	    GraphTraversal<Edge,Edge> edge = g.E(edgeId);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String key = entry.getKey()
					.equalsIgnoreCase(PROPERTY_KEY_ONTOLOGY_LABEL) ? 
					PROPERTY_KEY_ONTOLOGY_LABEL_REPLACEMENT : 
						entry.getKey();
			edge.property(key, entry.getValue());
		}
		return edge.next();
		
	}

	@Override
	public void deleteEdge(long edgeId) throws NoSuchElementException {
		g.E(edgeId).remove();
		
	}
	
	@Override
	public void deleteEdges() {
		g.E().drop().iterate();
	}
	
	@Override
	public void deleteEdges(String propertyKey, Object propertyValue) {
		g.E().has(propertyKey, propertyValue).drop().iterate();
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
