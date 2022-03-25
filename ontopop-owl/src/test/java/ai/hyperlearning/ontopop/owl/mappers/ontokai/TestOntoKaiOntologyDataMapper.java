package ai.hyperlearning.ontopop.owl.mappers.ontokai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyPayload;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;
import ai.hyperlearning.ontopop.owl.OWLRDFXMLAPI;

/**
 * Unit Tests - OntoKai Ontology Data Mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@TestInstance(Lifecycle.PER_CLASS)
class TestOntoKaiOntologyDataMapper {
    
    private static final String TEST_ONTOLOGY_OWL_FILENAME = 
            "test-ontology-ingested-ontokai-ontology-data-mapper.owl";
    private static final String TEST_ONTOLOGY_PARSED_FILENAME = 
            "test-ontology-parsed-ontokai-ontology-data-mapper.owl.json";
    private static final String TEST_ONTOKAI_PAYLOAD_FILENAME = 
            "test-ontokai-ontology-data-payload.json";
    private static final int TEST_CREATED_ANNOTATION_PROPERTIES_COUNT = 0;
    private static final int TEST_UPDATED_ANNOTATION_PROPERTIES_COUNT = 0;
    private static final int TEST_DELETED_ANNOTATION_PROPERTIES_COUNT = 0;
    private static final int TEST_CREATED_OBJECT_PROPERTIES_COUNT = 2;
    private static final int TEST_UPDATED_OBJECT_PROPERTIES_COUNT = 0;
    private static final int TEST_DELETED_OBJECT_PROPERTIES_COUNT = 0;
    private static final int TEST_CREATED_CLASSES_COUNT = 2;
    private static final int TEST_UPDATED_CLASSES_COUNT = 17;
    private static final int TEST_DELETED_CLASSES_COUNT = 254;
    private SimpleOntology simpleOntology = null;
    private OntoKaiOntologyPayload ontokaiOntologyPayload = null;
    
    @BeforeAll
    public void loadParsedOntology() 
            throws JsonParseException, JsonMappingException, IOException {
        ClassLoader classLoader = TestOntoKaiOntologyDataMapper
                .class.getClassLoader();
        ObjectMapper mapper = new ObjectMapper();
        simpleOntology = mapper.readValue(
                new File(classLoader.getResource(
                        TEST_ONTOLOGY_PARSED_FILENAME).getFile()), 
                SimpleOntology.class);
    }
    
    @BeforeAll
    public void loadOntoKaiPayload() 
            throws JsonParseException, JsonMappingException, IOException {
        ClassLoader classLoader = TestOntoKaiOntologyDataMapper
                .class.getClassLoader();
        ObjectMapper mapper = new ObjectMapper();
        ontokaiOntologyPayload = mapper.readValue(
                new File(classLoader.getResource(
                        TEST_ONTOKAI_PAYLOAD_FILENAME).getFile()), 
                OntoKaiOntologyPayload.class);
    }
    
    /**************************************************************************
     * OntoKaiOntologyDataMapper SimpleOntologyDiff generator
     *************************************************************************/
    
    @Test
    void testGenerateSimpleOntologyDiff() 
            throws OWLOntologyCreationException, IOException {
        SimpleOntologyDiff simpleOntologyDiff = OntoKaiOntologyDataMapper
                .generateSimpleOntologyDiff(ontokaiOntologyPayload, 
                        simpleOntology);
        assertEquals(TEST_CREATED_ANNOTATION_PROPERTIES_COUNT, 
                simpleOntologyDiff.getCreatedSimpleAnnotationProperties().size());
        assertEquals(TEST_UPDATED_ANNOTATION_PROPERTIES_COUNT, 
                simpleOntologyDiff.getUpdatedSimpleAnnotationProperties().size());
        assertEquals(TEST_DELETED_ANNOTATION_PROPERTIES_COUNT, 
                simpleOntologyDiff.getDeletedSimpleAnnotationProperties().size());
        assertEquals(TEST_CREATED_OBJECT_PROPERTIES_COUNT, 
                simpleOntologyDiff.getCreatedSimpleObjectProperties().size());
        assertEquals(TEST_UPDATED_OBJECT_PROPERTIES_COUNT, 
                simpleOntologyDiff.getUpdatedSimpleObjectProperties().size());
        assertEquals(TEST_DELETED_OBJECT_PROPERTIES_COUNT, 
                simpleOntologyDiff.getDeletedSimpleObjectProperties().size());
        assertEquals(TEST_CREATED_CLASSES_COUNT, 
                simpleOntologyDiff.getCreatedSimpleClasses().size());
        assertEquals(TEST_UPDATED_CLASSES_COUNT, 
                simpleOntologyDiff.getUpdatedSimpleClasses().size());
        assertEquals(TEST_DELETED_CLASSES_COUNT, 
                simpleOntologyDiff.getDeletedSimpleClasses().size());
    }
    
    /**************************************************************************
     * OntoKaiOntologyDataMapper SimpleOntologyDiff RDF/XML string
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     *************************************************************************/
    
    @Test
    void testOWLRDFXMLAPIGenerateRdfXmlOwlString() 
            throws IOException, OWLOntologyCreationException {
        ClassLoader classLoader = TestOntoKaiOntologyDataMapper
                .class.getClassLoader();
        String existingOwlRdfXml = FileUtils.readFileToString(new File(
                classLoader.getResource(TEST_ONTOLOGY_OWL_FILENAME).getFile()), 
                StandardCharsets.UTF_8);
        SimpleOntologyDiff simpleOntologyDiff = OntoKaiOntologyDataMapper
                .generateSimpleOntologyDiff(ontokaiOntologyPayload, 
                        simpleOntology);
        String generatedOwlRdfXml = OWLRDFXMLAPI.generateRdfXmlOwlString(
                existingOwlRdfXml, simpleOntologyDiff, "OntoPop");
        assertFalse(StringUtils.isAllBlank(generatedOwlRdfXml));
    }

}
