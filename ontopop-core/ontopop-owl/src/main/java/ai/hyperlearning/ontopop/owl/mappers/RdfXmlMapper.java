package ai.hyperlearning.ontopop.owl.mappers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPropertyGraphModellingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidOntologyDataException;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.owl.OWLAPI;
import ai.hyperlearning.ontopop.owl.mappers.graphson.RdfXmlGraphSONMapper;
import ai.hyperlearning.ontopop.owl.mappers.ontopop.RdfXmlNativeMapper;
import ai.hyperlearning.ontopop.rdf.DCMI;
import ai.hyperlearning.ontopop.rdf.RDFSchema;
import ai.hyperlearning.ontopop.rdf.SKOSVocabulary;

/**
 * RDF/XML mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class RdfXmlMapper {
    
    private static final int DEFAULT_ONTOLOGY_ID = 1;
    private static final long DEFAULT_LATEST_GIT_WEBHOOK_ID = 0;
    
    private RdfXmlMapper() {
        throw new IllegalStateException("The RdfXmlModeller utility "
            + "class cannot be instantiated.");
    }
    
    /**
     * Model the contents of a given RDF/XML OWL file into a given format
     * @param rdfXml
     * @param format
     * @return
     * @throws IOException 
     */
    
    public static String model(String owlFile, String format) 
            throws IOException, 
            OntologyMapperInvalidOntologyDataException, 
            OntologyMapperInvalidFormatException, 
            OntologyDataParsingException, 
            OntologyDataPropertyGraphModellingException{
        
        // Validate the given OWL file and its contents
        if ( !isValid(owlFile) || !isSemanticallyValid(owlFile) )
            throw new OntologyMapperInvalidOntologyDataException();
        
        // Get and validate the target format
        RdfXmlMapperFormat targetFormat = RdfXmlMapperFormat
                .valueOfLabel(format.strip().toUpperCase());
        if ( targetFormat == null )
            throw new OntologyMapperInvalidFormatException();
        
        // Parse the RDF/XML contents of the given OWL file
        // into a SimpleOntology object
        SimpleOntology simpleOntology = toSimpleOntology(owlFile);
        
        // Transform the SimpleOntology object into a 
        // SimpleOntologyPropertyGraph object
        SimpleOntologyPropertyGraph simpleOntologyPropertyGraph = 
                toSimpleOntologyPropertyGraph(simpleOntology);
        
        // Map the RDF/XML contents of the given OWL file 
        // into the given format
        switch (targetFormat) {
            case NATIVE:
                return RdfXmlNativeMapper
                        .map(simpleOntologyPropertyGraph);
            case GRAPHSON:
                return RdfXmlGraphSONMapper
                        .map(simpleOntologyPropertyGraph);
            default:
                throw new OntologyMapperInvalidFormatException();
        }
        
    }
    
    /**
     * Validate that a given OWL file exists and is not empty
     * @param owlFile
     * @return
     * @throws IOException
     */
    
    public static boolean isValid(String owlFile) throws IOException {
        Path path = Paths.get(owlFile);
        if ( Files.exists(path) ) {
            String rdfXml = Files.readString(path, StandardCharsets.UTF_8);
            if ( !StringUtils.isBlank(rdfXml) )
                return true;
        }
        return false;
    }
    
    /**
     * Validate that the contents of a given OWL file is semantically valid
     * @param owlFile
     * @return
     * @throws OWLOntologyCreationException
     */
    
    public static boolean isSemanticallyValid(String owlFile) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            OWLOntology ontology = manager
                    .loadOntologyFromOntologyDocument(new File(owlFile));
            Configuration configuration = new Configuration();
            OWLReasoner reasoner = new Reasoner(configuration, ontology);
            return reasoner.isConsistent();
        } catch (OWLOntologyCreationException e) {
            return false;
        }
    }
    
    /**
     * Parse the RDF/XML contents of a given OWL file into a
     * SimpleOntology object 
     * @param owlFile
     * @return
     */
    
    public static SimpleOntology toSimpleOntology(String owlFile) {
        try {
            OWLOntology ontology = OWLAPI.loadOntology(new File(owlFile));
            Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap =
                    OWLAPI.parseAnnotationProperties(ontology);
            Map<String, SimpleObjectProperty> simpleObjectPropertyMap =
                    OWLAPI.parseObjectProperties(ontology);
            Map<String, SimpleClass> simpleClassMap = 
                    OWLAPI.parseClasses(ontology);
            return new SimpleOntology(DEFAULT_ONTOLOGY_ID, 
                    DEFAULT_LATEST_GIT_WEBHOOK_ID,
                    simpleAnnotationPropertyMap, 
                    simpleObjectPropertyMap,
                    simpleClassMap);
        } catch (Exception e) {
            throw new OntologyDataParsingException();
        }
    }
    
    /**
     * Transform the given parsed RDF/XML contents of an OWL file 
     * into a SimpleOntologyPropertyGraph object
     * @param simpleOntology
     * @return
     * @throws OWLOntologyCreationException
     * @throws IOException
     */
    
    public static SimpleOntologyPropertyGraph toSimpleOntologyPropertyGraph(
            SimpleOntology simpleOntology) {
        
        try {
            
            // Load the SKOS Vocabulary and parse its annotation properties
            OWLOntology skos = SKOSVocabulary.loadSKOSRDF();
            Map<String, SimpleAnnotationProperty> skosAnnotationProperties =
                    SKOSVocabulary.parseAnnotationProperties(skos);

            // Load the RDF Schema and parse its annotation properties
            OWLOntology rdf = RDFSchema.loadRdfSchema();
            Map<String, SimpleAnnotationProperty> rdfSchemaAnnotationProperties =
                    RDFSchema.parseAnnotationProperties(rdf);

            // Load the DCMI RDF Schema and parse its annotation properties
            Map<String, SimpleAnnotationProperty> dcmiSchemaAnnotationProperties =
                    DCMI.parseAnnotationProperties();

            // Aggregate the standard schema annotation properties
            Map<String, SimpleAnnotationProperty> standardSchemaAnnotationProperties =
                    new LinkedHashMap<>(skosAnnotationProperties);
            standardSchemaAnnotationProperties
                    .putAll(rdfSchemaAnnotationProperties);
            standardSchemaAnnotationProperties
                    .putAll(dcmiSchemaAnnotationProperties);
            
            // Transform the SimpleOntology object into a
            // SimpleOntologyPropertyGraph object
            return new SimpleOntologyPropertyGraph(DEFAULT_ONTOLOGY_ID, 
                    DEFAULT_LATEST_GIT_WEBHOOK_ID, 
                    simpleOntology,
                    standardSchemaAnnotationProperties);
            
        } catch (Exception e) {
            throw new OntologyDataPropertyGraphModellingException();
        }
        
    }

}