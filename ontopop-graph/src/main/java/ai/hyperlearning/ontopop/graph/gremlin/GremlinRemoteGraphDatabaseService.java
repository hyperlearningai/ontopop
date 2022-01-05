package ai.hyperlearning.ontopop.graph.gremlin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.apache.tinkerpop.gremlin.driver.ResultSet;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphEdge;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphVertex;

/**
 * Gremlin Remote Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class GremlinRemoteGraphDatabaseService implements GraphDatabaseService {
	
	private static final String VERTEX_ID_PROPERTY_KEY = "vertexId";
	
	@Autowired
	@Qualifier("gremlinRemoteGraphClient")
	private Client client;
	
	@Value("${storage.graph.gremlin-remote-graph.supportsNonStringIds}")
	private boolean supportsNonStringIds;
	
	@Value("${storage.graph.gremlin-remote-graph.supportsTraversals.by}")
	private boolean supportsTraversalsBy;

	/**************************************************************************
	 * GRAPH INSTANCE MANAGEMENT
	 *************************************************************************/
	
	@Override
	public GraphTraversalSource openGraph() throws IOException {
		return null;
	}

	@Override
	public void closeGraph() throws Exception {
		client.getCluster().close();
	}

	@Override
	public void deleteGraph() {
		client.submit(GremlinRecipes.deleteVertices());
		client.submit(GremlinRecipes.deleteEdges());
	}

	@Override
	public void deleteSubGraph(String propertyKey, Object propertyValue) {
		client.submit(
				GremlinRecipes.deleteVertices(propertyKey, propertyValue));
		client.submit(
				GremlinRecipes.deleteEdges(propertyKey, propertyValue));
	}

	@Override
	public void commit() throws TransactionException {
		
	}

	@Override
	public void rollback() throws TransactionException {
		
	}

	@Override
	public void serializeGraph(String filepath) throws IOException {
		client.submit(GremlinRecipes.writeGraph(filepath, "gryo"));
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
	 * RESULT SET MANAGEMENT
	 *************************************************************************/
	
	/**
	 * Get a completed result list given a result set
	 * @param results
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	private List<Result> getResultList(ResultSet results) 
			throws InterruptedException, ExecutionException {
		CompletableFuture<List<Result>> completableFutureResults = 
				results.all();
		return completableFutureResults.get();
	}
	
	/**
	 * Get a completed result given a result set. Note that the result
	 * may be null.
	 * @param results
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	
	private Result getResult(ResultSet results) 
			throws InterruptedException, ExecutionException {
		CompletableFuture<List<Result>> completableFutureResults = 
				results.all();
		List<Result> resultList = completableFutureResults.get();
		return resultList.isEmpty() ? null : resultList.iterator().next();
	}
	
	/**************************************************************************
	 * VERTEX MANAGEMENT
	 *************************************************************************/

	@Override
	public List<Result> getVertices() 
			throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getVertices(supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public List<Result> getVertices(String label) 
			throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(GremlinRecipes.getVertices(
				label, supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public List<Result> getVertices(
			String label, String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getVertices(label, 
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public List<Result> getVertices(
			String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getVertices(
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public Result getVertex(long vertexId) 
			throws NoSuchElementException, InterruptedException, 
			ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getVertex(
						vertexId, supportsNonStringIds, supportsTraversalsBy));
		return getResult(results);
	}

	@Override
	public Result getVertex(
			String label, String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getVertices(label, 
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResult(results);
	}

	@Override
	public Result getVertex(String propertyKey, Object propertyValue) 
			throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getVertices(
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResult(results);
	}

	@Override
	public void addVertices(String label, Set<SimpleGraphVertex> vertices) 
			throws InterruptedException, ExecutionException {
		if ( !vertices.isEmpty() ) {
			for (SimpleGraphVertex vertex : vertices) {
				client.submit(GremlinRecipes.addVertex(
						label, 
						vertex.getVertexId(),
						vertex.getProperties(), 
						supportsNonStringIds))
					.all().get();
			}
		}
	}

	@Override
	public void addVertices(
			String label, List<Map<String, Object>> propertyMaps) 
					throws InterruptedException, ExecutionException {
		if ( !propertyMaps.isEmpty() ) {
			for ( Map<String, Object> properties : propertyMaps ) {
				long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
				client.submit(
						GremlinRecipes.addVertex(label, 
								vertexId, properties, supportsNonStringIds))
					.all().get();
			}
		}
	}

	@Override
	public Result addVertex(String label, Map<String, Object> properties) 
			throws NoSuchElementException, InterruptedException, 
			ExecutionException {
		long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
		client.submit(GremlinRecipes.addVertex(label, 
				vertexId, properties, supportsNonStringIds))
			.all().get();
		return getVertex(vertexId);
	}

	@Override
	public Result updateVertex(
			long vertexId, String propertyKey, Object propertyValue) 
					throws NoSuchElementException, InterruptedException, 
					ExecutionException {
		client.submit(GremlinRecipes.updateVertex(
				vertexId, propertyKey, propertyValue, supportsNonStringIds))
			.all().get();
		return getVertex(vertexId);
	}

	@Override
	public Result updateVertex(
			long vertexId, Map<String, Object> properties) 
					throws NoSuchElementException, InterruptedException, 
					ExecutionException {
		client.submit(GremlinRecipes.updateVertex(
				vertexId, properties, supportsNonStringIds))
			.all().get();
		return getVertex(vertexId);
	}

	@Override
	public List<Result> deleteVertex(long vertexId) 
			throws NoSuchElementException, InterruptedException, 
			ExecutionException {
		return client.submit(
				GremlinRecipes.deleteVertex(vertexId, supportsNonStringIds))
					.all().get();
	}

	@Override
	public void deleteVertices() 
			throws InterruptedException, ExecutionException {
		client.submit(GremlinRecipes.deleteVertices()).all().get();
	}

	@Override
	public void deleteVertices(String propertyKey, Object propertyValue) 
			throws InterruptedException, ExecutionException {
		client.submit(
				GremlinRecipes.deleteVertices(propertyKey, propertyValue))
			.all().get();
	}
	
	/**************************************************************************
	 * EDGE MANAGEMENT
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 *************************************************************************/

	@Override
	public List<Result> getEdges() 
			throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdges(supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public List<Result> getEdges(String label) 
			throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdges(label, supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public List<Result> getEdges(
			String label, String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdges(label, 
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public List<Result> getEdges(
			String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdges(
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResultList(results);
	}

	@Override
	public Result getEdge(long edgeId) 
			throws NoSuchElementException, InterruptedException, 
			ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdge(
						edgeId, supportsNonStringIds, supportsTraversalsBy));
		return getResult(results);
	}

	@Override
	public Result getEdge(
			String label, String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdges(label, 
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResult(results);
	}

	@Override
	public Result getEdge(
			String propertyKey, Object propertyValue) 
					throws InterruptedException, ExecutionException {
		ResultSet results = client.submit(
				GremlinRecipes.getEdges(
						propertyKey, propertyValue, supportsTraversalsBy));
		return getResult(results);
	}

	@Override
	public void addEdges(List<SimpleGraphEdge> edges) 
			throws InterruptedException, ExecutionException {
		if ( !edges.isEmpty() ) {
			for (SimpleGraphEdge edge : edges) {
				client.submit(GremlinRecipes.addEdge(
						edge.getSourceVertexId(), 
						edge.getTargetVertexId(), 
						edge.getLabel(), 
						edge.getProperties(), supportsNonStringIds))
					.all().get();
			}
		}
	}

	@Override
	public Result addEdge(
			Vertex sourceVertex, Vertex targetVertex, 
			String label, Map<String, Object> properties) 
					throws InterruptedException, ExecutionException {
		client.submit(GremlinRecipes.addEdge(
				(Long) sourceVertex.id(), 
				(Long) targetVertex.id(), 
				label, 
				properties, 
				supportsNonStringIds)).all().get();
		return null;
	}

	@Override
	public Result updateEdge(
			long edgeId, String propertyKey, Object propertyValue) 
					throws NoSuchElementException, InterruptedException, 
					ExecutionException {
		client.submit(
				GremlinRecipes.updateEdge(edgeId, 
						propertyKey, propertyValue, supportsNonStringIds))
			.all().get();
		return getEdge(edgeId);
	}

	@Override
	public Result updateEdge(long edgeId, Map<String, Object> properties) 
			throws NoSuchElementException, InterruptedException, 
			ExecutionException {
		client.submit(GremlinRecipes.updateEdge(
				edgeId, properties, supportsNonStringIds))
			.all().get();
		return getEdge(edgeId);
	}

	@Override
	public List<Result> deleteEdge(long edgeId) 
			throws NoSuchElementException, InterruptedException, 
			ExecutionException {
		return client.submit(GremlinRecipes.deleteEdge(
				edgeId, supportsNonStringIds)).all().get();
	}

	@Override
	public void deleteEdges() throws InterruptedException, ExecutionException {
		client.submit(GremlinRecipes.deleteEdges()).all().get();
	}

	@Override
	public void deleteEdges(String propertyKey, Object propertyValue) 
			throws InterruptedException, ExecutionException {
		client.submit(GremlinRecipes.deleteEdges(propertyKey, propertyValue))
			.all().get();
	}
	
	/**************************************************************************
	 * QUERY MANAGEMENT
	 *************************************************************************/

	@Override
	public List<Result> query(String gremlinQuery) 
			throws ScriptException, InterruptedException, ExecutionException {
		ResultSet results = client.submit(gremlinQuery);
		return getResultList(results);
	}

}
