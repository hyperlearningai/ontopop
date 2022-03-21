package ai.hyperlearning.ontopop.owl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;

/**
 * Unit Tests - OWL API 5 Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@TestInstance(Lifecycle.PER_CLASS)
class TestOWLAPI {
    
    private static final String TEST_ONTOLOGY_FILENAME = 
            "test-ontology.owl";
    private static final String TEST_ONTOLOGY_DIFF_FILENAME = 
            "test-ontology-diff.owl";
    private static final int TEST_ONTOLOGY_ANNOTATION_PROPERTIES_COUNT = 15;
    private static final int TEST_ONTOLOGY_OBJECT_PROPERTIES_COUNT = 71;
    private static final int TEST_ONTOLOGY_CLASS_COUNT = 285;
    private static final int TEST_ONTOLOGY_DIFF_CREATED_ANNOTATION_PROPERTIES_COUNT = 0;
    private static final int TEST_ONTOLOGY_DIFF_UPDATED_ANNOTATION_PROPERTIES_COUNT = 2;
    private static final int TEST_ONTOLOGY_DIFF_DELETED_ANNOTATION_PROPERTIES_COUNT = 1;
    private static final int TEST_ONTOLOGY_DIFF_CREATED_OBJECT_PROPERTIES_COUNT = 1;
    private static final int TEST_ONTOLOGY_DIFF_UPDATED_OBJECT_PROPERTIES_COUNT = 2;
    private static final int TEST_ONTOLOGY_DIFF_DELETED_OBJECT_PROPERTIES_COUNT = 0;
    private static final int TEST_ONTOLOGY_DIFF_CREATED_CLASSES_COUNT = 1;
    private static final int TEST_ONTOLOGY_DIFF_UPDATED_CLASSES_COUNT = 1;
    private static final int TEST_ONTOLOGY_DIFF_DELETED_CLASSES_COUNT = 1;
    private OWLOntology ontology = null;
    
    @BeforeAll
    public void loadOntology() 
            throws OWLOntologyCreationException {
        ClassLoader classLoader = TestOWLAPI.class.getClassLoader();
        ontology = OWLAPI.loadOntology(new File(classLoader
                .getResource(TEST_ONTOLOGY_FILENAME).getFile()));
    }
    
    /**************************************************************************
     * Loaders
     *************************************************************************/
    
    @Test
    void testLoadOntologyFromFile() {
        assertFalse(ontology.isEmpty());
    }
    
    /**************************************************************************
     * Reasoners
     *************************************************************************/
    
    @Test
    void testIsConsistent() {
        assertTrue(OWLAPI.isConsistent(ontology));
    }
    
    /**************************************************************************
     * Annotation Properties
     *************************************************************************/
    
    @Test
    void testParseAnnotationProperties() {
        Map<String, SimpleAnnotationProperty> annotationProperties = 
                OWLAPI.parseAnnotationProperties(ontology);
        assertEquals(TEST_ONTOLOGY_ANNOTATION_PROPERTIES_COUNT, 
                annotationProperties.size());
    }
    
    /**************************************************************************
     * Object Properties
     *************************************************************************/
    
    @Test
    void testParseObjectProperties() {
        Map<String, SimpleObjectProperty> objectProperties = 
                OWLAPI.parseObjectProperties(ontology);
        assertEquals(TEST_ONTOLOGY_OBJECT_PROPERTIES_COUNT, 
                objectProperties.size());
    }
    
    /**************************************************************************
     * Classes
     *************************************************************************/
    
    @Test
    void testParseClasses() {
        Map<String, SimpleClass> classes = OWLAPI.parseClasses(ontology);
        assertEquals(TEST_ONTOLOGY_CLASS_COUNT, classes.size());
    }
    
    /**************************************************************************
     * Diff
     *************************************************************************/
    
    @Test
    void testDiff() throws OWLOntologyCreationException, IOException {
        ClassLoader classLoader = TestOWLAPI.class.getClassLoader();
        String leftOwlFile = classLoader
                .getResource(TEST_ONTOLOGY_FILENAME).getPath();
        String rightOwlFile = classLoader
                .getResource(TEST_ONTOLOGY_DIFF_FILENAME).getPath();
        SimpleOntologyDiff diff = OWLAPI.diff(leftOwlFile, rightOwlFile);
        assertTrue(diff.doChangesExist());
        assertEquals(TEST_ONTOLOGY_DIFF_CREATED_ANNOTATION_PROPERTIES_COUNT, 
                diff.getCreatedSimpleAnnotationProperties().size());
        assertEquals(TEST_ONTOLOGY_DIFF_UPDATED_ANNOTATION_PROPERTIES_COUNT, 
                diff.getUpdatedSimpleAnnotationProperties().size());
        assertEquals(TEST_ONTOLOGY_DIFF_DELETED_ANNOTATION_PROPERTIES_COUNT, 
                diff.getDeletedSimpleAnnotationProperties().size());
        assertEquals(TEST_ONTOLOGY_DIFF_CREATED_OBJECT_PROPERTIES_COUNT, 
                diff.getCreatedSimpleObjectProperties().size());
        assertEquals(TEST_ONTOLOGY_DIFF_UPDATED_OBJECT_PROPERTIES_COUNT, 
                diff.getUpdatedSimpleObjectProperties().size());
        assertEquals(TEST_ONTOLOGY_DIFF_DELETED_OBJECT_PROPERTIES_COUNT, 
                diff.getDeletedSimpleObjectProperties().size());
        assertEquals(TEST_ONTOLOGY_DIFF_CREATED_CLASSES_COUNT, 
                diff.getCreatedSimpleClasses().size());
        assertEquals(TEST_ONTOLOGY_DIFF_UPDATED_CLASSES_COUNT, 
                diff.getUpdatedSimpleClasses().size());
        assertEquals(TEST_ONTOLOGY_DIFF_DELETED_CLASSES_COUNT, 
                diff.getDeletedSimpleClasses().size());
    }

}
