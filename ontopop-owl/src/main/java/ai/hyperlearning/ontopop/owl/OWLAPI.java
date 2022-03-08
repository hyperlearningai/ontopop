package ai.hyperlearning.ontopop.owl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLAPIStreamUtils;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleAnnotationPropertyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleClassDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleObjectPropertyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;

/**
 * OWL API 5 Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OWLAPI {

    private static final OWLOntologyManager SHARED_OWL_ONTOLOGY_MANAGER =
            OWLManager.createOWLOntologyManager();
    private static final OWLDataFactory SHARED_OWL_DATA_FACTORY =
            SHARED_OWL_ONTOLOGY_MANAGER.getOWLDataFactory();
    private static final String DELIMITER = "|";

    /**************************************************************************
     * Loaders
     *************************************************************************/

    /**
     * Load an OWL ontology given an OWL file
     * 
     * @param owlFile
     * @return
     * @throws OWLOntologyCreationException
     */

    public static OWLOntology loadOntology(File owlFile)
            throws OWLOntologyCreationException {
        OWLOntologyManager owlOntologyManager = 
                OWLManager.createOWLOntologyManager();
        return owlOntologyManager.loadOntologyFromOntologyDocument(owlFile);
    }

    /**
     * Load an OWL ontology given an input stream to an OWL file
     * 
     * @param owlFile
     * @return
     * @throws OWLOntologyCreationException
     */

    public static OWLOntology loadOntology(InputStream owlFile)
            throws OWLOntologyCreationException {
        OWLOntologyManager owlOntologyManager = 
                OWLManager.createOWLOntologyManager();
        return owlOntologyManager.loadOntologyFromOntologyDocument(owlFile);
    }

    /**************************************************************************
     * Reasoners
     *************************************************************************/

    /**
     * Test whether a given OWL ontology is consistent using the HermiT reasoner
     * 
     * @param ontology
     * @return
     */

    public static boolean isConsistent(OWLOntology ontology) {
        Configuration configuration = new Configuration();
        OWLReasoner reasoner = new Reasoner(configuration, ontology);
        return reasoner.isConsistent();
    }

    /**************************************************************************
     * Annotations
     *************************************************************************/

    /**
     * Extract the literal value of an annotation value
     * 
     * @param annotation
     * @return
     */

    public static String getAnnotationValueLiteral(OWLAnnotation annotation) {
        OWLAnnotationValue annotationValue = annotation.getValue();
        try {
            return ((OWLLiteral) annotationValue).getLiteral();
        } catch (ClassCastException e) {
            return annotationValue.toString();
        }

    }

    /**
     * Extract the RDFS Label literal value from a given list of OWL annotations
     * 
     * @param annotations
     * @return
     */

    public static String getRDFSLabel(List<OWLAnnotation> annotations) {
        for (OWLAnnotation annotation : annotations) {
            if (annotation.getProperty()
                    .equals(SHARED_OWL_DATA_FACTORY.getRDFSLabel())) {
                return getAnnotationValueLiteral(annotation);
            }
        }
        return null;
    }

    /**************************************************************************
     * Annotation Properties
     *************************************************************************/

    public static Set<OWLAnnotationProperty> getAnnotationProperties(
            OWLOntology ontology) {
        return ontology.getAnnotationPropertiesInSignature();
    }

    /**
     * Parse the ontology and generate a map of Annotation Property IRI to
     * OntoPop Simple Annotation Property objects
     * 
     * @param ontology
     * @return
     */

    public static Map<String, SimpleAnnotationProperty> parseAnnotationProperties(
            OWLOntology ontology) {

        Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap =
                new LinkedHashMap<>();

        // Iterate over all OWL annotation properties found in the OWL ontology
        Set<OWLAnnotationProperty> owlAnnotationProperties =
                getAnnotationProperties(ontology);
        for (OWLAnnotationProperty owlAnnotationProperty : owlAnnotationProperties) {

            // Get the OWL annotation property IRI
            String annotationPropertyIri =
                    owlAnnotationProperty.getIRI().toString();

            // Extract the list of OWL annotations from this
            // OWL annotation property
            List<OWLAnnotation> owlAnnotations =
                    OWLAPIStreamUtils.asList(EntitySearcher
                            .getAnnotations(owlAnnotationProperty, ontology));

            // Get the OWL annotation property RDFS Label
            String annotationPropertyRDFSLabel = getRDFSLabel(owlAnnotations);

            // Generate a map of annotation IRI to annotation literal value
            Map<String, String> annotations = new LinkedHashMap<>();
            for (OWLAnnotation owlAnnotation : owlAnnotations) {
                String iri = owlAnnotation.getProperty().getIRI().toString();
                String literalValue = annotations.containsKey(iri) ? 
                        annotations.get(iri) + " " + DELIMITER + " " + 
                            getAnnotationValueLiteral(owlAnnotation) : 
                                getAnnotationValueLiteral(owlAnnotation);
                annotations.put(iri, literalValue);
            }

            // Create a Simple Annotation Property object and add it
            // to the map of Simple Annotation Property objects
            SimpleAnnotationProperty simpleAnnotationProperty =
                    new SimpleAnnotationProperty(annotationPropertyIri,
                            annotationPropertyRDFSLabel, annotations);

            // Add the new Simple Annotation Property to the map of
            // Simple Annotation Property objects
            simpleAnnotationPropertyMap.put(annotationPropertyIri,
                    simpleAnnotationProperty);

        }

        return simpleAnnotationPropertyMap;

    }

    /**************************************************************************
     * Data Types
     *************************************************************************/

    public static Set<OWLDatatype> getDatatypes(OWLOntology ontology) {
        return ontology.getDatatypesInSignature();
    }

    /**************************************************************************
     * Data Properties
     *************************************************************************/

    public static Set<OWLDataProperty> getDataProperties(OWLOntology ontology) {
        return ontology.getDataPropertiesInSignature();
    }

    /**************************************************************************
     * Object Properties
     *************************************************************************/

    /**
     * Get all object properties
     * 
     * @param ontology
     * @return
     */

    public static Set<OWLObjectProperty> getObjectProperties(
            OWLOntology ontology) {
        return ontology.getObjectPropertiesInSignature();
    }

    /**
     * Get all object property subPropertyOf relationships
     * 
     * @param ontology
     * @return
     */

    public static Set<OWLSubObjectPropertyOfAxiom> getSubPropertyOfAxioms(
            OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.SUB_OBJECT_PROPERTY);
    }

    /**
     * Parse the ontology and generate a map of Object Property IRI to OntoPop
     * Simple Object Property objects
     * 
     * @param ontology
     * @return
     */

    public static Map<String, SimpleObjectProperty> parseObjectProperties(
            OWLOntology ontology) {

        Map<String, SimpleObjectProperty> simpleObjectPropertyMap =
                new LinkedHashMap<>();

        // Iterate over all OWL object properties found in the OWL ontology
        Set<OWLObjectProperty> owlObjectProperties =
                getObjectProperties(ontology);
        for (OWLObjectProperty owlObjectProperty : owlObjectProperties) {

            // Get the OWL object property IRI
            String objectPropertyIri = owlObjectProperty.getIRI().toString();

            // Extract the list of OWL annotations from this
            // OWL object property
            List<OWLAnnotation> owlAnnotations = OWLAPIStreamUtils.asList(
                    EntitySearcher.getAnnotations(owlObjectProperty, ontology));

            // Get the OWL object property RDFS Label
            String objectPropertyRDFSLabel = getRDFSLabel(owlAnnotations);

            // Generate a map of annotation IRI to annotation literal value
            Map<String, String> annotations = new LinkedHashMap<>();
            for (OWLAnnotation owlAnnotation : owlAnnotations) {
                String iri = owlAnnotation.getProperty().getIRI().toString();
                String literalValue = annotations.containsKey(iri) ? 
                        annotations.get(iri) + " " + DELIMITER + " " + 
                            getAnnotationValueLiteral(owlAnnotation) : 
                                getAnnotationValueLiteral(owlAnnotation);
                annotations.put(iri, literalValue);
            }

            // Create a Simple Object Property object
            SimpleObjectProperty simpleObjectProperty =
                    new SimpleObjectProperty();
            simpleObjectProperty.setIri(objectPropertyIri);
            simpleObjectProperty.setLabel(objectPropertyRDFSLabel);
            simpleObjectProperty.setAnnotations(annotations);

            // Get all axioms for this object property
            List<OWLAxiom> owlObjectPropertyReferencingAxioms =
                    OWLAPI.getReferencingAxioms(ontology, owlObjectProperty);
            for (OWLAxiom owlObjectPropertyReferencingAxiom : owlObjectPropertyReferencingAxioms) {
                if (owlObjectPropertyReferencingAxiom
                        .getAxiomType() == AxiomType.SUB_OBJECT_PROPERTY) {

                    // Get the parent object property IRI
                    OWLSubObjectPropertyOfAxiom owlObjectPropertySubObjectPropertyOfAxiom =
                            (OWLSubObjectPropertyOfAxiom) owlObjectPropertyReferencingAxiom;
                    String superObjectPropertyIRI =
                            owlObjectPropertySubObjectPropertyOfAxiom
                                    .getSuperProperty().asOWLObjectProperty()
                                    .getIRI().toString();
                    if ( !objectPropertyIri.equals(superObjectPropertyIRI) ) {
                        simpleObjectProperty
                            .setParentObjectPropertyIRI(
                                    superObjectPropertyIRI);
                        break;
                    }

                }
            }

            // Add the new Simple Object Property to the map of
            // Simple Object Property objects
            simpleObjectPropertyMap.put(objectPropertyIri,
                    simpleObjectProperty);

        }

        return simpleObjectPropertyMap;

    }

    /**************************************************************************
     * Individuals
     *************************************************************************/

    public static Set<OWLNamedIndividual> getIndividuals(OWLOntology ontology) {
        return ontology.getIndividualsInSignature();
    }

    /**************************************************************************
     * Classes
     *************************************************************************/

    /**
     * Get all classes
     * 
     * @param ontology
     * @return
     */

    public static Set<OWLClass> getClasses(OWLOntology ontology) {
        return ontology.getClassesInSignature();
    }

    /**
     * Get all class subClassOf relationships
     * 
     * @param ontology
     * @return
     */

    public static Set<OWLSubClassOfAxiom> getSubClassOfAxioms(
            OWLOntology ontology) {
        return ontology.getAxioms(AxiomType.SUBCLASS_OF);
    }

    /**
     * Parse the ontology and generate a map of Class IRI to OntoPop Simple
     * Class objects
     * 
     * @param ontology
     * @return
     */

    public static Map<String, SimpleClass> parseClasses(OWLOntology ontology) {

        Map<String, SimpleClass> simpleClassMap = new LinkedHashMap<>();

        // Iterate over all OWL classes found in the OWL ontology
        Set<OWLClass> owlClasses = getClasses(ontology);
        for (OWLClass owlClass : owlClasses) {

            // Get the OWL class IRI
            String classIri = owlClass.getIRI().toString();

            // Extract the list of OWL annotations from this OWL class
            List<OWLAnnotation> owlAnnotations = OWLAPIStreamUtils
                    .asList(EntitySearcher.getAnnotations(owlClass, ontology));

            // Get the OWL class RDFS Label
            String classRDFSLabel = getRDFSLabel(owlAnnotations);

            // Generate a map of annotation IRI to annotation literal value
            Map<String, String> annotations = new LinkedHashMap<>();
            for (OWLAnnotation owlAnnotation : owlAnnotations) {
                String iri = owlAnnotation.getProperty().getIRI().toString();
                String literalValue = annotations.containsKey(iri) ? 
                        annotations.get(iri) + " " + DELIMITER + " " + 
                            getAnnotationValueLiteral(owlAnnotation) : 
                                getAnnotationValueLiteral(owlAnnotation);
                annotations.put(iri, literalValue);
            }

            // Create a Simple Class object
            SimpleClass simpleClass = new SimpleClass();
            simpleClass.setIri(classIri);
            simpleClass.setLabel(classRDFSLabel);
            simpleClass.setAnnotations(annotations);

            // Get all axioms for this class
            Map<String, String> parentClasses = new LinkedHashMap<>();
            List<OWLAxiom> owlClassReferencingAxioms =
                    OWLAPI.getReferencingAxioms(ontology, owlClass);
            for (OWLAxiom owlClassReferencingAxiom : owlClassReferencingAxioms) {
                if (owlClassReferencingAxiom
                        .getAxiomType() == AxiomType.SUBCLASS_OF) {

                    // Get the parent class IRI for all parent classes
                    // that do not have OWL restrictions
                    OWLSubClassOfAxiom owlClassSubClassOfAxiom =
                            (OWLSubClassOfAxiom) owlClassReferencingAxiom;
                    if (owlClassSubClassOfAxiom
                            .getSuperClass() instanceof OWLClass
                            && owlClassSubClassOfAxiom
                                    .getSubClass() instanceof OWLClass
                            && owlClassReferencingAxiom.getSignature()
                                    .size() == 2) {

                        String superClassIRI =
                                owlClassSubClassOfAxiom.getSuperClass()
                                        .asOWLClass().getIRI().toString();
                        if (!classIri.equals(superClassIRI)) {
                            if ( !parentClasses.containsKey(superClassIRI) )
                                parentClasses.put(superClassIRI, null);
                        };

                    }

                    // Get the parent class IRI and object property IRI for
                    // all parent classes that do have OWL restrictions
                    else if (owlClassSubClassOfAxiom
                            .getSuperClass() instanceof OWLObjectSomeValuesFrom
                            && owlClassSubClassOfAxiom
                                    .getSubClass() instanceof OWLClass
                            && owlClassReferencingAxiom.getSignature()
                                    .size() == 3) {

                        OWLObjectSomeValuesFrom owlObjectPropertySomeValuesFrom =
                                (OWLObjectSomeValuesFrom) owlClassSubClassOfAxiom
                                        .getSuperClass();
                        String objectPropertyIRI =
                                ((OWLObjectProperty) owlObjectPropertySomeValuesFrom
                                        .getProperty()).getIRI().toString();
                        String superClassIRI =
                                ((OWLClass) owlObjectPropertySomeValuesFrom
                                        .getFiller()).getIRI().toString();
                        if (!classIri.equals(superClassIRI)) {
                            if ( parentClasses.containsKey(superClassIRI) ) {
                                String currentObjectPropertyIRI = 
                                        parentClasses.get(superClassIRI);
                                if ( currentObjectPropertyIRI == null ) {
                                    parentClasses.put(superClassIRI, 
                                            objectPropertyIRI);
                                } else {
                                    parentClasses.put(superClassIRI, 
                                            currentObjectPropertyIRI 
                                                + " " + DELIMITER + " " 
                                                +  objectPropertyIRI);
                                }
                            } else {
                                parentClasses.put(superClassIRI, 
                                        objectPropertyIRI);
                            }
                        }

                    }

                }
            }

            // Set the parent class IRI <> object property IRI mapping
            simpleClass.setParentClasses(parentClasses);

            // Add the new Simple Class object to the map of 
            // Simple Class objects
            simpleClassMap.put(classIri, simpleClass);

        }

        return simpleClassMap;

    }

    /**************************************************************************
     * Axioms
     *************************************************************************/

    public static List<OWLAxiom> getReferencingAxioms(OWLOntology ontology,
            OWLEntity owlEntity) {
        return OWLAPIStreamUtils.asList(
                EntitySearcher.getReferencingAxioms(owlEntity, ontology));
    }
    
    /**************************************************************************
     * Diff
     * @throws IOException 
     *************************************************************************/

    public static SimpleOntologyDiff diff(
            String leftOwlFile, String rightOwlFile ) 
                    throws OWLOntologyCreationException, IOException {
        
        // Load and parse the left OWL ontology
        OWLOntology leftOntology = OWLAPI.loadOntology(new File(leftOwlFile));
        Map<String, SimpleAnnotationProperty> leftSimpleAnnotationPropertyMap =
                OWLAPI.parseAnnotationProperties(leftOntology);
        Map<String, SimpleObjectProperty> leftSimpleObjectPropertyMap =
                OWLAPI.parseObjectProperties(leftOntology);
        Map<String, SimpleClass> leftSimpleClassMap = 
                OWLAPI.parseClasses(leftOntology);
        String leftOntologyXml = OWLRDFXMLAPI.read(leftOwlFile);
        
        // Load and parse the right OWL ontology
        OWLOntology rightOntology = OWLAPI.loadOntology(new File(rightOwlFile));
        Map<String, SimpleAnnotationProperty> rightSimpleAnnotationPropertyMap =
                OWLAPI.parseAnnotationProperties(rightOntology);
        Map<String, SimpleObjectProperty> rightSimpleObjectPropertyMap =
                OWLAPI.parseObjectProperties(rightOntology);
        Map<String, SimpleClass> rightSimpleClassMap = 
                OWLAPI.parseClasses(rightOntology);
        String rightOntologyXml = OWLRDFXMLAPI.read(rightOwlFile);
        
        // Resolve the diffs
        List<List<SimpleAnnotationPropertyDiff>> simpleAnnotationPropertyDiff = 
                simpleAnnotationPropertyDiff(leftSimpleAnnotationPropertyMap, 
                        rightSimpleAnnotationPropertyMap, 
                        leftOntologyXml, rightOntologyXml);
        List<List<SimpleObjectPropertyDiff>> simpleObjectPropertyDiff = 
                simpleObjectPropertyDiff(leftSimpleObjectPropertyMap, 
                        rightSimpleObjectPropertyMap, 
                        leftOntologyXml, rightOntologyXml);
        List<List<SimpleClassDiff>> simpleClassDiff = 
                simpleClassDiff(leftSimpleClassMap, rightSimpleClassMap, 
                        leftOntologyXml, rightOntologyXml);
        
        // Return the diff
        return new SimpleOntologyDiff(
                simpleAnnotationPropertyDiff.get(0), 
                simpleAnnotationPropertyDiff.get(1), 
                simpleAnnotationPropertyDiff.get(2), 
                simpleObjectPropertyDiff.get(0), 
                simpleObjectPropertyDiff.get(1), 
                simpleObjectPropertyDiff.get(2), 
                simpleClassDiff.get(0), 
                simpleClassDiff.get(1), 
                simpleClassDiff.get(2) 
                );
        
    }
    
    /**
     * Resolve simple annotation property diffs
     * @param leftSimpleAnnotationPropertyMap
     * @param rightSimpleAnnotationPropertyMap
     * @return
     */
    
    public static List<List<SimpleAnnotationPropertyDiff>> simpleAnnotationPropertyDiff(
            Map<String, SimpleAnnotationProperty> leftSimpleAnnotationPropertyMap, 
            Map<String, SimpleAnnotationProperty> rightSimpleAnnotationPropertyMap, 
            String leftOntologyXml, String rightOntologyXml) {
        
        // Instantiate diff collections
        List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationProperties = 
                new ArrayList<>();
        List<SimpleAnnotationPropertyDiff> updatedSimpleAnnotationProperties = 
                new ArrayList<>();
        List<SimpleAnnotationPropertyDiff> deletedSimpleAnnotationProperties = 
                new ArrayList<>();
        
        // Iterate over left annotation properties
        for (var entry : leftSimpleAnnotationPropertyMap.entrySet()) {
            String leftIri = entry.getKey();
            SimpleAnnotationProperty leftSimpleAnnotationProperty = 
                    entry.getValue();
            
            // Deletes
            if ( !rightSimpleAnnotationPropertyMap.containsKey(leftIri) )
                deletedSimpleAnnotationProperties.add(
                        new SimpleAnnotationPropertyDiff(
                                leftSimpleAnnotationProperty, 
                                extractXmlNode(leftOntologyXml, 
                                        OWLSchemaVocabulary.ANNOTATION_PROPERTY, 
                                        leftIri), 
                                true));
            
            // Updates
            else {
                SimpleAnnotationProperty rightSimpleAnnotationProperty = 
                        rightSimpleAnnotationPropertyMap.get(leftIri);
                
                // Updated RDFS label
                if ( isLabelUpdated(leftSimpleAnnotationProperty.getLabel(), 
                        rightSimpleAnnotationProperty.getLabel()) )
                    updatedSimpleAnnotationProperties.add(
                            generateUpdatedSimpleAnnotationPropertyDiff(
                                    leftIri, leftSimpleAnnotationProperty, 
                                    leftOntologyXml, rightSimpleAnnotationProperty, 
                                    rightOntologyXml));
                
                else {
                    
                    // Deleted or updated annotation property annotations
                    boolean updatedAnnotation = false;
                    for (var annotationEntry : leftSimpleAnnotationProperty
                            .getAnnotations().entrySet()) {
                        String annotationPropertyIri = 
                                annotationEntry.getKey();
                        String leftAnnotationPropertyValue = 
                                annotationEntry.getValue();
                        
                        // Deleted annotation property annotation
                        if ( !rightSimpleAnnotationProperty.getAnnotations()
                                .containsKey(annotationPropertyIri) ) {
                            updatedSimpleAnnotationProperties.add(
                                    generateUpdatedSimpleAnnotationPropertyDiff(
                                            leftIri, leftSimpleAnnotationProperty, 
                                            leftOntologyXml, rightSimpleAnnotationProperty, 
                                            rightOntologyXml));
                            updatedAnnotation = true;
                            break;
                        }
                        
                        // Updated annotation property annotation
                        else {
                            String rightAnnotationPropertyValue = 
                                    rightSimpleAnnotationProperty.getAnnotations()
                                        .get(annotationPropertyIri);
                            if ( isAnnotationUpdated(
                                    leftAnnotationPropertyValue, 
                                    rightAnnotationPropertyValue) ) {
                                updatedSimpleAnnotationProperties.add(
                                        generateUpdatedSimpleAnnotationPropertyDiff(
                                                leftIri, leftSimpleAnnotationProperty, 
                                                leftOntologyXml, rightSimpleAnnotationProperty, 
                                                rightOntologyXml));
                                updatedAnnotation = true;
                                break;
                            }
                        }
                        
                    }
                    
                    // Created annotation property annotation
                    if (!updatedAnnotation) {
                        for (var annotationEntry : rightSimpleAnnotationProperty
                                .getAnnotations().entrySet()) {
                            String annotationPropertyIri = 
                                    annotationEntry.getKey();
                            if ( !leftSimpleAnnotationProperty.getAnnotations()
                                    .containsKey(annotationPropertyIri) ) {
                                updatedSimpleAnnotationProperties.add(
                                        generateUpdatedSimpleAnnotationPropertyDiff(
                                                leftIri, leftSimpleAnnotationProperty, 
                                                leftOntologyXml, rightSimpleAnnotationProperty, 
                                                rightOntologyXml));
                                break;
                            }
                        }
                    }
                    
                }
                
            }
            
        }
        
        // Iterate over right annotation properties
        for (var entry : rightSimpleAnnotationPropertyMap.entrySet()) {
            String rightIri = entry.getKey();
            SimpleAnnotationProperty rightSimpleAnnotationProperty = 
                    entry.getValue();
            
            // Creates
            if ( !leftSimpleAnnotationPropertyMap.containsKey(rightIri) )
                createdSimpleAnnotationProperties.add(
                        new SimpleAnnotationPropertyDiff(
                                rightSimpleAnnotationProperty, 
                                extractXmlNode(rightOntologyXml, 
                                        OWLSchemaVocabulary.ANNOTATION_PROPERTY, 
                                        rightIri), 
                                false));
            
        }
        
        // Return the diff collections
        List<List<SimpleAnnotationPropertyDiff>> diff = new ArrayList<>();
        diff.add(createdSimpleAnnotationProperties);
        diff.add(updatedSimpleAnnotationProperties);
        diff.add(deletedSimpleAnnotationProperties);
        return diff;
        
    }
    
    /**
     * Resolve simple object property diffs
     * @param leftSimpleObjectPropertyMap
     * @param rightSimpleObjectPropertyMap
     * @return
     */
    
    public static List<List<SimpleObjectPropertyDiff>> simpleObjectPropertyDiff(
            Map<String, SimpleObjectProperty> leftSimpleObjectPropertyMap, 
            Map<String, SimpleObjectProperty> rightSimpleObjectPropertyMap, 
            String leftOntologyXml, String rightOntologyXml) {
        
        // Instantiate diff collections
        List<SimpleObjectPropertyDiff> createdSimpleObjectProperties = 
                new ArrayList<>();
        List<SimpleObjectPropertyDiff> updatedSimpleObjectProperties = 
                new ArrayList<>();
        List<SimpleObjectPropertyDiff> deletedSimpleObjectProperties = 
                new ArrayList<>();
        
        // Iterate over left object properties
        for (var entry : leftSimpleObjectPropertyMap.entrySet()) {
            String leftIri = entry.getKey();
            SimpleObjectProperty leftSimpleObjectProperty = 
                    entry.getValue();
            
            // Deletes
            if ( isObjectPropertyDeleted(leftIri, leftSimpleObjectPropertyMap, 
                    rightSimpleObjectPropertyMap) )
                deletedSimpleObjectProperties.add(
                        new SimpleObjectPropertyDiff(
                                leftSimpleObjectProperty, 
                                extractXmlNode(leftOntologyXml, 
                                        OWLSchemaVocabulary.OBJECT_PROPERTY, 
                                        leftIri), 
                                true));
            
            // Updates
            else {
                SimpleObjectProperty rightSimpleObjectProperty = 
                        rightSimpleObjectPropertyMap.get(leftIri);
                
                // Updated RDFS label
                if ( isLabelUpdated(leftSimpleObjectProperty.getLabel(), 
                        rightSimpleObjectProperty.getLabel()) )
                    updatedSimpleObjectProperties.add(
                            generateUpdatedSimpleObjectPropertyDiff(
                                    leftIri, leftSimpleObjectProperty, 
                                    leftOntologyXml, rightSimpleObjectProperty, 
                                    rightOntologyXml));
                
                // Updated parent object property IRI
                else if ( leftSimpleObjectProperty.getParentObjectPropertyIRI() != null && 
                            rightSimpleObjectProperty.getParentObjectPropertyIRI() != null && 
                            !leftSimpleObjectProperty.getParentObjectPropertyIRI()
                                .equals(rightSimpleObjectProperty
                                        .getParentObjectPropertyIRI()) )
                    updatedSimpleObjectProperties.add(
                            generateUpdatedSimpleObjectPropertyDiff(
                                    leftIri, leftSimpleObjectProperty, 
                                    leftOntologyXml, rightSimpleObjectProperty, 
                                    rightOntologyXml));
                
                else {
                    
                    // Deleted or updated object property annotations
                    boolean updatedAnnotation = false;
                    for (var annotationEntry : leftSimpleObjectProperty
                            .getAnnotations().entrySet()) {
                        String annotationPropertyIri = 
                                annotationEntry.getKey();
                        String leftAnnotationPropertyValue = 
                                annotationEntry.getValue();
                        
                        // Deleted object property annotation
                        if ( !rightSimpleObjectProperty.getAnnotations()
                                .containsKey(annotationPropertyIri) ) {
                            updatedSimpleObjectProperties.add(
                                    generateUpdatedSimpleObjectPropertyDiff(
                                            leftIri, leftSimpleObjectProperty, 
                                            leftOntologyXml, rightSimpleObjectProperty, 
                                            rightOntologyXml));
                            updatedAnnotation = true;
                            break;
                        }
                        
                        // Updated object property annotation
                        else {
                            String rightAnnotationPropertyValue = 
                                    rightSimpleObjectProperty.getAnnotations()
                                        .get(annotationPropertyIri);
                            if ( isAnnotationUpdated(
                                    leftAnnotationPropertyValue, 
                                    rightAnnotationPropertyValue) ) {
                                updatedSimpleObjectProperties.add(
                                        generateUpdatedSimpleObjectPropertyDiff(
                                                leftIri, leftSimpleObjectProperty, 
                                                leftOntologyXml, rightSimpleObjectProperty, 
                                                rightOntologyXml));
                                updatedAnnotation = true;
                                break;
                            }
                        }
                        
                    }
                    
                    // Created annotation property annotation
                    if (!updatedAnnotation) {
                        for (var annotationEntry : rightSimpleObjectProperty
                                .getAnnotations().entrySet()) {
                            String annotationPropertyIri = 
                                    annotationEntry.getKey();
                            if ( !leftSimpleObjectProperty.getAnnotations()
                                    .containsKey(annotationPropertyIri) ) {
                                updatedSimpleObjectProperties.add(
                                        generateUpdatedSimpleObjectPropertyDiff(
                                                leftIri, leftSimpleObjectProperty, 
                                                leftOntologyXml, rightSimpleObjectProperty, 
                                                rightOntologyXml));
                                break;
                            }
                        }
                    }
                    
                }
                
            }
            
        }
        
        // Iterate over right object properties
        for (var entry : rightSimpleObjectPropertyMap.entrySet()) {
            String rightIri = entry.getKey();
            SimpleObjectProperty rightSimpleObjectProperty = 
                    entry.getValue();
            
            // Creates
            if ( !leftSimpleObjectPropertyMap.containsKey(rightIri) )
                createdSimpleObjectProperties.add(
                        new SimpleObjectPropertyDiff(
                                rightSimpleObjectProperty, 
                                extractXmlNode(rightOntologyXml, 
                                        OWLSchemaVocabulary.OBJECT_PROPERTY, 
                                        rightIri), 
                                false));
            
        }
        
        // Return the diff collections
        List<List<SimpleObjectPropertyDiff>> diff = new ArrayList<>();
        diff.add(createdSimpleObjectProperties);
        diff.add(updatedSimpleObjectProperties);
        diff.add(deletedSimpleObjectProperties);
        return diff;
        
    }
    
    /**
     * Resolve simple class diffs
     * @param leftSimpleClassMap
     * @param rightSimpleClassMap
     * @return
     */
    
    public static List<List<SimpleClassDiff>> simpleClassDiff(
            Map<String, SimpleClass> leftSimpleClassMap, 
            Map<String, SimpleClass> rightSimpleClassMap, 
            String leftOntologyXml, String rightOntologyXml) {
        
        // Instantiate diff collections
        List<SimpleClassDiff> createdSimpleClasses = new ArrayList<>();
        List<SimpleClassDiff> updatedSimpleClasses = new ArrayList<>();
        List<SimpleClassDiff> deletedSimpleClasses = new ArrayList<>();
        
        // Iterate over left classes
        for (var entry : leftSimpleClassMap.entrySet()) {
            String leftIri = entry.getKey();
            SimpleClass leftSimpleClass = entry.getValue();
            
            // Deletes
            if ( isClassDeleted(leftIri, leftSimpleClassMap, 
                    rightSimpleClassMap) )
                deletedSimpleClasses.add(
                        new SimpleClassDiff(leftSimpleClass, 
                                extractXmlNode(leftOntologyXml, 
                                        OWLSchemaVocabulary.CLASS, 
                                        leftIri), 
                                true));
            
            // Updates
            else {
                SimpleClass rightSimpleClass = rightSimpleClassMap.get(leftIri);
                
                // Updated RDFS label
                if ( isLabelUpdated(leftSimpleClass.getLabel(), 
                        rightSimpleClass.getLabel()) )
                    updatedSimpleClasses.add(
                            generateUpdatedSimpleClassDiff(
                                    leftIri, leftSimpleClass, leftOntologyXml, 
                                    rightSimpleClass, rightOntologyXml));
                
                else {
                    
                    // Deleted or updated class annotations
                    boolean updatedAnnotationOrParentClass = false;
                    for (var annotationEntry : leftSimpleClass
                            .getAnnotations().entrySet()) {
                        String annotationPropertyIri = 
                                annotationEntry.getKey();
                        String leftAnnotationPropertyValue = 
                                annotationEntry.getValue();
                        
                        // Deleted class annotation
                        if ( !rightSimpleClass.getAnnotations()
                                .containsKey(annotationPropertyIri) ) {
                            updatedSimpleClasses.add(
                                    generateUpdatedSimpleClassDiff(
                                            leftIri, leftSimpleClass, 
                                            leftOntologyXml, rightSimpleClass, 
                                            rightOntologyXml));
                            updatedAnnotationOrParentClass = true;
                            break;
                        }
                        
                        // Updated class annotation
                        else {
                            String rightAnnotationPropertyValue = 
                                    rightSimpleClass.getAnnotations()
                                        .get(annotationPropertyIri);
                            if ( isAnnotationUpdated(
                                    leftAnnotationPropertyValue, 
                                    rightAnnotationPropertyValue) ) {
                                updatedSimpleClasses.add(
                                        generateUpdatedSimpleClassDiff(
                                                leftIri, leftSimpleClass, 
                                                leftOntologyXml, rightSimpleClass, 
                                                rightOntologyXml));
                                updatedAnnotationOrParentClass = true;
                                break;
                            }
                        }
                        
                    }
                    
                    // Created class annotation
                    if (!updatedAnnotationOrParentClass) {
                        for (var annotationEntry : rightSimpleClass
                                .getAnnotations().entrySet()) {
                            String annotationPropertyIri = 
                                    annotationEntry.getKey();
                            if ( !leftSimpleClass.getAnnotations()
                                    .containsKey(annotationPropertyIri) ) {
                                updatedSimpleClasses.add(
                                        generateUpdatedSimpleClassDiff(
                                                leftIri, leftSimpleClass, 
                                                leftOntologyXml, rightSimpleClass, 
                                                rightOntologyXml));
                                updatedAnnotationOrParentClass = true;
                                break;
                            }
                        }
                    }
                    
                    // Deleted or updated parent class relationships
                    if (!updatedAnnotationOrParentClass) {
                        for (var parentEntry : leftSimpleClass
                                .getParentClasses().entrySet()) {
                            String parentClassIri = 
                                    parentEntry.getKey();
                            String leftParentClassRestrictionIri = 
                                    parentEntry.getValue();
                            
                            // Deleted parent class relationship
                            if ( !rightSimpleClass.getParentClasses()
                                    .containsKey(parentClassIri) ) {
                                updatedSimpleClasses.add(
                                        generateUpdatedSimpleClassDiff(
                                                leftIri, leftSimpleClass, 
                                                leftOntologyXml, rightSimpleClass, 
                                                rightOntologyXml));
                                updatedAnnotationOrParentClass = true;
                                break;
                            }
                            
                            // Updated parent class relationship
                            else {
                                String rightParentClassRestrictionIri = 
                                        rightSimpleClass.getParentClasses()
                                            .get(parentClassIri);
                                if ( isParentClassRelationshipUpdated(
                                        leftParentClassRestrictionIri, 
                                        rightParentClassRestrictionIri) ) {
                                    updatedSimpleClasses.add(
                                            generateUpdatedSimpleClassDiff(
                                                    leftIri, leftSimpleClass, 
                                                    leftOntologyXml, rightSimpleClass, 
                                                    rightOntologyXml));
                                    updatedAnnotationOrParentClass = true;
                                    break;
                                }
                            }
                            
                        }
                    }
                    
                    // Created parent class relationships
                    if (!updatedAnnotationOrParentClass) {
                        for (var parentEntry : rightSimpleClass
                                .getParentClasses().entrySet()) {
                            String parentClassIri = 
                                    parentEntry.getKey();
                            if ( !leftSimpleClass.getParentClasses()
                                    .containsKey(parentClassIri) ) {
                                updatedSimpleClasses.add(
                                        generateUpdatedSimpleClassDiff(
                                                leftIri, leftSimpleClass, 
                                                leftOntologyXml, rightSimpleClass, 
                                                rightOntologyXml));
                                break;
                            }
                        }
                    }
                    
                }
                
            }
            
        }
        
        // Iterate over right classes
        for (var entry : rightSimpleClassMap.entrySet()) {
            String rightIri = entry.getKey();
            SimpleClass rightSimpleClass = entry.getValue();
            
            // Creates
            if ( !leftSimpleClassMap.containsKey(rightIri) )
                createdSimpleClasses.add(
                        new SimpleClassDiff(rightSimpleClass, 
                                extractXmlNode(rightOntologyXml, 
                                        OWLSchemaVocabulary.CLASS, 
                                        rightIri), 
                                false));
            
        }
        
        // Return the diff collections
        List<List<SimpleClassDiff>> diff = new ArrayList<>();
        diff.add(createdSimpleClasses);
        diff.add(updatedSimpleClasses);
        diff.add(deletedSimpleClasses);
        return diff;
        
    }
    
    /**
     * Ascertain whether the RDFS label has been updated
     * @param leftLabel
     * @param rightLabel
     * @return
     */
    
    public static boolean isLabelUpdated(String leftLabel, String rightLabel) {
        return (leftLabel != null && rightLabel == null) || 
        (leftLabel == null && rightLabel != null) || 
        (leftLabel != null && rightLabel != null && 
            !leftLabel.equals(rightLabel));
    }
    
    /**
     * Ascertain whether an annotation property has been updated
     * @param leftAnnotationPropertyValue
     * @param rightAnnotationPropertyValue
     * @return
     */
    
    public static boolean isAnnotationUpdated(
            String leftAnnotationPropertyValue, 
            String rightAnnotationPropertyValue) {
        boolean updated = false;
        if ( leftAnnotationPropertyValue != null && 
                rightAnnotationPropertyValue == null )
            updated = true;
        else if ( leftAnnotationPropertyValue == null && 
                rightAnnotationPropertyValue != null )
            updated = true;
        else if ( leftAnnotationPropertyValue != null && 
                rightAnnotationPropertyValue != null ) {
            List<String> leftAnnotationPropertyValues = 
                    Arrays.asList(leftAnnotationPropertyValue
                            .replace(" " + DELIMITER + " ", DELIMITER)
                            .split("\\" + DELIMITER));
            List<String> rightAnnotationPropertyValues = 
                    Arrays.asList(rightAnnotationPropertyValue
                            .replace(" " + DELIMITER + " ", DELIMITER)
                            .split("\\" + DELIMITER));
            for ( String currentLeftAnnotationPropertyValue : 
                leftAnnotationPropertyValues ) {
                if ( !rightAnnotationPropertyValues.contains(
                        currentLeftAnnotationPropertyValue) ) {
                    updated = true;
                    break;
                }
            }
        }
        return updated;
    }
    
    /**
     * Ascertain whether an object property has been deleted
     * @param leftIri
     * @param leftSimpleObjectPropertyMap
     * @param rightSimpleObjectPropertyMap
     * @return
     */
    
    public static boolean isObjectPropertyDeleted(
            String leftIri, 
            Map<String, SimpleObjectProperty> leftSimpleObjectPropertyMap, 
            Map<String, SimpleObjectProperty> rightSimpleObjectPropertyMap) {
        return (!rightSimpleObjectPropertyMap.containsKey(leftIri) ) || 
                ( rightSimpleObjectPropertyMap.containsKey(leftIri) && 
                        rightSimpleObjectPropertyMap.get(leftIri)
                            .getLabel() == null && 
                        rightSimpleObjectPropertyMap.get(leftIri)
                            .getAnnotations().isEmpty() && 
                        rightSimpleObjectPropertyMap.get(leftIri)
                            .getParentObjectPropertyIRI() == null && ( 
                            leftSimpleObjectPropertyMap.get(leftIri)
                                .getLabel() != null || 
                            !leftSimpleObjectPropertyMap.get(leftIri)
                                .getAnnotations().isEmpty() ||
                            leftSimpleObjectPropertyMap.get(leftIri)
                                 .getParentObjectPropertyIRI() != null ) );
    }
    
    /**
     * Ascertain whether a class has been deleted
     * @param leftIri
     * @param leftSimpleClassMap
     * @param rightSimpleClassMap
     * @return
     */
    
    public static boolean isClassDeleted(
            String leftIri, 
            Map<String, SimpleClass> leftSimpleClassMap, 
            Map<String, SimpleClass> rightSimpleClassMap) {
        return (!rightSimpleClassMap.containsKey(leftIri)) || 
                ( rightSimpleClassMap.containsKey(leftIri) && 
                        rightSimpleClassMap.get(leftIri).getLabel() == null && 
                        rightSimpleClassMap.get(leftIri).getAnnotations()
                            .isEmpty() && 
                        rightSimpleClassMap.get(leftIri).getParentClasses()
                            .isEmpty() && 
                        ( leftSimpleClassMap.get(leftIri).getLabel() != null || 
                          !leftSimpleClassMap.get(leftIri).getAnnotations()
                              .isEmpty() || 
                          !leftSimpleClassMap.get(leftIri).getParentClasses()
                              .isEmpty() ) );
    }
    
    /**
     * Ascertain whether a parent class relationship has been updated
     * @param leftParentClassRestrictionIri
     * @param rightParentClassRestrictionIri
     * @return
     */
    
    public static boolean isParentClassRelationshipUpdated(
            String leftParentClassRestrictionIri, 
            String rightParentClassRestrictionIri) {
        boolean updated = false;
        if ( leftParentClassRestrictionIri != null && 
                rightParentClassRestrictionIri == null )
            updated = true;
        else if ( leftParentClassRestrictionIri == null && 
                rightParentClassRestrictionIri != null )
            updated = true;
        else if ( leftParentClassRestrictionIri != null && 
                rightParentClassRestrictionIri != null ) {
            List<String> leftParentClassRestrictionIris = 
                    Arrays.asList(leftParentClassRestrictionIri
                            .replace(" " + DELIMITER + " ", DELIMITER)
                            .split("\\" + DELIMITER));
            List<String> rightParentClassRestrictionIris = 
                    Arrays.asList(rightParentClassRestrictionIri
                            .replace(" " + DELIMITER + " ", DELIMITER)
                            .split("\\" + DELIMITER));
            for ( String currentLeftParentClassRestrictionIri : 
                leftParentClassRestrictionIris ) {
                if ( !rightParentClassRestrictionIris.contains(
                        currentLeftParentClassRestrictionIri) ) {
                    updated = true;
                    break;
                }
            }
        }
        return updated;
    }
    
    /**
     * Extract a node from the full OWL RDF XML given the IRI 
     * and OWL 2 object type
     * @param fullXml
     * @param owlSchemaVocabulary
     * @param iri
     * @return
     */
    
    public static String extractXmlNode(String fullXml, 
            OWLSchemaVocabulary owlSchemaVocabulary, String iri) {
        return OWLRDFXMLAPI.getNodeAsString(fullXml, owlSchemaVocabulary, iri)
                .replace("\n", "")
                .replace("\r", "")
                .replace("\t", "")
                .replaceAll(" +", " ")
                .replace("> <", "><")
                .strip();
    }
    
    /**
     * Generate a new SimpleAnnotationPropertyDiff for an updated
     * annotation property
     * @param iri
     * @param leftSimpleAnnotationProperty
     * @param leftOntologyXml
     * @param rightSimpleAnnotationProperty
     * @param rightOntologyXml
     * @return
     */
    
    public static SimpleAnnotationPropertyDiff generateUpdatedSimpleAnnotationPropertyDiff(
            String iri, 
            SimpleAnnotationProperty leftSimpleAnnotationProperty, 
            String leftOntologyXml, 
            SimpleAnnotationProperty rightSimpleAnnotationProperty, 
            String rightOntologyXml) {
        return new SimpleAnnotationPropertyDiff(
                leftSimpleAnnotationProperty, 
                extractXmlNode(leftOntologyXml, 
                        OWLSchemaVocabulary.ANNOTATION_PROPERTY, iri), 
                rightSimpleAnnotationProperty, 
                extractXmlNode(rightOntologyXml, 
                        OWLSchemaVocabulary.ANNOTATION_PROPERTY, iri));
    }
    
    /**
     * Generate a new SimpleObjectPropertyDiff for an updated
     * object property
     * @param iri
     * @param leftSimpleObjectProperty
     * @param leftOntologyXml
     * @param rightSimpleObjectProperty
     * @param rightOntologyXml
     * @return
     */
    
    public static SimpleObjectPropertyDiff generateUpdatedSimpleObjectPropertyDiff(
            String iri, 
            SimpleObjectProperty leftSimpleObjectProperty, 
            String leftOntologyXml, 
            SimpleObjectProperty rightSimpleObjectProperty, 
            String rightOntologyXml) {
        return new SimpleObjectPropertyDiff(
                leftSimpleObjectProperty, 
                extractXmlNode(leftOntologyXml, 
                        OWLSchemaVocabulary.OBJECT_PROPERTY, iri), 
                rightSimpleObjectProperty, 
                extractXmlNode(rightOntologyXml, 
                        OWLSchemaVocabulary.OBJECT_PROPERTY, iri));
    }
    
    /**
     * Generate a new SimpleObjectPropertyDiff for an updated class
     * @param iri
     * @param leftSimpleClass
     * @param leftOntologyXml
     * @param rightSimpleClass
     * @param rightOntologyXml
     * @return
     */
    
    public static SimpleClassDiff generateUpdatedSimpleClassDiff(
            String iri, 
            SimpleClass leftSimpleClass, 
            String leftOntologyXml, 
            SimpleClass rightSimpleClass, 
            String rightOntologyXml) {
        return new SimpleClassDiff(
                leftSimpleClass, 
                extractXmlNode(leftOntologyXml, 
                        OWLSchemaVocabulary.CLASS, iri), 
                rightSimpleClass, 
                extractXmlNode(rightOntologyXml, 
                        OWLSchemaVocabulary.CLASS, iri));
    }
    
}
