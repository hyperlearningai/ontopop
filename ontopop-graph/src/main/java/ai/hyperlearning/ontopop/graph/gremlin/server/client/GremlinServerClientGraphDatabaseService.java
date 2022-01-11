package ai.hyperlearning.ontopop.graph.gremlin.server.client;

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
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.GremlinRecipes;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphEdge;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphVertex;

/**
 * Gremlin Server Client Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnExpression("'${storage.graph.service}'.equals('gremlin-server-client') or "
        + "'${storage.graph.service}'.equals('azure-cosmosdb')")
public class GremlinServerClientGraphDatabaseService
        implements GraphDatabaseService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GremlinServerClientGraphDatabaseService.class);

    private static final String VERTEX_ID_PROPERTY_KEY = "vertexId";

    @Autowired(required = false)
    @Qualifier("gremlinServerClient")
    private Client gremlinServerClient;

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

    protected Client client;

    /**************************************************************************
     * GRAPH INSTANCE MANAGEMENT
     *************************************************************************/

    @Override
    public Client openGraph() throws IOException {
        this.client = gremlinServerClient;
        return client;
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
        client.submit(GremlinRecipes.deleteEdges(propertyKey, propertyValue));
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
     * 
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
     * Get a completed result given a result set. Note that the result may be
     * null.
     * 
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
        String query = GremlinRecipes.getVertices(supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public List<Result> getVertices(String label)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getVertices(label, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public List<Result> getVertices(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getVertices(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public List<Result> getVertices(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getVertices(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public Result getVertex(long vertexId) throws NoSuchElementException,
            InterruptedException, ExecutionException {
        String query = GremlinRecipes.getVertex(vertexId, supportsNonStringIds,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertex: {}", query);
        ResultSet results = client.submit(query);
        return getResult(results);
    }

    @Override
    public Result getVertex(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getVertices(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertex: {}", query);
        ResultSet results = client.submit(query);
        return getResult(results);
    }

    @Override
    public Result getVertex(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getVertices(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertex: {}", query);
        ResultSet results = client.submit(query);
        return getResult(results);
    }

    @Override
    public void addVertices(String label, Set<SimpleGraphVertex> vertices)
            throws InterruptedException, ExecutionException {
        if (!vertices.isEmpty()) {
            for (SimpleGraphVertex vertex : vertices) {
                String query = GremlinRecipes.addVertex(label,
                        vertex.getVertexId(), vertex.getProperties(),
                        supportsNonStringIds, supportsUserDefinedIds);
                LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
                client.submit(query).all().get();
            }
        }
    }

    @Override
    public void addVertices(String label,
            List<Map<String, Object>> propertyMaps)
            throws InterruptedException, ExecutionException {
        if (!propertyMaps.isEmpty()) {
            for (Map<String, Object> properties : propertyMaps) {
                long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
                String query =
                        GremlinRecipes.addVertex(label, vertexId, properties,
                                supportsNonStringIds, supportsUserDefinedIds);
                LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
                client.submit(query).all().get();
            }
        }
    }

    @Override
    public Result addVertex(String label, Map<String, Object> properties)
            throws NoSuchElementException, InterruptedException,
            ExecutionException {
        long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
        String query = GremlinRecipes.addVertex(label, vertexId, properties,
                supportsNonStringIds, supportsUserDefinedIds);
        LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
        client.submit(query).all().get();
        return getVertex(vertexId);
    }

    @Override
    public Result updateVertex(long vertexId, String propertyKey,
            Object propertyValue) throws NoSuchElementException,
            InterruptedException, ExecutionException {
        String query = GremlinRecipes.updateVertex(vertexId, propertyKey,
                propertyValue, supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Update Vertex: {}", query);
        client.submit(query).all().get();
        return getVertex(vertexId);
    }

    @Override
    public Result updateVertex(long vertexId, Map<String, Object> properties)
            throws NoSuchElementException, InterruptedException,
            ExecutionException {
        String query = GremlinRecipes.updateVertex(vertexId, properties,
                supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Update Vertex: {}", query);
        client.submit(query).all().get();
        return getVertex(vertexId);
    }

    @Override
    public void deleteVertex(long vertexId) throws NoSuchElementException,
            InterruptedException, ExecutionException {
        String query =
                GremlinRecipes.deleteVertex(vertexId, supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Delete Vertex: {}", query);
        client.submit(query).all().get();
    }

    @Override
    public void deleteVertices()
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.deleteVertices();
        LOGGER.debug("Gremlin Query - Delete Vertices: {}", query);
        client.submit(query).all().get();
    }

    @Override
    public void deleteVertices(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query =
                GremlinRecipes.deleteVertices(propertyKey, propertyValue);
        LOGGER.debug("Gremlin Query - Delete Vertices: {}", query);
        client.submit(query).all().get();
    }

    /**************************************************************************
     * EDGE MANAGEMENT
     * 
     * @throws ExecutionException
     * @throws InterruptedException
     *************************************************************************/

    @Override
    public List<Result> getEdges()
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdges(supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public List<Result> getEdges(String label)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdges(label, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public List<Result> getEdges(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdges(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public List<Result> getEdges(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdges(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        ResultSet results = client.submit(query);
        return getResultList(results);
    }

    @Override
    public Result getEdge(long edgeId) throws NoSuchElementException,
            InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdge(edgeId, supportsNonStringIds,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edge: {}", query);
        ResultSet results = client.submit(query);
        return getResult(results);
    }

    @Override
    public Result getEdge(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdges(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edge: {}", query);
        ResultSet results = client.submit(query);
        return getResult(results);
    }

    @Override
    public Result getEdge(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.getEdges(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edge: {}", query);
        ResultSet results = client.submit(query);
        return getResult(results);
    }

    @Override
    public void addEdges(List<SimpleGraphEdge> edges)
            throws InterruptedException, ExecutionException {
        if (!edges.isEmpty()) {
            for (SimpleGraphEdge edge : edges) {
                String query = GremlinRecipes.addEdge(edge.getSourceVertexId(),
                        edge.getTargetVertexId(), edge.getLabel(),
                        edge.getProperties(), supportsNonStringIds);
                LOGGER.debug("Gremlin Query - Add Edge: {}", query);
                client.submit(query).all().get();
            }
        }
    }

    @Override
    public Result addEdge(Vertex sourceVertex, Vertex targetVertex,
            String label, Map<String, Object> properties)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.addEdge((Long) sourceVertex.id(),
                (Long) targetVertex.id(), label, properties,
                supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Add Edge: {}", query);
        client.submit(query).all().get();
        return null;
    }

    @Override
    public Result updateEdge(long edgeId, String propertyKey,
            Object propertyValue) throws NoSuchElementException,
            InterruptedException, ExecutionException {
        String query = GremlinRecipes.updateEdge(edgeId, propertyKey,
                propertyValue, supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Update Edge: {}", query);
        client.submit(query).all().get();
        return getEdge(edgeId);
    }

    @Override
    public Result updateEdge(long edgeId, Map<String, Object> properties)
            throws NoSuchElementException, InterruptedException,
            ExecutionException {
        String query = GremlinRecipes.updateEdge(edgeId, properties,
                supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Update Edge: {}", query);
        client.submit(query).all().get();
        return getEdge(edgeId);
    }

    @Override
    public void deleteEdge(long edgeId) throws NoSuchElementException,
            InterruptedException, ExecutionException {
        String query = GremlinRecipes.deleteEdge(edgeId, supportsNonStringIds);
        LOGGER.debug("Gremlin Query - Delete Edge: {}", query);
        client.submit(query).all().get();
    }

    @Override
    public void deleteEdges() throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.deleteEdges();
        LOGGER.debug("Gremlin Query - Delete Edges: {}", query);
        client.submit(query).all().get();
    }

    @Override
    public void deleteEdges(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException {
        String query = GremlinRecipes.deleteEdges(propertyKey, propertyValue);
        LOGGER.debug("Gremlin Query - Delete Edges: {}", query);
        client.submit(query).all().get();
    }

    /**************************************************************************
     * CUSTOM QUERY MANAGEMENT
     *************************************************************************/

    @Override
    public List<Result> query(String gremlinQuery)
            throws ScriptException, InterruptedException, ExecutionException {
        LOGGER.debug("Gremlin Query - Custom Query: {}", gremlinQuery);
        ResultSet results = client.submit(gremlinQuery);
        return getResultList(results);
    }

}
