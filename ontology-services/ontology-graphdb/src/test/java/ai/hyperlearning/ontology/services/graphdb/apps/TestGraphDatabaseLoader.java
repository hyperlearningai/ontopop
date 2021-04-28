package ai.hyperlearning.ontology.services.graphdb.apps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.graphdb.impl.tinkergraph.TinkerGraphDatabaseManager;
import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import ai.hyperlearning.ontology.services.owlapi.OWLAPIPipelines;

/**
 * Graph Database Loader Unit Tests
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class TestGraphDatabaseLoader {
	
	private static final String TEST_ONTOLOGY_FILENAME = "test-ontology.owl";
	private static final int TEST_ONTOLOGY_CLASS_COUNT = 196;
	private static GraphDatabaseManager graphDatabaseManager;
	private static ParsedRDFOwlOntology parsedRDFOwlOntology;
	private GraphDatabaseLoader graphDatabaseLoader;
	
	/**************************************************************************
	 * Graph Database Loader - Apache TinkerGraph
	 * @throws Exception 
	 *************************************************************************/
	
	@Test
	public void testGraphDatabaseLoaderTinkerGraph() 
			throws Exception {
		
		// Load TinkerGraph with the parsed test ontology
		String graphConfigurationFilename = "graphdb-tinkergraph.properties";
		graphDatabaseManager = new TinkerGraphDatabaseManager(
				graphConfigurationFilename);
		graphDatabaseManager.openGraph();
		graphDatabaseManager.createSchema();
		graphDatabaseManager.dropGraph();
		parsedRDFOwlOntology = OWLAPIPipelines
				.parseRdfOwlOntology(TEST_ONTOLOGY_FILENAME);
		graphDatabaseLoader = new GraphDatabaseLoader(parsedRDFOwlOntology);
		graphDatabaseLoader.loadParsedOntologyClassesAsVertices(
				graphDatabaseManager);
		graphDatabaseLoader.loadParsedOntologyClassesAsEdges(
				graphDatabaseManager);
		graphDatabaseLoader.clearLoadedVertexMap();
		
		// Count the number of vertices (i.e. ontological classes)
		// currently in TinkerGraph
		List<Object> queryResult = (List<Object>) graphDatabaseManager
				.query("g.V().hasLabel('class').count()");
		int vertexCount = (int) (long) queryResult.get(0);
		assertEquals(vertexCount, TEST_ONTOLOGY_CLASS_COUNT);
		graphDatabaseManager.closeGraph();
		
	}
	
}
