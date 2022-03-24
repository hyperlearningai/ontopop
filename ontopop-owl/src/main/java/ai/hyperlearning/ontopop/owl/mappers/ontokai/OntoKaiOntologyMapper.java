package ai.hyperlearning.ontopop.owl.mappers.ontokai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.WordUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.google.common.collect.Maps;

import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNode;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNodeAttribute;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNodeRelationship;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyPayload;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleAnnotationPropertyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleClassDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleObjectPropertyDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;
import ai.hyperlearning.ontopop.owl.OWLRDFXMLAPI;
import ai.hyperlearning.ontopop.rdf.StandardRDFSchema;

/**
 * OntoKai to OWL mapper helper methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyMapper {
    
    private static final String DEFAULT_IRI_PREFIX = 
            "http://webprotege.stanford.edu/R";
    private static final int DEFAULT_IRI_SUFFIX_LENGTH = 22;
    private static final String RDF_SCHEMA_LABEL_IRI = 
            "http://www.w3.org/2000/01/rdf-schema#label";
    private static final String SUBCLASS_OF_OBJECT_PROPERTY_RDFS_LABEL = 
            "SUBCLASS OF";
    private static final String ONTOKAI_NODE_CLASS_CATEGORY_VALUE = 
            "CLASS";
    private static final String ONTOKAI_SUBCLASS_OF_TYPE_VALUE = 
            "SUBCLASS_OF";
    private static final String ONTOKAI_RESTRICTION_BUNDLE_TYPE_VALUE = 
            "SOME";
    
    private OntoKaiOntologyMapper() {
        throw new IllegalStateException("The OntoKaiOntologyMapper "
                + "utility class cannot be instantiated.");
    }
    
    /**
     * Generate a SimpleOntologyDiff object given an OntoKai ontology
     * payload object and the latest existing SimpleOntology object
     * for a given ontology ID. This new SimpleOntologyDiff object will contain
     * newly created SimpleAnnotationProperty objects, 
     * newly created SimpleObjectProperty objects, 
     * and created/updated/deleted SimpleClass objects.
     * @param ontoKaiOntologyPayload
     * @param latestExistingOntology
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static SimpleOntologyDiff generateSimpleOntologyDiff(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) 
                    throws OWLOntologyCreationException, IOException {
        
        // Identify new annotation properties
        Map<String, SimpleAnnotationProperty> newAnnotationProperties = 
                identifyNewAnnotationProperties(
                        ontoKaiOntologyPayload, simpleOntology);
        List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationPropertyDiffs = 
                toSimpleAnnotationPropertyDiffs(newAnnotationProperties);
        
        // Identify new object properties
        Map<String, SimpleObjectProperty> newObjectProperties = 
                identifyNewObjectProperties(
                        ontoKaiOntologyPayload, simpleOntology);
        List<SimpleObjectPropertyDiff> createdSimpleObjectPropertyDiffs = 
                toSimpleObjectPropertyDiffs(newObjectProperties);
        
        // Identify new classes
        List<Map<String, SimpleClass>> classDiffs = identifyClassDiffs(
                ontoKaiOntologyPayload, simpleOntology, 
                newAnnotationProperties, newObjectProperties);
        Map<String, SimpleClass> newClasses = classDiffs.get(0);
        List<SimpleClassDiff> createdSimpleClassDiffs = 
                toCreatedSimpleClassDiffs(newClasses);
        
        // Identify updated classes
        Map<String, SimpleClass> updatedClasses = classDiffs.get(1);
        List<SimpleClassDiff> updatedSimpleClassDiffs = 
                toUpdatedSimpleClassDiffs(updatedClasses);
        
        // Identify deleted classes
        Map<String, SimpleClass> deletedClasses = classDiffs.get(2);
        List<SimpleClassDiff> deletedSimpleClassDiffs = 
                toDeletedSimpleClassDiffs(deletedClasses);
        
        // Generate the SimpleOntologyDiff object
        SimpleOntologyDiff simpleOntologyDiff = new SimpleOntologyDiff();
        simpleOntologyDiff.setCreatedSimpleAnnotationProperties(
                createdSimpleAnnotationPropertyDiffs);
        simpleOntologyDiff.setCreatedSimpleObjectProperties(
                createdSimpleObjectPropertyDiffs);
        simpleOntologyDiff.setCreatedSimpleClasses(createdSimpleClassDiffs);
        simpleOntologyDiff.setUpdatedSimpleClasses(updatedSimpleClassDiffs);
        simpleOntologyDiff.setDeletedSimpleClasses(deletedSimpleClassDiffs);
        
        return simpleOntologyDiff;
        
    }
    
    /**
     * Generate a mapping between OntoKai node ID and the OntoKai
     * node object
     * @param ontoKaiOntologyPayload
     * @return
     */
    
    public static Map<Integer, OntoKaiOntologyNode> generateNodeMap(
            OntoKaiOntologyPayload ontoKaiOntologyPayload) {
        return Maps.uniqueIndex(ontoKaiOntologyPayload.getNodes(), 
                OntoKaiOntologyNode::getId);
    }
    
    /**
     * Identify any new annotation properties in the OntoKai ontology
     * payload object. If new annotation properties are found,
     * create SimpleAnnotationProperty object representations of them and 
     * return a mapping between the label and the SimpleAnnotationProperty.
     * @param ontoKaiOntologyPayload
     * @param latestExistingOntology
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static Map<String, SimpleAnnotationProperty> identifyNewAnnotationProperties(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) 
                    throws OWLOntologyCreationException, IOException {
        
        // Instantiate a map of new SimpleAnnotationProperty objects
        Set<String> newSimpleAnnotationPropertyLabels = new HashSet<>();
        Map<String, SimpleAnnotationProperty> newSimpleAnnotationProperties = 
                new HashMap<>();
        
        // Get a list of unique standard schema annotation property labels
        Set<String> standardSchemaAnnotationPropertyLabels = StandardRDFSchema
                .getUniqueStandardSchemaAnnotationPropertyLabels();
        
        // Get a list of unique existing annotation property labels
        Set<String> uniqueExistingSimpleAnnotationPropertyLabels = 
                simpleOntology.getUniqueSimpleAnnotationPropertyLabels();
        
        // Iterate over all the nodes in the OntoKai ontology payload object
        for ( OntoKaiOntologyNode node : ontoKaiOntologyPayload.getNodes() ) {
            
            // Iterate over all the attributes for this node
            for ( OntoKaiOntologyNodeAttribute attribute : node.getAttributes() ) {
                
                // Check whether the attribute name already exists
                // as an annotation property in the given SimpleOntology
                String attributeName = attribute.getName().strip();
                if ( !standardSchemaAnnotationPropertyLabels.contains(
                        attributeName.toUpperCase()) && 
                        !uniqueExistingSimpleAnnotationPropertyLabels.contains(
                                attributeName.toUpperCase()) && 
                        !newSimpleAnnotationPropertyLabels.contains(
                                attributeName.toUpperCase()) ) {
                    
                    // Create a new SimpleAnnotationProperty object
                    SimpleAnnotationProperty newSimpleAnnotationProperty = 
                            new SimpleAnnotationProperty();
                    newSimpleAnnotationProperty.setIri(
                            generateIri(simpleOntology));
                    newSimpleAnnotationProperty.setLabel(attributeName);
                    Map<String, String> annotations = new LinkedHashMap<>();
                    annotations.put(RDF_SCHEMA_LABEL_IRI, attributeName);
                    newSimpleAnnotationProperty.setAnnotations(annotations);
                    newSimpleAnnotationPropertyLabels.add(attributeName.toUpperCase());
                    newSimpleAnnotationProperties.put(
                            attributeName.toUpperCase(), 
                            newSimpleAnnotationProperty);
                    
                }
                
            }
            
        }
        
        return newSimpleAnnotationProperties;
        
    }
    
    /**
     * Convert a given map of identified new annotation properties into
     * a list of SimpleAnnotationPropertyDiff objects
     * @param newAnnotationProperties
     * @return
     */
    
    public static List<SimpleAnnotationPropertyDiff> toSimpleAnnotationPropertyDiffs(
            Map<String, SimpleAnnotationProperty> newAnnotationProperties) {
        List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationPropertyDiffs = 
                new ArrayList<>();
        for ( SimpleAnnotationProperty simpleAnnotationProperty : 
                newAnnotationProperties.values()) {
            SimpleAnnotationPropertyDiff simpleAnnotationPropertyDiff = 
                    new SimpleAnnotationPropertyDiff();
            simpleAnnotationPropertyDiff.setAfter(simpleAnnotationProperty);
            simpleAnnotationPropertyDiff.setAfterXml(OWLRDFXMLAPI
                    .generateNewOwlAnnotationPropertyXml(simpleAnnotationProperty));
            createdSimpleAnnotationPropertyDiffs.add(simpleAnnotationPropertyDiff);
        }
        return createdSimpleAnnotationPropertyDiffs;
    }
    
    /**
     * Identify new object properties in the OntoKai ontology payload object. 
     * If new object properties are found, create SimpleObjectProperty object
     * representations of them and return a mapping between the label and
     * the SimpleObjectProperty.
     * @param ontoKaiOntologyPayload
     * @param simpleOntology
     * @return
     */
    
    public static Map<String, SimpleObjectProperty> identifyNewObjectProperties(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology) {
        
        // Instantiate a map of new SimpleObjectProperty objects
        Set<String> newSimpleObjectPropertyLabels = new HashSet<>();
        Map<String, SimpleObjectProperty> newSimpleObjectProperties = 
                new HashMap<>();
        
        // Get a list of unique existing object property labels
        Set<String> uniqueSimpleObjectPropertyLabels = 
                simpleOntology.getUniqueSimpleObjectPropertyLabels();
        
        // Iterate over all the nodes in the OntoKai ontology payload object
        for ( OntoKaiOntologyNode node : ontoKaiOntologyPayload.getNodes() ) {
            
            // Iterate over all the relationships for this node
            for ( OntoKaiOntologyNodeRelationship relationship : 
                node.getRelationships() ) {
                
                // Check whether the type value already exists
                // as an object property in the given SimpleOntology
                String typeTransformed = relationship.getType().strip()
                        .toUpperCase().replace("_", " ");
                if ( !typeTransformed.equalsIgnoreCase(
                        SUBCLASS_OF_OBJECT_PROPERTY_RDFS_LABEL) && 
                        !uniqueSimpleObjectPropertyLabels.contains(typeTransformed) &&
                        !newSimpleObjectPropertyLabels.contains(typeTransformed) ) {
                    
                    // Create a new SimpleObjectProperty object
                    SimpleObjectProperty newSimpleObjectProperty = 
                            new SimpleObjectProperty();
                    newSimpleObjectProperty.setIri(
                            generateIri(simpleOntology));
                    newSimpleObjectProperty.setLabel(
                            WordUtils.capitalize(typeTransformed.toLowerCase()));
                    Map<String, String> annotations = new LinkedHashMap<>();
                    annotations.put(RDF_SCHEMA_LABEL_IRI, 
                            WordUtils.capitalize(typeTransformed.toLowerCase()));
                    newSimpleObjectProperty.setAnnotations(annotations);
                    newSimpleObjectPropertyLabels.add(typeTransformed);
                    newSimpleObjectProperties.put(
                            typeTransformed, 
                            newSimpleObjectProperty);
                    
                }
                
            }
            
        }
        
        return newSimpleObjectProperties;
        
    }
    
    /**
     * Convert a given map of identified new object properties into
     * a list of SimpleObjectPropertyDiff objects
     * @param newObjectProperties
     * @return
     */
    
    public static List<SimpleObjectPropertyDiff> toSimpleObjectPropertyDiffs(
            Map<String, SimpleObjectProperty> newObjectProperties) {
        List<SimpleObjectPropertyDiff> createdSimpleObjectPropertyDiffs = 
                new ArrayList<>();
        for ( SimpleObjectProperty simpleObjectProperty : 
            newObjectProperties.values()) {
            SimpleObjectPropertyDiff simpleObjectPropertyDiff = 
                    new SimpleObjectPropertyDiff();
            simpleObjectPropertyDiff.setAfter(simpleObjectProperty);
            simpleObjectPropertyDiff.setAfterXml(OWLRDFXMLAPI
                    .generateNewOwlObjectPropertyXml(simpleObjectProperty));
            createdSimpleObjectPropertyDiffs.add(simpleObjectPropertyDiff);
        }
        return createdSimpleObjectPropertyDiffs;
    }
    
    /**
     * Identify classes that have been created, updated and deleted by 
     * comparing the OntoKai ontology payload object with the latest
     * existing SimpleOntology object. The first map in the list returned 
     * contains created classes, the second map contains updated classes, 
     * and the third map contains deleted classes.
     * @param ontoKaiOntologyPayload
     * @param simpleOntology
     * @param newAnnotationProperties
     * @param newObjectProperties
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static List<Map<String, SimpleClass>> identifyClassDiffs(
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology, 
            Map<String, SimpleAnnotationProperty> newAnnotationProperties, 
            Map<String, SimpleObjectProperty> newObjectProperties) 
                    throws OWLOntologyCreationException, IOException {
        
        // Instantiate maps that will contain created, updated and deleted classes
        Map<String, SimpleClass> createdClasses = new HashMap<>();
        Map<String, SimpleClass> updatedClasses = new HashMap<>();
        Map<String, SimpleClass> deletedClasses = new HashMap<>();
        
        // Join the existing and new annotation property maps
        Map<String, SimpleAnnotationProperty> allSimpleAnnotationProperties = 
                new HashMap<>(simpleOntology
                        .getUniqueSimpleAnnotationPropertyLabelsMap());
        allSimpleAnnotationProperties.putAll(newAnnotationProperties);
        allSimpleAnnotationProperties.putAll(StandardRDFSchema
                .getLabelStandardSchemaAnnotationPropertyMap());
        
        // Join the existing and new object property maps
        Map<String, SimpleObjectProperty> allSimpleObjectProperties = 
                new HashMap<>(simpleOntology.getUniqueSimpleObjectPropertyLabelsMap());
        allSimpleObjectProperties.putAll(newObjectProperties);
        
        // Generate a mapping between OntoKai node ID and class IRI
        Map<Integer, String> ontoKaiNodeIdIriMap = ontoKaiOntologyPayload
                .generateNodeIdIriMap();
        
        /**********************************************************************
         * DELETED CLASSES
         *********************************************************************/
        
        // Iterate over all existing classes
        for (String existingClassIri : 
            simpleOntology.getSimpleClassMap().keySet()) {
            
            // Iterate over all nodes in the OntoKai ontology payload object
            boolean existingClassIriFound = false;
            for ( OntoKaiOntologyNode node : ontoKaiOntologyPayload.getNodes() ) {
                if ( node.getUrl().equalsIgnoreCase(existingClassIri) ) {
                    existingClassIriFound = true;
                    break;
                }
            }
            
            // If the existing class IRI has not been found in the 
            // OntoKai ontology payload object, then we assume that it 
            // has been deleted.
            if ( !existingClassIriFound )
                deletedClasses.put(existingClassIri, 
                        simpleOntology.getSimpleClassMap()
                            .get(existingClassIri));
            
        }
        
        /**********************************************************************
         * CREATED AND UPDATED CLASSES
         *********************************************************************/
        
        // Iterate over all nodes in the OntoKai ontology payload object
        for ( OntoKaiOntologyNode node : ontoKaiOntologyPayload.getNodes() ) {
            if ( node.getCategory() != null && node.getCategory()
                    .equalsIgnoreCase(ONTOKAI_NODE_CLASS_CATEGORY_VALUE) ) {
            
                // Iterate over all existing classes
                boolean ontoKaiNodeFound = false;
                for (Map.Entry<String, SimpleClass> entry : 
                    simpleOntology.getSimpleClassMap().entrySet()) {
                    if ( entry.getKey().equalsIgnoreCase(node.getUrl()) ) {
                        ontoKaiNodeFound = true;
                        break;
                    }
                }
                
                // Created classes
                if ( !ontoKaiNodeFound )
                    createdClasses.put(node.getUrl(), toSimpleClass(node, 
                            ontoKaiOntologyPayload, simpleOntology, 
                            newAnnotationProperties, newObjectProperties));
                
                // Check whether the class has been updated
                else {
                    
                    // Get the existing class
                    SimpleClass existingClass = simpleOntology
                            .getSimpleClassMap().get(node.getUrl());
                    
                    /**********************************************************
                     * ATTRIBUTES
                     *********************************************************/
                    
                    boolean deletedAttributeExists = false;
                    boolean createdAttributeExists = false;
                    boolean updateAttributeExists = false;
                    
                    // Generate a map of annotation property IRI to annotation
                    // literal value for the current OntoKai node
                    Map<String, String> currentNodeAnnotationIriValueMap = 
                            node.generateAttributeIriValueMap(
                                    allSimpleAnnotationProperties);
                    
                    // Check for deleted attributes
                    for ( String existingAnnotationIri : 
                        existingClass.getAnnotations().keySet() ) {
                        if ( !currentNodeAnnotationIriValueMap.containsKey(
                                existingAnnotationIri) ) {
                            deletedAttributeExists = true;
                            break;
                        }
                    }
                    
                    // Check for created or updated attributes
                    if ( !deletedAttributeExists ) {
                        
                        for (Map.Entry<String, String> entry : 
                            currentNodeAnnotationIriValueMap.entrySet()) {
                            
                            // Check for created attributes
                            if ( !existingClass.getAnnotations().containsKey(
                                    entry.getKey()) ) {
                                createdAttributeExists = true;
                                break;
                            }
                            
                            // Check for updated attributes
                            else {
                                
                                String currentNodeAnnotationValue = entry.getValue();
                                String existingClassAnnotationValue = 
                                        existingClass.getAnnotations().get(entry.getKey());
                                if ( !currentNodeAnnotationValue.equalsIgnoreCase(
                                        existingClassAnnotationValue) ) {
                                    updateAttributeExists = true;
                                    break;
                                }
                                
                            }
                            
                        }
                        
                    }
                    
                    // Attribute changes found
                    if ( deletedAttributeExists || createdAttributeExists 
                            || updateAttributeExists )
                        updatedClasses.put(node.getUrl(), toSimpleClass(node, 
                                ontoKaiOntologyPayload, simpleOntology, 
                                newAnnotationProperties, newObjectProperties));
                    
                    else {
                        
                        /******************************************************
                         * RELATIONSHIPS
                         *****************************************************/
                        
                        boolean deletedRelationshipExists = false;
                        boolean createdRelationshipExists = false;
                        boolean updateRelationshipExists = false;
                        
                        // Generate a map of parent class IRI to object property
                        // IRI for the current OntoKai node relationships
                        Map<String, String> currentNodeRelationshipsMap = 
                                node.generateParentClassIriPropertyRestrictionIriMap(
                                        ontoKaiNodeIdIriMap, 
                                        allSimpleObjectProperties);
                        
                        // Check for deleted relationships
                        for ( String existingClassParentClassIri : 
                            existingClass.getParentClasses().keySet() ) {
                            if ( !currentNodeRelationshipsMap.containsKey(
                                    existingClassParentClassIri) ) {
                                deletedRelationshipExists = true;
                                break;
                            }
                        }
                        
                        // Check for created or updated relationships
                        if ( !deletedRelationshipExists ) {
                            
                            for (Map.Entry<String, String> entry : 
                                currentNodeRelationshipsMap.entrySet()) {
                                
                                // Check for created relationships
                                if ( !existingClass.getParentClasses()
                                        .containsKey(entry.getKey()) ) {
                                    createdRelationshipExists = true;
                                    break;
                                }
                                
                                // Check for updated relationships
                                else {
                                    
                                    String currentNodeRelationshipRestrictionIri = 
                                            entry.getValue();
                                    String existingClassRelationshipRestrictionIri = 
                                            existingClass.getParentClasses()
                                                .get(entry.getKey());
                                    if ( (currentNodeRelationshipRestrictionIri == null 
                                            && existingClassRelationshipRestrictionIri != null) ||
                                         (currentNodeRelationshipRestrictionIri != null 
                                            && existingClassRelationshipRestrictionIri == null) ||
                                         (currentNodeRelationshipRestrictionIri != null
                                                 && existingClassRelationshipRestrictionIri != null 
                                                 && !currentNodeRelationshipRestrictionIri
                                                     .equalsIgnoreCase(
                                                             existingClassRelationshipRestrictionIri)) ) {
                                        updateRelationshipExists = true;
                                        break;
                                    }  
                                    
                                }
                                
                                
                            }
                            
                        }
                        
                        // Relationship changes found
                        if ( deletedRelationshipExists || createdRelationshipExists 
                                || updateRelationshipExists )
                            updatedClasses.put(node.getUrl(), toSimpleClass(node, 
                                    ontoKaiOntologyPayload, simpleOntology, 
                                    newAnnotationProperties, newObjectProperties));
                        
                    }
                    
                }
                
            }
            
        }
        
        // Return the list of maps
        return Arrays.asList(createdClasses, updatedClasses, deletedClasses);
        
    }
    
    /**
     * Parse and convert an OntoKaiOntologyNode object into a 
     * SimpleClass object.
     * @param node
     * @return
     * @throws IOException 
     * @throws OWLOntologyCreationException 
     */
    
    public static SimpleClass toSimpleClass(OntoKaiOntologyNode node, 
            OntoKaiOntologyPayload ontoKaiOntologyPayload, 
            SimpleOntology simpleOntology, 
            Map<String, SimpleAnnotationProperty> newAnnotationProperties, 
            Map<String, SimpleObjectProperty> newObjectProperties) 
                    throws OWLOntologyCreationException, IOException {
        
        // Set the IRI and RDFS label
        SimpleClass simpleClass = new SimpleClass();
        simpleClass.setIri(node.getUrl());
        simpleClass.setLabel(node.getLabel());
        
        /**********************************************************************
         * ATTRIBUTES
         *********************************************************************/
        
        // Join the existing and new annotation property maps
        Map<String, SimpleAnnotationProperty> allSimpleAnnotationProperties = 
                new HashMap<>(simpleOntology.getUniqueSimpleAnnotationPropertyLabelsMap());
        allSimpleAnnotationProperties.putAll(newAnnotationProperties);
        allSimpleAnnotationProperties.putAll(StandardRDFSchema
                .getLabelStandardSchemaAnnotationPropertyMap());
        
        // Iterate over all the node attributes
        Map<String, String> annotations = new LinkedHashMap<>();
        for ( OntoKaiOntologyNodeAttribute attribute : node.getAttributes() ) {
            
            // Create a new annotation mapping
            String attributeName = attribute.getName().strip();
            String annotationPropertyIri = allSimpleAnnotationProperties
                    .containsKey(attributeName.toUpperCase()) ? 
                            allSimpleAnnotationProperties
                                .get(attributeName.toUpperCase()).getIri() : 
                                    null;
            if ( annotationPropertyIri != null )
                annotations.put(annotationPropertyIri, attribute.getValue());
            
        }
        
        // Set the annotations
        simpleClass.setAnnotations(annotations);
        
        /**********************************************************************
         * RELATIONSHIPS
         *********************************************************************/
        
        // Join the existing and new object property maps
        Map<String, SimpleObjectProperty> allSimpleObjectProperties = 
                new HashMap<>(simpleOntology.getUniqueSimpleObjectPropertyLabelsMap());
        allSimpleObjectProperties.putAll(newObjectProperties);
        
        // Generate a mapping between OntoKai node ID and class IRI
        Map<Integer, String> ontoKaiNodeIdIriMap = ontoKaiOntologyPayload
                .generateNodeIdIriMap();
        
        // Iterate over all the node relationships
        Map<String, String> parentClasses = new LinkedHashMap<>();
        for ( OntoKaiOntologyNodeRelationship relationship : 
            node.getRelationships() ) {
            
            // Create a new parent class mapping
            String parentClassIri = ontoKaiNodeIdIriMap.containsKey(
                    relationship.getParentId()) ? 
                            ontoKaiNodeIdIriMap.get(relationship.getParentId()) : 
                                null;
            if ( parentClassIri != null || ( relationship.getBundleType() != null && 
                    relationship.getBundleType().equalsIgnoreCase(
                            ONTOKAI_RESTRICTION_BUNDLE_TYPE_VALUE) ) ) {
                
                // If there is NO object property restriction
                if ( parentClassIri != null && 
                        relationship.getType().equalsIgnoreCase(
                                ONTOKAI_SUBCLASS_OF_TYPE_VALUE) )
                    parentClasses.put(parentClassIri, null);
                
                // If there IS an object property restriction
                else {
                    if ( relationship.getBundleType() != null && 
                            relationship.getBundleType().equalsIgnoreCase(
                                    ONTOKAI_RESTRICTION_BUNDLE_TYPE_VALUE) ) {
                        parentClassIri = relationship.getBundleValue();
                        if ( parentClassIri != null ) {
                            
                            // Get the object property IRI
                            String typeTransformed = relationship.getType()
                                    .strip().toUpperCase().replace("_", " ");
                            String objectPropertyIri = allSimpleObjectProperties
                                    .containsKey(typeTransformed) ? 
                                            allSimpleObjectProperties
                                                .get(typeTransformed).getIri() : 
                                                    null;
                            if ( objectPropertyIri != null )
                                parentClasses.put(parentClassIri, 
                                        objectPropertyIri);
                            
                        }
                    }
                }
                
            }
            
        }
        
        // Set the parent classes
        simpleClass.setParentClasses(parentClasses);
        
        // Return the final simple class object
        return simpleClass;
        
    }
    
    /**
     * Convert a given map of identified new classes into
     * a list of SimpleClassDiff objects
     * @param newClasses
     * @return
     */
    
    public static List<SimpleClassDiff> toCreatedSimpleClassDiffs(
            Map<String, SimpleClass> newClasses) {
        List<SimpleClassDiff> createdSimpleClassDiffs = new ArrayList<>();
        for ( SimpleClass simpleClass : newClasses.values()) {
            SimpleClassDiff simpleClassDiff = new SimpleClassDiff();
            simpleClassDiff.setAfter(simpleClass);
            simpleClassDiff.setAfterXml(OWLRDFXMLAPI
                    .generateOwlClassXml(simpleClass));
            createdSimpleClassDiffs.add(simpleClassDiff);
        }
        return createdSimpleClassDiffs;
    }
    
    /**
     * Convert a given map of identified updated classes into
     * a list of SimpleClassDiff objects
     * @param updatedClasses
     * @return
     */
    
    public static List<SimpleClassDiff> toUpdatedSimpleClassDiffs(
            Map<String, SimpleClass> updatedClasses) {
        List<SimpleClassDiff> updatedSimpleClassDiffs = new ArrayList<>();
        for ( SimpleClass simpleClass : updatedClasses.values()) {
            SimpleClassDiff simpleClassDiff = new SimpleClassDiff();
            simpleClassDiff.setBefore(simpleClass);
            simpleClassDiff.setAfter(simpleClass);
            simpleClassDiff.setAfterXml(OWLRDFXMLAPI
                    .generateOwlClassXml(simpleClass));
            updatedSimpleClassDiffs.add(simpleClassDiff);
        }
        return updatedSimpleClassDiffs;
    }
    
    /**
     * Convert a given map of identified deleted classes into 
     * a list of SimpleClassDiff objects
     * @param deletedClasses
     * @return
     */
    
    public static List<SimpleClassDiff> toDeletedSimpleClassDiffs(
            Map<String, SimpleClass> deletedClasses) {
        List<SimpleClassDiff> deletedSimpleClassDiffs = new ArrayList<>();
        for ( SimpleClass simpleClass : deletedClasses.values()) {
            SimpleClassDiff simpleClassDiff = new SimpleClassDiff();
            simpleClassDiff.setBefore(simpleClass);
            deletedSimpleClassDiffs.add(simpleClassDiff);
        }
        return deletedSimpleClassDiffs;
    }
    
    /**
     * Generate a random IRI using a given prefix
     * @return
     */
    
    public static String generateIri(SimpleOntology simpleOntology) {
        String iri = null;
        boolean generatedUniqueIri = false;
        while (!generatedUniqueIri) {
            iri = DEFAULT_IRI_PREFIX + RandomStringUtils.random(
                    DEFAULT_IRI_SUFFIX_LENGTH, true, true);
            if ( !simpleOntology.getSimpleAnnotationPropertyMap().containsKey(iri) && 
                    !simpleOntology.getSimpleObjectPropertyMap().containsKey(iri) && 
                    !simpleOntology.getSimpleClassMap().containsKey(iri))
                generatedUniqueIri = true;
        } 
        return iri;
    }

}
