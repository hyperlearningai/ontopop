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
     * given its IRI and RDFS label
     * @param iri
     * @param label
     * @return
     * @throws ParserConfigurationException 
     * @throws TransformerException 
     */
    
    public static String generateNewOwlAnnotationPropertyXml(
            String iri, String label)  {
        StringBuilder xmlBuilder = new StringBuilder(NODE_START.get(
                OWLSchemaVocabulary.ANNOTATION_PROPERTY)
                    .replace(IRI_PLACEHOLDER, iri));
        xmlBuilder.append(RDFS_LABEL_NODE
                .replace(RDFS_LABEL_PLACEHOLDER, label));
        xmlBuilder.append(NODE_END.get(
                OWLSchemaVocabulary.ANNOTATION_PROPERTY));
        return xmlBuilder.toString();
    }
    
    /**
     * Generate an XML string representing a new OWL Object Property
     * given its IRI and RDFS label
     * @param iri
     * @param label
     * @return
     */
    
    public static String generateNewOwlObjectPropertyXml(
            String iri, String label) {
        StringBuilder xmlBuilder = new StringBuilder(NODE_START.get(
                OWLSchemaVocabulary.OBJECT_PROPERTY)
                    .replace(IRI_PLACEHOLDER, iri));
        xmlBuilder.append(RDFS_SUBPROPERTYOF_NODE);
        xmlBuilder.append(RDFS_LABEL_NODE
                .replace(RDFS_LABEL_PLACEHOLDER, label));
        xmlBuilder.append(NODE_END.get(
                OWLSchemaVocabulary.OBJECT_PROPERTY));
        return xmlBuilder.toString();
    }

}
