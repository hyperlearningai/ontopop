package ai.hyperlearning.ontopop.graph.aws.neptune;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;

/**
 * AWS Neptune Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AwsNeptuneGraphDatabaseService implements GraphDatabaseService {

	@Override
	public GraphTraversalSource openGraph() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeGraph() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteGraph() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serializeGraph(String filepath) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createSchema() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vertex addVertex(String label, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex updateVertex(long vertexId, String propertyKey, Object propertyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex updateVertex(long vertexId, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex deleteVertex(long vertexId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge addEdge(Vertex sourceVertex, Vertex targetVertex, String label, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge updateEdge(long edgeId, String propertyKey, Object propertyValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge updateEdge(long edgeId, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Edge deleteEdge(long edgeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> query(String gremlinQuery) throws ScriptException, InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

}
