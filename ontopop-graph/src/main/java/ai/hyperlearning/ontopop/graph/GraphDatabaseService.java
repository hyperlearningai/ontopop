package ai.hyperlearning.ontopop.graph;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

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

    public Object openGraph() throws IOException;

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

    public Object getVertices() throws InterruptedException, ExecutionException;

    public Object getVertices(String label)
            throws InterruptedException, ExecutionException;

    public Object getVertices(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException;

    public Object getVertices(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException;

    public Object getVertex(long vertexId) throws NoSuchElementException,
            InterruptedException, ExecutionException;

    public Object getVertex(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException;

    public Object getVertex(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException;

    public void addVertices(String label, Set<SimpleGraphVertex> vertices)
            throws InterruptedException, ExecutionException;

    public void addVertices(String label,
            List<Map<String, Object>> propertyMaps)
            throws InterruptedException, ExecutionException;

    public Object addVertex(String label, Map<String, Object> properties)
            throws InterruptedException, ExecutionException;

    public Object updateVertex(long vertexId, String propertyKey,
            Object propertyValue) throws NoSuchElementException,
            InterruptedException, ExecutionException;

    public Object updateVertex(long vertexId, Map<String, Object> properties)
            throws NoSuchElementException, InterruptedException,
            ExecutionException;

    public void deleteVertex(long vertexId) throws NoSuchElementException,
            InterruptedException, ExecutionException;

    public void deleteVertices()
            throws InterruptedException, ExecutionException;

    public void deleteVertices(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException;

    /**************************************************************************
     * EDGE MANAGEMENT
     *************************************************************************/

    public Object getEdges() throws InterruptedException, ExecutionException;

    public Object getEdges(String label)
            throws InterruptedException, ExecutionException;

    public Object getEdges(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException;

    public Object getEdges(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException;

    public Object getEdge(long edgeId) throws NoSuchElementException,
            InterruptedException, ExecutionException;

    public Object getEdge(String label, String propertyKey,
            Object propertyValue)
            throws InterruptedException, ExecutionException;

    public Object getEdge(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException;

    public void addEdges(List<SimpleGraphEdge> edges)
            throws InterruptedException, ExecutionException;

    public Object addEdge(Vertex sourceVertex, Vertex targetVertex,
            String label, Map<String, Object> properties)
            throws InterruptedException, ExecutionException;

    public Object updateEdge(long edgeId, String propertyKey,
            Object propertyValue) throws NoSuchElementException,
            InterruptedException, ExecutionException;

    public Object updateEdge(long edgeId, Map<String, Object> properties)
            throws NoSuchElementException, InterruptedException,
            ExecutionException;

    public void deleteEdge(long edgeId) throws NoSuchElementException,
            InterruptedException, ExecutionException;

    public void deleteEdges() throws InterruptedException, ExecutionException;

    public void deleteEdges(String propertyKey, Object propertyValue)
            throws InterruptedException, ExecutionException;

    /**************************************************************************
     * QUERY MANAGEMENT
     *************************************************************************/

    public Object query(String gremlinQuery)
            throws ScriptException, InterruptedException, ExecutionException;

}
