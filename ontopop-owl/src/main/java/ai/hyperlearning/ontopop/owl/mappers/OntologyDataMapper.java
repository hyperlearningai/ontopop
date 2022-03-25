package ai.hyperlearning.ontopop.owl.mappers;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataInvalidFormatException;
import ai.hyperlearning.ontopop.exceptions.security.InvalidClientNameException;
import ai.hyperlearning.ontopop.exceptions.vendors.OntoKaiInvalidOntologyPayloadException;
import ai.hyperlearning.ontopop.exceptions.vendors.OntoKaiOntologyPayloadMappingException;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyNode;
import ai.hyperlearning.ontopop.model.ontokai.OntoKaiOntologyPayload;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyDiff;
import ai.hyperlearning.ontopop.owl.OWLRDFXMLAPI;
import ai.hyperlearning.ontopop.owl.mappers.ontokai.OntoKaiOntologyDataMapper;

/**
 * Ontology data mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataMapper {
    
    private OntologyDataMapper() {
        throw new IllegalStateException("The OntologyDataMapper utility "
                + "class cannot be instantiated.");
    }
    
    /**
     * Transform a given string object into an RDF/XML OWL string.
     * @param format
     * @param clientData
     * @param client
     * @param existingOwlRdfXml
     * @param existingSimpleOntology
     * @return
     */
    
    public static String toOwlRdfXml(
            String format, 
            String ontologyData, String client, 
            String existingOwlRdfXml, 
            SimpleOntology existingSimpleOntology) {
        
        // RDF/XML format
        OntologyDataMapperFormat ontologyDataFormat = OntologyDataMapperFormat
                .valueOfLabel(format.strip().toUpperCase());
        if ( ontologyDataFormat != null && 
                ontologyDataFormat.equals(OntologyDataMapperFormat.RDF_XML) )
            return ontologyData;
        
        // JSON format
        else if ( ontologyDataFormat != null && 
                ontologyDataFormat.equals(OntologyDataMapperFormat.JSON) ) {
            
            // OntoKai client
            OntologyDataMapperClient ontologyDataClient = 
                    OntologyDataMapperClient.valueOfLabel(
                            client.strip().toUpperCase());
            if ( ontologyDataClient != null && 
                    ontologyDataClient.equals(OntologyDataMapperClient.ONTOKAI) ) {
                
                // Map the client data JSON string as an OntoKai ontology
                // payload object
                OntoKaiOntologyPayload ontokaiOntologyPayload = null;
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ontokaiOntologyPayload = mapper.readValue(ontologyData, 
                            OntoKaiOntologyPayload.class);
                    if ( !ontokaiOntologyPayload.isValid() )
                        throw new OntoKaiInvalidOntologyPayloadException();
                    for ( OntoKaiOntologyNode node : 
                        ontokaiOntologyPayload.getNodes() ) {
                        if ( !node.isValid() )
                            throw new OntoKaiInvalidOntologyPayloadException();
                    }
                } catch (JsonProcessingException e) {
                    throw new OntoKaiInvalidOntologyPayloadException();
                }
                
                // Generate the RDF/XML string
                try {
                    
                    // Transform the OntoKai ontology payload object
                    // into a SimpleOntology diff object
                    SimpleOntologyDiff simpleOntologyDiff = OntoKaiOntologyDataMapper
                            .generateSimpleOntologyDiff(ontokaiOntologyPayload, 
                                    existingSimpleOntology);
                    
                    // Generate the target RDF/XML string
                    return OWLRDFXMLAPI.prettyPrint(
                            OWLRDFXMLAPI.generateRdfXmlOwlString(
                                    existingOwlRdfXml, simpleOntologyDiff, 
                                    "OntoKai"));
                    
                } catch (IOException | OWLOntologyCreationException | 
                        TransformerException | SAXException | 
                        ParserConfigurationException e) {
                    throw new OntoKaiOntologyPayloadMappingException();
                }
                
            }
            
            // Invalid client
            else 
                throw new InvalidClientNameException();
            
        } 
        
        // Invalid format
        else
            throw new OntologyDataInvalidFormatException();
        
    }

}
