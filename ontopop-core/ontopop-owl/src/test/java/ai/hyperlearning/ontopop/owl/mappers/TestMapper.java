package ai.hyperlearning.ontopop.owl.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidRequestException;

/**
 * Unit Tests - Ontology data (e.g. RDF/XML) to target format (e.g. GRAPHSON) mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@TestInstance(Lifecycle.PER_CLASS)
class TestMapper {
    
    private static final String TEST_ONTOLOGY_FILENAME_VALID = 
            "test-ontology.owl";
    private static final String TEST_ONTOLOGY_FILENAME_INVALID_BLANK = 
            "test-ontology-invalid-blank.owl";
    private static final String TEST_ONTOLOGY_FILENAME_INVALID_FILE_EXTENSION = 
            "test-ontology-invalid-file-extension.abc";
    private static final String TEST_ONTOLOGY_FILENAME_INVALID_FILE_SIZE = 
            "test-ontology-invalid-file-size.owl";
    private static final String TEST_ONTOLOGY_FILENAME_TTL = 
            "test-ontology.ttl";
    private static final int TEST_ONTOLOGY_VERTEX_COUNT = 285;
    private static final int TEST_ONTOLOGY_EDGE_COUNT = 696;
    
    private static final String RDF_XML_FORMAT = "rdf-xml";
    private static final String GRAPHSON_FORMAT = "graphson";
    private static final String GRAPHSON_GRAPH_OBJECT_KEY = "graph";
    private static final String GRAPHSON_VERTICES_ARRAY_KEY = "vertices";
    private static final String GRAPHSON_EDGES_ARRAY_KEY = "edges";
    private static final String VIS_FORMAT = "vis";
    private static final String VIS_GRAPH_OBJECT_KEY = "graph";
    private static final String VIS_VERTICES_ARRAY_KEY = "vertices";
    private static final String VIS_EDGES_ARRAY_KEY = "edges";
    
    private String testOntologyFilePathValid;
    private String testOntologyFilePathInvalidBlank;
    private String testOntologyFilePathInvalidFileExtension;
    private String testOntologyFilePathInvalidFileSize;
    private String testOntologyFilePathTtl;
    
    @BeforeAll
    public void getTestOntologyFilePaths() {
        ClassLoader classLoader = TestMapper
                .class.getClassLoader();
        testOntologyFilePathValid = classLoader.getResource(
                TEST_ONTOLOGY_FILENAME_VALID).getPath();
        testOntologyFilePathInvalidBlank = classLoader.getResource(
                TEST_ONTOLOGY_FILENAME_INVALID_BLANK).getPath();
        testOntologyFilePathInvalidFileExtension = classLoader.getResource(
                TEST_ONTOLOGY_FILENAME_INVALID_FILE_EXTENSION).getPath();
        testOntologyFilePathInvalidFileSize = classLoader.getResource(
                TEST_ONTOLOGY_FILENAME_INVALID_FILE_SIZE).getPath();
        testOntologyFilePathTtl = classLoader.getResource(
                TEST_ONTOLOGY_FILENAME_TTL).getPath();
    }
    
    /**************************************************************************
     * VALIDATION - Source Format
     *************************************************************************/
    
    @Test
    void testMapInvalidSourceFormat() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("json", "graphson", testOntologyFilePathValid);
        });
        String expectedMessage = "Invalid source format provided.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    void testMapOwlXmlInvalidSourceFormat() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("turtle", "owl-xml", testOntologyFilePathTtl);
        });
        String expectedMessage = "Invalid source format provided.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * VALIDATION - Target Format
     *************************************************************************/
    
    @Test
    void testMapInvalidTargetFormat() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("rdf-xml", "json", testOntologyFilePathValid);
        });
        String expectedMessage = "Invalid target format provided.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * VALIDATION - Existence
     *************************************************************************/
    
    @Test
    void testMapInvalidExistence() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("rdf-xml", "graphson", "/tmp/i-do-not-exist.owl");
        });
        String expectedMessage = "Invalid ontology data file provided - "
                + "file does not exist.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * VALIDATION - File Extension
     *************************************************************************/
    
    @Test
    void testMapInvalidFileExtension() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("rdf-xml", "graphson", 
                    testOntologyFilePathInvalidFileExtension);
        });
        String expectedMessage = "Invalid ontology data file provided - "
                + "file extension does not match the specified source format.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * VALIDATION - File Size
     *************************************************************************/
    
    @Test
    void testMapInvalidFileSize() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("rdf-xml", "graphson", 
                    testOntologyFilePathInvalidFileSize);
        });
        String expectedMessage = "Invalid ontology data file provided - "
                + "file size limit exceeded.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * VALIDATION - Blank
     *************************************************************************/
    
    @Test
    void testMapInvalidBlank() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("rdf-xml", "graphson", 
                    testOntologyFilePathInvalidBlank);
        });
        String expectedMessage = "Invalid ontology data file provided - "
                + "file is blank.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * VALIDATION - Source Format equals Target Format
     *************************************************************************/
    
    @Test
    void testMapInvalidSourceTargetFormat() {
        Exception exception = assertThrows(
                OntologyMapperInvalidRequestException.class, () -> {
            Mapper.map("rdf-xml", "rdf-xml", 
                    testOntologyFilePathValid);
        });
        String expectedMessage = "Invalid target format provided.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    /**************************************************************************
     * MAPPER - Formats
     *************************************************************************/
    
    @Test
    void testMapGrapSon() throws OntologyMapperInvalidRequestException, 
        OntologyDataParsingException, 
        OntologyDataPipelineException, 
        OWLOntologyCreationException, 
        OWLOntologyStorageException, 
        IOException, JSONException {
        
        // Format and parse the GraphSON string
        String graphSonString = Mapper.map(RDF_XML_FORMAT, GRAPHSON_FORMAT, 
                testOntologyFilePathValid);
        JSONObject graphSon = new JSONObject(graphSonString);
        JSONArray vertices = graphSon.getJSONObject(GRAPHSON_GRAPH_OBJECT_KEY)
                .getJSONArray(GRAPHSON_VERTICES_ARRAY_KEY);
        JSONArray edges = graphSon.getJSONObject(GRAPHSON_GRAPH_OBJECT_KEY)
                .getJSONArray(GRAPHSON_EDGES_ARRAY_KEY);
        
        // GraphSON unit tests
        assertFalse(StringUtils.isAllBlank(graphSonString));
        assertEquals(TEST_ONTOLOGY_VERTEX_COUNT, vertices.length());
        assertEquals(TEST_ONTOLOGY_EDGE_COUNT, edges.length());
        
    }
    
    @Test
    void testMapVis() throws OntologyMapperInvalidRequestException, 
        OntologyDataParsingException, 
        OntologyDataPipelineException, 
        OWLOntologyCreationException, 
        OWLOntologyStorageException, 
        IOException, JSONException {
        
        // Format and parse the Vis Dataset string
        String visString = Mapper.map(RDF_XML_FORMAT, VIS_FORMAT, 
                testOntologyFilePathValid);
        JSONObject vis = new JSONObject(visString);
        JSONArray vertices = vis.getJSONObject(VIS_GRAPH_OBJECT_KEY)
                .getJSONArray(VIS_VERTICES_ARRAY_KEY);
        JSONArray edges = vis.getJSONObject(VIS_GRAPH_OBJECT_KEY)
                .getJSONArray(VIS_EDGES_ARRAY_KEY);
        
        // Vis Dataset unit tests
        assertFalse(StringUtils.isAllBlank(visString));
        assertEquals(TEST_ONTOLOGY_VERTEX_COUNT, vertices.length());
        assertEquals(TEST_ONTOLOGY_EDGE_COUNT, edges.length());
        
    }

}
