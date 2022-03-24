package ai.hyperlearning.ontopop.owl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;

/**
 * RDF/XML Helper Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OWLRDFXMLAPI {
    
    private static final String IRI_PLACEHOLDER = "{IRI}";
    private static final String RDFS_LABEL_PLACEHOLDER = "{RDFS_LABEL}";
    private static final String RDFS_LABEL_NODE = 
            "<rdfs:label>" + RDFS_LABEL_PLACEHOLDER + "</rdfs:label>";
    private static final String RDFS_SUBPROPERTYOF_NODE = 
            "<rdfs:subPropertyOf rdf:resource=\"http://www.w3.org/2002/07/owl#topObjectProperty\"/>";
    private static final String WEBPROTEGE_NAMESPACE = 
            "http://webprotege.stanford.edu/";
    private static final String WEBPROTEGE_CLASS_ATTRIBUTE_NODE = 
            "<webprotege:{IRI_SUFFIX}>{VALUE}</webprotege:{IRI_SUFFIX}>";
    private static final String SKOS_NAMESPACE = 
            "http://www.w3.org/2004/02/skos/core#";
    private static final String SKOS_CLASS_ATTRIBUTE_NODE = 
            "<skos:{IRI_SUFFIX}>{VALUE}</skos:{IRI_SUFFIX}>";
    private static final String DCMI_NAMESPACE = 
            "http://purl.org/dc/elements/1.1/";
    private static final String DCMI_CLASS_ATTRIBUTE_NODE = 
            "<dc:{IRI_SUFFIX}>{VALUE}</dc:{IRI_SUFFIX}>";
    private static final String RDFS_NAMESPACE = 
            "http://www.w3.org/2000/01/rdf-schema#";
    private static final String RDFS_CLASS_ATTRIBUTE_NODE = 
            "<rdfs:{IRI_SUFFIX}>{VALUE}</rdfs:{IRI_SUFFIX}>";
    private static final String RDFS_SUBCLASSOF_NO_RESTRICTION_NODE = 
            "<rdfs:subClassOf rdf:resource=\"{PARENT_CLASS_IRI}\"/>";
    private static final String RDFS_SUBCLASSOF_RESTRICTION_NODE = 
            "<rdfs:subClassOf>"
                + "<owl:Restriction>"
                    + "<owl:onProperty rdf:resource=\"{OBJECT_PROPERTY_IRI}\"/>"
                    + "<owl:someValuesFrom rdf:resource=\"{PARENT_CLASS_IRI}\"/>"
                + "</owl:Restriction>"
            + "</rdfs:subClassOf>";
    
    private static final Map<OWLSchemaVocabulary, String> NODE_START = Map.ofEntries(
            new AbstractMap.SimpleEntry<OWLSchemaVocabulary, String>(
                    OWLSchemaVocabulary.ANNOTATION_PROPERTY, 
                    "<owl:AnnotationProperty rdf:about=\"{IRI}\">"), 
            new AbstractMap.SimpleEntry<OWLSchemaVocabulary, String>(
                    OWLSchemaVocabulary.OBJECT_PROPERTY, 
                    "<owl:ObjectProperty rdf:about=\"{IRI}\">"), 
            new AbstractMap.SimpleEntry<OWLSchemaVocabulary, String>(
                    OWLSchemaVocabulary.CLASS, 
                    "<owl:Class rdf:about=\"{IRI}\">")
    );
    
    private static final Map<OWLSchemaVocabulary, String> NODE_END = Map.ofEntries(
            new AbstractMap.SimpleEntry<OWLSchemaVocabulary, String>(
                    OWLSchemaVocabulary.ANNOTATION_PROPERTY, 
                    "</owl:AnnotationProperty>"), 
            new AbstractMap.SimpleEntry<OWLSchemaVocabulary, String>(
                    OWLSchemaVocabulary.OBJECT_PROPERTY, 
                    "</owl:ObjectProperty>"), 
            new AbstractMap.SimpleEntry<OWLSchemaVocabulary, String>(
                    OWLSchemaVocabulary.CLASS, 
                    "</owl:Class>")
    );
    
    private static final String RDF_XML_DECLARATION_START = 
            "<?xml version=\"1.0\"?>\n"
            + "<rdf:RDF xmlns=\"{IRI}#\"" + System.lineSeparator()
            + "     xml:base=\"{IRI}\"" + System.lineSeparator()
            + "     xmlns:dc=\"http://purl.org/dc/elements/1.1/\"" + System.lineSeparator()
            + "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"" + System.lineSeparator()
            + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" + System.lineSeparator()
            + "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"" + System.lineSeparator()
            + "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"" + System.lineSeparator()
            + "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"" + System.lineSeparator()
            + "     xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"" + System.lineSeparator()
            + "     xmlns:webprotege=\"http://webprotege.stanford.edu/\">" + System.lineSeparator()
            + "    <owl:Ontology rdf:about=\"{IRI}\"/>";
    private static final String RDF_XML_DECLARATION_END = "</rdf:RDF>";
    
    private OWLRDFXMLAPI() {
        throw new IllegalStateException("The OWLRDFXMLAPI "
                + "utility class cannot be instantiated.");
    }
    
    /**
     * Read an XML file and returns its contents as a string
     * @param xmlFile
     * @return
     * @throws IOException
     */
    
    public static String read(String xmlFile) throws IOException {
        Path path = Paths.get(xmlFile);
        return Files.readString(path, StandardCharsets.UTF_8);
    }
    
    /**
     * Extract an XML node as a string
     * @param xml
     * @param owlSchemaVocabulary
     * @param iri
     * @return
     */
    
    public static String getNodeAsString(
            String xml, OWLSchemaVocabulary owlSchemaVocabulary, String iri) {
        String nodeStart = NODE_START.get(owlSchemaVocabulary);
        String nodeEnd = NODE_END.get(owlSchemaVocabulary);
        int startIndex = xml.indexOf(nodeStart.replace(IRI_PLACEHOLDER, iri));
        int endIndex = xml.indexOf(nodeEnd, startIndex);
        return xml.substring(startIndex, endIndex + nodeEnd.length());
    }
    
    /**
     * Generate an XML string representing a new OWL Annotation Property
     * given its SimpleAnnotationProperty object representation
     * @param iri
     * @param label
     * @return
     * @throws ParserConfigurationException 
     * @throws TransformerException 
     */
    
    public static String generateNewOwlAnnotationPropertyXml(
            SimpleAnnotationProperty simpleAnnotationProperty)  {
        StringBuilder xmlBuilder = new StringBuilder(NODE_START.get(
                OWLSchemaVocabulary.ANNOTATION_PROPERTY)
                    .replace(IRI_PLACEHOLDER, 
                            simpleAnnotationProperty.getIri()));
        xmlBuilder.append(RDFS_LABEL_NODE.replace(
                RDFS_LABEL_PLACEHOLDER, simpleAnnotationProperty.getLabel()));
        xmlBuilder.append(NODE_END.get(
                OWLSchemaVocabulary.ANNOTATION_PROPERTY));
        return xmlBuilder.toString();
    }
    
    /**
     * Generate an XML string representing a new OWL Object Property
     * given its SimpleObjectProperty object representation
     * @param iri
     * @param label
     * @return
     */
    
    public static String generateNewOwlObjectPropertyXml(
            SimpleObjectProperty simpleObjectProperty) {
        StringBuilder xmlBuilder = new StringBuilder(NODE_START.get(
                OWLSchemaVocabulary.OBJECT_PROPERTY)
                    .replace(IRI_PLACEHOLDER, 
                            simpleObjectProperty.getIri()));
        xmlBuilder.append(RDFS_SUBPROPERTYOF_NODE);
        xmlBuilder.append(RDFS_LABEL_NODE.replace(
                RDFS_LABEL_PLACEHOLDER, simpleObjectProperty.getLabel()));
        xmlBuilder.append(NODE_END.get(
                OWLSchemaVocabulary.OBJECT_PROPERTY));
        return xmlBuilder.toString();
    }
    
    /**
     * Generate an XML string representing an OWL Class
     * given its SimpleClass object representation
     * @param simpleClass
     * @return
     */
    
    public static String generateOwlClassXml(SimpleClass simpleClass) {
        
        // Class IRI
        StringBuilder xmlBuilder = new StringBuilder(NODE_START.get(
                OWLSchemaVocabulary.CLASS)
                    .replace(IRI_PLACEHOLDER, simpleClass.getIri()));
        
        // RDFS Label
        xmlBuilder.append(RDFS_LABEL_NODE.replace(
                RDFS_LABEL_PLACEHOLDER, simpleClass.getLabel()));
        
        // Attributes
        for (Map.Entry<String, String> entry : 
            simpleClass.getAnnotations().entrySet()) {
            
            // Get the annotation IRI and literal value
            String annotationIri = entry.getKey();
            String annotationLiteralValue = entry.getValue();
            
            // WebProtege Namespace
            if ( annotationIri.contains(WEBPROTEGE_NAMESPACE) ) {
                String iriSuffix = annotationIri.substring(
                        annotationIri.indexOf(WEBPROTEGE_NAMESPACE) 
                            + WEBPROTEGE_NAMESPACE.length(), 
                        annotationIri.length());
                xmlBuilder.append(WEBPROTEGE_CLASS_ATTRIBUTE_NODE
                        .replace("{IRI_SUFFIX}", iriSuffix)
                        .replace("{VALUE}", annotationLiteralValue));
            }
            
            // SKOS Namespace
            else if ( annotationIri.contains(SKOS_NAMESPACE) ) {
                String iriSuffix = annotationIri.substring(
                        annotationIri.indexOf(SKOS_NAMESPACE) 
                            + SKOS_NAMESPACE.length(), 
                        annotationIri.length());
                xmlBuilder.append(SKOS_CLASS_ATTRIBUTE_NODE
                        .replace("{IRI_SUFFIX}", iriSuffix)
                        .replace("{VALUE}", annotationLiteralValue));
            }
            
            // DCMI Namespace
            else if ( annotationIri.contains(DCMI_NAMESPACE) ) {
                String iriSuffix = annotationIri.substring(
                        annotationIri.indexOf(DCMI_NAMESPACE) 
                            + DCMI_NAMESPACE.length(), 
                        annotationIri.length());
                xmlBuilder.append(DCMI_CLASS_ATTRIBUTE_NODE
                        .replace("{IRI_SUFFIX}", iriSuffix)
                        .replace("{VALUE}", annotationLiteralValue));
            }
            
            // RDFS Namespace
            else if ( annotationIri.contains(RDFS_NAMESPACE) ) {
                String iriSuffix = annotationIri.substring(
                        annotationIri.indexOf(RDFS_NAMESPACE) 
                            + RDFS_NAMESPACE.length(), 
                        annotationIri.length());
                xmlBuilder.append(RDFS_CLASS_ATTRIBUTE_NODE
                        .replace("{IRI_SUFFIX}", iriSuffix)
                        .replace("{VALUE}", annotationLiteralValue));
            }
            
        }
        
        // Relationships
        for (Map.Entry<String, String> entry : 
            simpleClass.getParentClasses().entrySet()) {
            
            // Get the parent class IRI and object property restriction IRI
            String parentClassIri = entry.getKey();
            String objectPropertyRestrictionIri = entry.getValue();
            
            // NO object property restriction
            if ( objectPropertyRestrictionIri == null )
                xmlBuilder.append(RDFS_SUBCLASSOF_NO_RESTRICTION_NODE
                        .replace("{PARENT_CLASS_IRI}", parentClassIri));
            
            // Object property restriction
            else 
                xmlBuilder.append(RDFS_SUBCLASSOF_RESTRICTION_NODE
                        .replace("{OBJECT_PROPERTY_IRI}", 
                                objectPropertyRestrictionIri)
                        .replace("{PARENT_CLASS_IRI}", 
                                parentClassIri));
            
        }
        
        // End node
        xmlBuilder.append(NODE_END.get(
                OWLSchemaVocabulary.CLASS));
        return xmlBuilder.toString();
        
    }
    
    /**
     * Delete an OWL Annotation Property XML node from a given
     * RDF/XML OWL string given a SimpleAnnotationProperty object
     * @param simpleAnnotationProperty
     * @return
     */
    
    public static String deleteOwlAnnotationPropertyFromOwlXmlString(
            String owlXml, SimpleAnnotationProperty simpleAnnotationProperty, 
            String comment) {
        return deleteOwlNodeFromXmlString(owlXml, 
                OWLSchemaVocabulary.ANNOTATION_PROPERTY, 
                simpleAnnotationProperty.getIri(), 
                comment);
    }
    
    /**
     * Delete an OWL Object Property XML node from a given
     * RDF/XML OWL string given a SimpleObjectProperty object
     * @param simpleObjectProperty
     * @return
     */
    
    public static String deleteOwlObjectPropertyFromOwlXmlString(
            String owlXml, SimpleObjectProperty simpleObjectProperty, 
            String comment) {
        return deleteOwlNodeFromXmlString(owlXml, 
                OWLSchemaVocabulary.OBJECT_PROPERTY, 
                simpleObjectProperty.getIri(), 
                comment);
    }
    
    /**
     * Delete an OWL Class XML node from a given
     * RDF/XML OWL string given a SimpleClass object
     * @param simpleClass
     * @return
     */
    
    public static String deleteOwlClassFromOwlXmlString(
            String owlXml, SimpleClass simpleClass, String comment) {
        return deleteOwlNodeFromXmlString(owlXml, 
                OWLSchemaVocabulary.CLASS, 
                simpleClass.getIri(), 
                comment);
    }
    
    /**
     * Delete a general OWL XML node from a given RDF/XML OWL
     * string given the OWL object type and IRI
     * @param owlSchemaVocabulary
     * @param iri
     * @return
     */
    
    public static String deleteOwlNodeFromXmlString(String owlXml, 
            OWLSchemaVocabulary owlSchemaVocabulary, String iri, 
            String comment) {
        String owlXmlNodeStart = NODE_START.get(owlSchemaVocabulary)
                .replace(IRI_PLACEHOLDER, iri);
        String owlXmlNodeEnd = NODE_END.get(owlSchemaVocabulary);
        int owlXmlNodeStartIndex = owlXml.indexOf(owlXmlNodeStart);
        if ( owlXmlNodeStartIndex > -1 ) {
            int owlXmlNodeEndIndex = owlXml.indexOf(
                    owlXmlNodeEnd, owlXmlNodeStartIndex);
            if ( owlXmlNodeEndIndex > -1 ) {
                StringBuilder buffer = new StringBuilder(owlXml);
                buffer.replace(owlXmlNodeStartIndex, 
                        owlXmlNodeEndIndex + owlXmlNodeEnd.length(), 
                        comment != null ? "<!-- " + comment + " -->" : "");
                return buffer.toString();
            }
        }
        return owlXml;
    }
    
    /**
     * Delete a general string from a given RDF/XML OWL string
     * @param owlXml
     * @param stringToRemove
     * @return
     */
    
    public static String deleteStringFromXmlString(
            String owlXml, String stringToRemove) {
        return owlXml.replace(stringToRemove, "");
    }
    
    /**
     * Append a general string to a given RDF/XML OWL string
     * @param owlXml
     * @param owlXmlToAdd
     * @param comment
     * @return
     */
    
    public static String addStringToXmlString(
            String owlXml, String stringToAdd, String comment) {
        StringBuilder xml = new StringBuilder(owlXml);
        xml.append(System.lineSeparator());
        if ( comment != null ) {
            xml.append("<!-- " + comment + " -->");
            xml.append(System.lineSeparator());
        }
        xml.append(stringToAdd);
        return xml.toString();
    }
    
    /**
     * Add the RDF/XML declaration to a given RDF/XML OWL string
     * @param ontologyIri
     * @param owlXml
     * @return
     */
    
    public static String addDeclarationToXmlString(
            String ontologyIri, String owlXml) {
        StringBuilder xml = new StringBuilder(RDF_XML_DECLARATION_START
                .replace(IRI_PLACEHOLDER, ontologyIri));
        xml.append(owlXml);
        xml.append(RDF_XML_DECLARATION_END);
        return xml.toString();
    }

}
