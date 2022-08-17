package a.hyperlearning.ontopop.api.ontology.mapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ai.hyperlearning.ontopop.api.ontology.mapping.OntologyMappingController;
import ai.hyperlearning.ontopop.exceptions.OntoPopExceptionHandler;
import ai.hyperlearning.ontopop.webprotege.WebProtegeDownloader;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test - Ontology Mapping API Service - Mapping Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ContextConfiguration(classes = { OntologyMappingController.class })
@WebMvcTest(controllers = OntologyMappingController.class)
@ImportAutoConfiguration(OntoPopExceptionHandler.class)
@ActiveProfiles("integration-test-mapping-api")
class TestOntologyMappingControllerIT {
    
    // Request parameters
    private static final String MAPPING_MAP_URI = "/mapping/map";
    private static final String REQUEST_PARAMETER_SOURCE_NAME = "source";
    private static final String REQUEST_PARAMETER_TARGET_NAME = "target";
    private static final String REQUEST_PARAMETER_FILE_NAME = "file";
    private static final String REQUEST_PARAMETER_WEBPROTEGEID_NAME = "webProtegeId";
    
    // Test resources
    private static final String TEST_ONTOLOGY_MEDIA_TYPE = "application/rdf+xml";
    private static final String TEST_FULL_VALID_ONTOLOGY_FILENAME = "test-ontology.owl";
    private static final String TEST_FULL_VALID_ONTOLOGY_GRAPHSON_FILENAME = "test-ontology-graphson.json";
    private static final String TEST_FULL_VALID_ONTOLOGY_VIS_FILENAME = "test-ontology-vis.json";
    private static final String TEST_SMALL_VALID_ONTOLOGY_FILENAME = "test-ontology-small.owl";
    
    // Test resource multipart files
    private static MockMultipartFile testFullOntologyFile;
    private static MockMultipartFile testSmallOntologyFile;
    private static MockMultipartFile testBlankOntologyFile;
    private static MockMultipartFile testInvalidFileExtensionOntologyFile;
    
    // Test resource contents
    private static String testFullOntologyGraphSon;
    private static String testFullOntologyVis;
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @MockBean
    private WebProtegeDownloader webProtegeDownloader;
    
    /**************************************************************************
     * SETUP
     **************************************************************************/
    
    @BeforeAll
    static void buildMockMultipartFiles() throws IOException {
        
        // Identify the test resource file paths
        ClassLoader classLoader = TestOntologyMappingControllerIT
                .class.getClassLoader();
        String testFullOntologyFilePath = classLoader.getResource(
                TEST_FULL_VALID_ONTOLOGY_FILENAME).getPath();
        String testFullOntologyGraphSonFilePath = classLoader.getResource(
                TEST_FULL_VALID_ONTOLOGY_GRAPHSON_FILENAME).getPath();
        String testFullOntologyVisFilePath = classLoader.getResource(
                TEST_FULL_VALID_ONTOLOGY_VIS_FILENAME).getPath();
        String testSmallOntologyFilePath = classLoader.getResource(
                TEST_SMALL_VALID_ONTOLOGY_FILENAME).getPath();
        
        // Load the test resource contents
        String testFullOntologyContents = Files.readString(
                Paths.get(testFullOntologyFilePath), StandardCharsets.UTF_8);
        String testSmallOntologyContents = Files.readString(
                Paths.get(testSmallOntologyFilePath), StandardCharsets.UTF_8);
        testFullOntologyGraphSon = Files.readString(
                Paths.get(testFullOntologyGraphSonFilePath), StandardCharsets.UTF_8);
        testFullOntologyVis = Files.readString(
                Paths.get(testFullOntologyVisFilePath), StandardCharsets.UTF_8);
        
        // Build the mock multipart files
        testFullOntologyFile = new MockMultipartFile(
                REQUEST_PARAMETER_FILE_NAME, 
                TEST_SMALL_VALID_ONTOLOGY_FILENAME, 
                TEST_ONTOLOGY_MEDIA_TYPE, 
                testFullOntologyContents.getBytes());
        testSmallOntologyFile = new MockMultipartFile(
                REQUEST_PARAMETER_FILE_NAME, 
                TEST_SMALL_VALID_ONTOLOGY_FILENAME, 
                TEST_ONTOLOGY_MEDIA_TYPE, 
                testSmallOntologyContents.getBytes());
        testBlankOntologyFile = new MockMultipartFile(
                REQUEST_PARAMETER_FILE_NAME, 
                TEST_SMALL_VALID_ONTOLOGY_FILENAME, 
                TEST_ONTOLOGY_MEDIA_TYPE, 
                " ".getBytes());
        testInvalidFileExtensionOntologyFile = new MockMultipartFile(
                REQUEST_PARAMETER_FILE_NAME, 
                "test-ontology.json", 
                TEST_ONTOLOGY_MEDIA_TYPE, 
                testSmallOntologyContents.getBytes());
        
    }
    
    /**************************************************************************
     * TESTS - INVALID REQUEST PARAMETERS
     **************************************************************************/
    
    @Test
    void whenSourceInvalid_thenReturns400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(MAPPING_MAP_URI)
                    .file(testSmallOntologyFile)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "json")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "graphson");
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenTargetInvalid_thenReturns400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(MAPPING_MAP_URI)
                    .file(testSmallOntologyFile)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "json");
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenFileBlank_thenReturns400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(MAPPING_MAP_URI)
                    .file(testBlankOntologyFile)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "graphson");
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenFileExtensionInvalid_thenReturns400() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(MAPPING_MAP_URI)
                    .file(testInvalidFileExtensionOntologyFile)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "graphson");
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenWebProtegeIdInvalidTooShort_thenReturns400() throws Exception {
        String invalidWebProtegeProjectId = "abc-123";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(MAPPING_MAP_URI)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "graphson")
                    .param(REQUEST_PARAMETER_WEBPROTEGEID_NAME, 
                            invalidWebProtegeProjectId);
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenWebProtegeIdInvalidNonAlphaNumeric_thenReturns400() 
            throws Exception {
        String invalidWebProtegeProjectId = "abc-123$-def";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(MAPPING_MAP_URI)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "graphson")
                    .param(REQUEST_PARAMETER_WEBPROTEGEID_NAME, 
                            invalidWebProtegeProjectId);
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }
    
    /**************************************************************************
     * TESTS - HTTP OK
     **************************************************************************/
    
    @Test
    void whenFullOntologyFileValid_thenReturns200AndGraphSon() 
            throws Exception {
        
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(MAPPING_MAP_URI)
                    .file(testFullOntologyFile)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "graphson");
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        
        // Do not use JSONAssert as the vertex and edge IDs across
        // different runs are not guaranteed be consistent
        assertTrue(testFullOntologyGraphSon.length() <= 
                result.getResponse().getContentAsString().length());
        
    }
    
    @Test
    void whenFullOntologyFileValid_thenReturns200AndVisDataset() 
            throws Exception {
        
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(MAPPING_MAP_URI)
                    .file(testFullOntologyFile)
                    .param(REQUEST_PARAMETER_SOURCE_NAME, "rdf-xml")
                    .param(REQUEST_PARAMETER_TARGET_NAME, "vis");
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        
        // Do not use JSONAssert as the vertex and edge IDs across
        // different runs are not guaranteed be consistent
        assertTrue(testFullOntologyVis.length() <= 
                result.getResponse().getContentAsString().length());
        
    }

}
