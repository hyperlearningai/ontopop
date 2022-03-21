package ai.hyperlearning.ontopop.triplestore.jena;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ai.hyperlearning.ontopop.triplestore.TestTriplestoreApp;

/**
 * Integration Tests - Apache Jena Triplestore Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestTriplestoreApp.class)
@ActiveProfiles("integration-test-triplestore-jena")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class ITJenaTriplestoreService {
    
    private static final int NON_EXISTENT_REPOSITORY_ID = 99;
    private static final int NEW_REPOSITORY_ID = 0;
    private static final String TEST_ONTOLOGY_FILENAME = "test-ontology.owl";
    private static final int TEST_ONTOLOGY_TRIPLES_COUNT = 3772;
    private static final String SPARQL_QUERY_COUNT_TRIPLES = 
            "SELECT (COUNT(*) as ?Triples) WHERE { ?s ?p ?o}";
    private static final String SPARQL_QUERY_SELECT_TRIPLES = 
            "SELECT ?subject ?predicate ?object WHERE {"
            + "<http://webprotege.stanford.edu/RkKVruwOD8lCCdsbyX0lwY> "
            + "?predicate ?object"
            + "}";
    private static final String SPARQL_QUERY_RESULTS_KEY = "results";
    private static final String SPARQL_QUERY_BINDINGS_KEY = "bindings";
    private static final int SPARQL_QUERY_SELECT_TRIPLES_BINDINGS_SIZE = 4;
    private static final String GET_DATA_GRAPH_KEY = "@graph";
    private static final int GET_DATA_GRAPH_SIZE = 793;
    private static final String FUSEKI_SPARQL_QUERY_DEFAULT_ACCEPT_HEADER = 
            "application/sparql-results+json";
    private static final String FUSEKI_DATA_DEFAULT_ACCEPT_HEADER = 
            "application/ld+json";
    
    @Autowired
    private JenaTriplestoreService jenaTriplestoreService;
    
    /**************************************************************************
     * TRIPLESTORE MANAGEMENT
     *************************************************************************/
    
    @Test
    @Order(1)
    void testGetNonExistentRepository() throws IOException {
        ResponseEntity<String> response = jenaTriplestoreService
                .getRepository(NON_EXISTENT_REPOSITORY_ID);
        assertNull(response);
    }
    
    @Test
    @Order(2)
    void testCreateRepository() throws IOException {
        jenaTriplestoreService.createRepository(NEW_REPOSITORY_ID);
        ResponseEntity<String> response = jenaTriplestoreService
                .getRepository(NEW_REPOSITORY_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    @Order(3)
    void testLoadOntologyOwlRdfXml() throws IOException {
        ClassLoader classLoader = ITJenaTriplestoreService.class.getClassLoader();
        String owlFile = classLoader.getResource(TEST_ONTOLOGY_FILENAME).getPath();
        jenaTriplestoreService.loadOntologyOwlRdfXml(NEW_REPOSITORY_ID, owlFile);
        ResponseEntity<String> countResponse = jenaTriplestoreService.query(
                NEW_REPOSITORY_ID, SPARQL_QUERY_COUNT_TRIPLES, 
                FUSEKI_SPARQL_QUERY_DEFAULT_ACCEPT_HEADER);
        assertTrue(countResponse.getBody().contains(
                String.valueOf(TEST_ONTOLOGY_TRIPLES_COUNT)));        
    }
    
    @Test
    @Order(4)
    void testQuery() throws ParseException {
        ResponseEntity<String> queryResponse = jenaTriplestoreService.query(
                NEW_REPOSITORY_ID, SPARQL_QUERY_SELECT_TRIPLES, 
                FUSEKI_SPARQL_QUERY_DEFAULT_ACCEPT_HEADER);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(queryResponse.getBody());
        JSONObject results = (JSONObject) json.get(SPARQL_QUERY_RESULTS_KEY);
        JSONArray bindings = (JSONArray) results.get(SPARQL_QUERY_BINDINGS_KEY);
        assertEquals(SPARQL_QUERY_SELECT_TRIPLES_BINDINGS_SIZE, bindings.size());
    }
    
    @Test
    @Order(5)
    void testGetData() throws ParseException {
        ResponseEntity<String> response = jenaTriplestoreService.getData(
                NEW_REPOSITORY_ID, FUSEKI_DATA_DEFAULT_ACCEPT_HEADER);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.getBody());
        JSONArray graph = (JSONArray) json.get(GET_DATA_GRAPH_KEY);
        assertEquals(GET_DATA_GRAPH_SIZE, graph.size());
    }
    
    @Test
    @Order(6)
    void testDeleteRepository() throws IOException {
        jenaTriplestoreService.deleteRepository(NEW_REPOSITORY_ID);
        ResponseEntity<String> response = jenaTriplestoreService
                .getRepository(NEW_REPOSITORY_ID);
        assertNull(response);
    }

}
