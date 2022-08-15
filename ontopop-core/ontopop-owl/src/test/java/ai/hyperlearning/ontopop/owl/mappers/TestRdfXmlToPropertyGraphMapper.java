package ai.hyperlearning.ontopop.owl.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPipelineException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidRequestException;

/**
 * Unit Tests - RDF/XML to target graph-based format mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@TestInstance(Lifecycle.PER_CLASS)
class TestRdfXmlToPropertyGraphMapper {
    
    private static final String TEST_ONTOLOGY_FILENAME = 
            "test-ontology.owl";
    private static final String GRAPHSON_FORMAT = "graphson";
    private static final String GRAPHSON_GRAPH_OBJECT_KEY = "graph";
    private static final String GRAPHSON_VERTICES_ARRAY_KEY = "vertices";
    private static final String GRAPHSON_EDGES_ARRAY_KEY = "edges";
    private static final String VIS_FORMAT = "vis";
    private static final String VIS_GRAPH_OBJECT_KEY = "graph";
    private static final String VIS_VERTICES_ARRAY_KEY = "vertices";
    private static final String VIS_EDGES_ARRAY_KEY = "edges";
    private static final int TEST_ONTOLOGY_VERTEX_COUNT = 285;
    private static final int TEST_ONTOLOGY_EDGE_COUNT = 696;
    private String testOntologyFilePath;
    
    @BeforeAll
    public void getTestOntologyFilePath() {
        ClassLoader classLoader = TestRdfXmlToPropertyGraphMapper
                .class.getClassLoader();
        testOntologyFilePath = classLoader.getResource(
                TEST_ONTOLOGY_FILENAME).getPath();
    }
    
    /**************************************************************************
     * GRAPH FORMAT - GRAPHSON
     *************************************************************************/
    
    @Test
    void testMapGraphSon() throws OntologyMapperInvalidRequestException, 
        OntologyDataParsingException, OntologyDataPipelineException, 
        IOException, JSONException {
        
        // Format and parse the GraphSON string
        String graphSonString = RdfXmlToPropertyGraphMapper.map(
                testOntologyFilePath, GRAPHSON_FORMAT);
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
    
    /**************************************************************************
     * GRAPH FORMAT - VIS DATASET
     *************************************************************************/
    
    @Test
    void testMapVis() throws OntologyMapperInvalidRequestException, 
        OntologyDataParsingException, OntologyDataPipelineException, 
        IOException, JSONException {
        
        // Format and parse the Vis Dataset string
        String visString = RdfXmlToPropertyGraphMapper.map(
                testOntologyFilePath, VIS_FORMAT);
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
