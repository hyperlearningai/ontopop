package ai.hyperlearning.ontopop.graph.gremlin.server.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.tinkerpop.gremlin.structure.util.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.gremlin.GremlinRecipes;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphEdge;
import ai.hyperlearning.ontopop.graph.model.SimpleGraphVertex;
import reactor.core.publisher.Mono;

/**
 * Gremlin Server HTTP Web Client Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnExpression("'${storage.graph.service}'.equals('gremlin-server-http') or "
        + "'${storage.graph.service}'.equals('janusgraph-server-http')")
public class GremlinServerHttpWebClientGraphDatabaseService 
        implements GraphDatabaseService {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(GremlinServerHttpWebClientGraphDatabaseService.class);
    
    protected static final String VERTEX_ID_PROPERTY_KEY = "vertexId";
    protected static final int RATE_LIMITER_WAIT_SECONDS = 1;
    
    @Autowired(required = false)
    @Qualifier("gremlinServerHttpWebClient")
    private WebClient gremlinServerHttpWebClient;
    
    @Value("${storage.graph.engine.supportsUserDefinedIds:true}")
    protected Boolean supportsUserDefinedIds;

    @Value("${storage.graph.engine.supportsNonStringIds:false}")
    protected Boolean supportsNonStringIds;

    @Value("${storage.graph.engine.supportsSchema:false}")
    protected Boolean supportsSchema;

    @Value("${storage.graph.engine.supportsTransactions:false}")
    protected Boolean supportsTransactions;

    @Value("${storage.graph.engine.supportsGeoshape:false}")
    protected Boolean supportsGeoshape;

    @Value("${storage.graph.engine.supportsTraversals.by:false}")
    protected Boolean supportsTraversalsBy;
    
    @Value("${storage.graph.gremlin-server.bulkExecutor.rateLimiter.enabled:false}")
    protected Boolean rateLimiterEnabled;
    
    @Value("${storage.graph.gremlin-server.bulkExecutor.rateLimiter.actionsPerSecond:100}")
    protected Integer rateLimiterActionsPerSecond;
    
    protected boolean iterate = true;
    protected WebClient client;
    
    /**************************************************************************
     * HTTP REQUEST MANAGEMENT
     *************************************************************************/
    
    /**
     * Execute a Gremlin query as a (blocking) HTTP POST request to the 
     * Gremlin server with the Gremlin query contained in the JSON 
     * request body. 
     * @param gremlinQuery
     * @return
     */
    
    protected ResponseEntity<String> sendBlockingRequest(
            String gremlinQuery) {
        
        // Create the request body
        GremlinServerHttpWebClientRequestBodyModel body = 
                new GremlinServerHttpWebClientRequestBodyModel(gremlinQuery);
        
        // Create and send the HTTP POST request to the Gremlin server
        return client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), 
                        GremlinServerHttpWebClientRequestBodyModel.class)
                .retrieve()
                .onStatus(status -> status.value() != HttpStatus.OK.value(),
                        clientResponse -> Mono.empty())
                .toEntity(String.class)
                .block();
        
    }
    
    /**
     * Execute a Gremlin query as a (non-blocking) HTTP POST request to the 
     * Gremlin server with the Gremlin query contained in the JSON 
     * request body. 
     * @param gremlinQuery
     * @return
     */
    
    protected Mono<ResponseEntity<String>> sendNonBlockingRequest(
            String gremlinQuery) {
        
        // Create the request body
        GremlinServerHttpWebClientRequestBodyModel body = 
                new GremlinServerHttpWebClientRequestBodyModel(gremlinQuery);
        
        // Create and send the HTTP POST request to the Gremlin server
        return client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), 
                        GremlinServerHttpWebClientRequestBodyModel.class)
                .retrieve()
                .onStatus(status -> status.value() != HttpStatus.OK.value(),
                        clientResponse -> Mono.empty())
                .toEntity(String.class);
        
    }
    
    /**************************************************************************
     * GRAPH INSTANCE MANAGEMENT
     *************************************************************************/

    @Override
    public Object openGraph() throws IOException {
        this.client = gremlinServerHttpWebClient;
        return client;
    }

    @Override
    public void closeGraph() throws Exception {
        
    }

    @Override
    public void deleteGraph() {
        sendBlockingRequest(GremlinRecipes.deleteVertices(iterate));
        sendBlockingRequest(GremlinRecipes.deleteEdges(iterate));
    }

    @Override
    public void deleteSubGraph(String propertyKey, Object propertyValue) {
        sendBlockingRequest(GremlinRecipes.deleteVertices(
                propertyKey, propertyValue, iterate));
        sendBlockingRequest(GremlinRecipes.deleteEdges(
                propertyKey, propertyValue, iterate));
    }

    @Override
    public void commit() throws TransactionException {
        
    }

    @Override
    public void rollback() throws TransactionException {
        
    }

    @Override
    public void serializeGraph(String filepath) throws IOException {
        sendBlockingRequest(GremlinRecipes.writeGraph(filepath, "gryo"));
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
    public ResponseEntity<String> getVertices() {
        String query = GremlinRecipes.getVertices(supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getVertices(String label) {
        String query = GremlinRecipes.getVertices(label, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getVertices(String label, String propertyKey,
            Object propertyValue) {
        String query = GremlinRecipes.getVertices(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getVertices(
            String propertyKey, Object propertyValue) {
        String query = GremlinRecipes.getVertices(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertices: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getVertex(long vertexId) {
        String query = GremlinRecipes.getVertex(vertexId, supportsNonStringIds,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertex: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getVertex(String label, String propertyKey,
            Object propertyValue) {
        String query = GremlinRecipes.getVertices(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertex: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getVertex(
            String propertyKey, Object propertyValue) {
        String query = GremlinRecipes.getVertices(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Vertex: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public void addVertices(String label, Set<SimpleGraphVertex> vertices) 
            throws InterruptedException {
        
        if (!vertices.isEmpty()) {
            
            if ( rateLimiterEnabled ) {
                
                // Partition the set of vertices
                Iterable<List<SimpleGraphVertex>> verticesSubLists =
                    Iterables.partition(vertices, rateLimiterActionsPerSecond);
                for (List<SimpleGraphVertex> verticesSubList : verticesSubLists) {
                    
                    // Blocking requests to add vertices
                    for (SimpleGraphVertex vertex : verticesSubList) {
                        String query = GremlinRecipes.addVertex(label,
                                vertex.getVertexId(), vertex.getProperties(),
                                supportsNonStringIds, supportsUserDefinedIds, iterate);
                        LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
                        sendBlockingRequest(query);
                    }
                    TimeUnit.SECONDS.sleep(RATE_LIMITER_WAIT_SECONDS);
                    
                }
                
            } else {
                
                // Blocking requests to add vertices
                for (SimpleGraphVertex vertex : vertices) {
                    String query = GremlinRecipes.addVertex(label,
                            vertex.getVertexId(), vertex.getProperties(),
                            supportsNonStringIds, supportsUserDefinedIds, iterate);
                    LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
                    sendBlockingRequest(query);
                }
                
            }
            
        }
        
    }

    @Override
    public void addVertices(String label,
            List<Map<String, Object>> propertyMaps) throws InterruptedException {
        
        if (!propertyMaps.isEmpty()) {
            
            if ( rateLimiterEnabled ) {
                
                // Partition the list of property maps
                List<List<Map<String, Object>>> propertyMapsSubLists =
                    Lists.partition(propertyMaps, rateLimiterActionsPerSecond);
                for (List<Map<String, Object>> propertyMapsSubList : propertyMapsSubLists) {
                    
                    // Blocking requests to add vertices
                    for (Map<String, Object> properties : propertyMapsSubList) {
                        long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
                        String query = GremlinRecipes.addVertex(label, 
                                vertexId, properties,
                                supportsNonStringIds, supportsUserDefinedIds, iterate);
                        LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
                        sendBlockingRequest(query);
                    }
                    TimeUnit.SECONDS.sleep(RATE_LIMITER_WAIT_SECONDS);
                    
                }
                
            } else {
                
                // Blocking requests to add vertices
                for (Map<String, Object> properties : propertyMaps) {
                    long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
                    String query = GremlinRecipes.addVertex(label, 
                            vertexId, properties,
                            supportsNonStringIds, supportsUserDefinedIds, iterate);
                    LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
                    sendBlockingRequest(query);
                }
                
            }
            
        }
        
    }

    @Override
    public ResponseEntity<String> addVertex(
            String label, Map<String, Object> properties) {
        long vertexId = (Long) properties.get(VERTEX_ID_PROPERTY_KEY);
        String query = GremlinRecipes.addVertex(label, vertexId, properties,
                supportsNonStringIds, supportsUserDefinedIds, iterate);
        LOGGER.debug("Gremlin Query - Add Vertex: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> updateVertex(
            long vertexId, String propertyKey, Object propertyValue)  {
        String query = GremlinRecipes.updateVertex(vertexId, propertyKey,
                propertyValue, supportsNonStringIds, iterate);
        LOGGER.debug("Gremlin Query - Update Vertex: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> updateVertex(
            long vertexId, Map<String, Object> properties) {
        String query = GremlinRecipes.updateVertex(vertexId, properties,
                supportsNonStringIds, iterate);
        LOGGER.debug("Gremlin Query - Update Vertex: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public void deleteVertex(long vertexId) {
        String query = GremlinRecipes.deleteVertex(
                vertexId, supportsNonStringIds, iterate);
        LOGGER.debug("Gremlin Query - Delete Vertex: {}", query);
        sendBlockingRequest(query);
    }

    @Override
    public void deleteVertices() {
        String query = GremlinRecipes.deleteVertices(iterate);
        LOGGER.debug("Gremlin Query - Delete Vertices: {}", query);
        sendBlockingRequest(query);
    }

    @Override
    public void deleteVertices(String propertyKey, Object propertyValue) {
        String query = GremlinRecipes.deleteVertices(
                propertyKey, propertyValue, iterate);
        LOGGER.debug("Gremlin Query - Delete Vertices: {}", query);
        sendBlockingRequest(query);
    }
    
    /**************************************************************************
     * EDGE MANAGEMENT
     *************************************************************************/

    @Override
    public ResponseEntity<String> getEdges() {
        String query = GremlinRecipes.getEdges(supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getEdges(String label) {
        String query = GremlinRecipes.getEdges(label, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getEdges(String label, String propertyKey,
            Object propertyValue) {
        String query = GremlinRecipes.getEdges(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getEdges(
            String propertyKey, Object propertyValue) {
        String query = GremlinRecipes.getEdges(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edges: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getEdge(long edgeId) {
        String query = GremlinRecipes.getEdge(edgeId, supportsNonStringIds,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edge: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getEdge(String label, String propertyKey,
            Object propertyValue) {
        String query = GremlinRecipes.getEdges(label, propertyKey,
                propertyValue, supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edge: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> getEdge(
            String propertyKey, Object propertyValue) {
        String query = GremlinRecipes.getEdges(propertyKey, propertyValue,
                supportsTraversalsBy);
        LOGGER.debug("Gremlin Query - Get Edge: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public void addEdges(List<SimpleGraphEdge> edges) 
            throws InterruptedException {
        
        if (!edges.isEmpty()) {
            
            if ( rateLimiterEnabled ) {
                
                // Partition the list of edges
                Iterable<List<SimpleGraphEdge>> edgesSubLists =
                    Iterables.partition(edges, rateLimiterActionsPerSecond);
                for (List<SimpleGraphEdge> edgesSubList : edgesSubLists) {
                    
                    // Blocking requests to add edges
                    for (SimpleGraphEdge edge : edgesSubList) {
                        String query = GremlinRecipes.addEdge(edge.getSourceVertexId(),
                                edge.getTargetVertexId(), edge.getLabel(),
                                edge.getProperties(), supportsNonStringIds, 
                                supportsUserDefinedIds, iterate);
                        LOGGER.debug("Gremlin Query - Add Edge: {}", query);
                        sendBlockingRequest(query);
                    }
                    TimeUnit.SECONDS.sleep(RATE_LIMITER_WAIT_SECONDS);
                    
                }
                
            } else {
                
                // Blocking requests to add edges
                for (SimpleGraphEdge edge : edges) {
                    String query = GremlinRecipes.addEdge(edge.getSourceVertexId(),
                            edge.getTargetVertexId(), edge.getLabel(),
                            edge.getProperties(), supportsNonStringIds, 
                            supportsUserDefinedIds, iterate);
                    LOGGER.debug("Gremlin Query - Add Edge: {}", query);
                    sendBlockingRequest(query);
                }
                
            }
            
        }
        
    }

    @Override
    public ResponseEntity<String> addEdge(
            long sourceVertexId, long targetVertexId,
            String label, Map<String, Object> properties) {
        String query = GremlinRecipes.addEdge(sourceVertexId,
                targetVertexId, label, properties,
                supportsNonStringIds, supportsUserDefinedIds, iterate);
        LOGGER.debug("Gremlin Query - Add Edge: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> updateEdge(
            long edgeId, String propertyKey, Object propertyValue) {
        String query = GremlinRecipes.updateEdge(edgeId, propertyKey,
                propertyValue, supportsNonStringIds, iterate);
        LOGGER.debug("Gremlin Query - Update Edge: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public ResponseEntity<String> updateEdge(
            long edgeId, Map<String, Object> properties) {
        String query = GremlinRecipes.updateEdge(edgeId, properties,
                supportsNonStringIds, iterate);
        LOGGER.debug("Gremlin Query - Update Edge: {}", query);
        return sendBlockingRequest(query);
    }

    @Override
    public void deleteEdge(long edgeId) {
        String query = GremlinRecipes.deleteEdge(
                edgeId, supportsNonStringIds, iterate);
        LOGGER.debug("Gremlin Query - Delete Edge: {}", query);
        sendBlockingRequest(query);
    }

    @Override
    public void deleteEdges() {
        String query = GremlinRecipes.deleteEdges(iterate);
        LOGGER.debug("Gremlin Query - Delete Edges: {}", query);
        sendBlockingRequest(query);
    }

    @Override
    public void deleteEdges(String propertyKey, Object propertyValue) {
        String query = GremlinRecipes
                .deleteEdges(propertyKey, propertyValue, iterate);
        LOGGER.debug("Gremlin Query - Delete Edges: {}", query);
        sendBlockingRequest(query);
    }
    
    /**************************************************************************
     * CUSTOM QUERY MANAGEMENT
     *************************************************************************/

    @Override
    public ResponseEntity<String> query(String gremlinQuery) {
        return sendBlockingRequest(gremlinQuery);
    }

}
