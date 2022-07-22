package ai.hyperlearning.ontopop.owl.mappers;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.apicatalog.jsonld.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPropertyGraphModellingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidTargetFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceOntologyDataException;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyEdge;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyVertex;
import ai.hyperlearning.ontopop.model.graph.formats.PropertyGraph;
import ai.hyperlearning.ontopop.model.graph.formats.PropertyGraphFormat;
import ai.hyperlearning.ontopop.model.graph.formats.graphson.GraphSONGraph;
import ai.hyperlearning.ontopop.model.graph.formats.vis.VisDatasetGraph;
import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleNamedIndividual;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.owl.OWLAPI;
import ai.hyperlearning.ontopop.owl.mappers.ontopop.RdfXmlNativeMapper;
import ai.hyperlearning.ontopop.rdf.DCMI;
import ai.hyperlearning.ontopop.rdf.RDFSchema;
import ai.hyperlearning.ontopop.rdf.SKOSVocabulary;

/**
 * RDF/XML to target graph-based format mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class RdfXmlToPropertyGraphMapper {
    
    private static final int DEFAULT_ONTOLOGY_ID = 1;
    private static final long DEFAULT_LATEST_GIT_WEBHOOK_ID = 0;
    
    private RdfXmlToPropertyGraphMapper() {
        throw new IllegalStateException("The RdfXmlModeller utility "
            + "class cannot be instantiated.");
    }
    
    /**
     * Map the contents of a given RDF/XML OWL file to a given format
     * @param rdfXml
     * @param format
     * @return
     * @throws IOException 
     * @throws OWLOntologyStorageException 
     * @throws OWLOntologyCreationException 
     */
    
    public static String map(String owlFile, String format) 
            throws IOException, 
            OntologyMapperInvalidSourceOntologyDataException, 
            OntologyMapperInvalidTargetFormatException, 
            OntologyDataParsingException, 
            OntologyDataPropertyGraphModellingException {
        
        // Parse the RDF/XML contents of the given OWL file
        // into a SimpleOntology object
        SimpleOntology simpleOntology = toSimpleOntology(owlFile);
        
        // Transform the SimpleOntology object into a 
        // SimpleOntologyPropertyGraph object
        SimpleOntologyPropertyGraph simpleOntologyPropertyGraph = 
                toSimpleOntologyPropertyGraph(simpleOntology);
        
        // Map the RDF/XML contents of the given OWL file 
        // into the given format
        MapperTargetFormat targetFormat = MapperTargetFormat
                .valueOfLabel(format.strip().toUpperCase());
        if ( targetFormat == null )
            throw new OntologyMapperInvalidTargetFormatException();
        String formattedPropertyGraph = 
                format(simpleOntologyPropertyGraph, targetFormat);
        if ( !StringUtils.isBlank(formattedPropertyGraph) )
            return formattedPropertyGraph;
        else {
            
            // Validate the given RDF/XML OWL file semantically
            if ( !isSemanticallyValid(owlFile) )
                throw new OntologyMapperInvalidSourceOntologyDataException(
                        "Invalid ontology data file provided - "
                        + "file is semantically invalid.");
            else throw new OntologyDataParsingException();
            
        }
        
    }
    
    /**
     * Validate that the contents of the given OWL file is semantically valid
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
            Map<String, SimpleNamedIndividual> simpleNamedIndividualMap = 
                    OWLAPI.parseNamedIndividuals(ontology);
            return new SimpleOntology(DEFAULT_ONTOLOGY_ID, 
                    DEFAULT_LATEST_GIT_WEBHOOK_ID,
                    simpleAnnotationPropertyMap, 
                    simpleObjectPropertyMap,
                    simpleClassMap, 
                    simpleNamedIndividualMap);
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
    
    /**
     * Format the given SimpleOntologyPropertyGraph object into the given
     * property graph format.
     * @param simpleOntologyPropertyGraph
     * @param propertyGraphFormat
     * @return
     * @throws JsonProcessingException
     */
    
    public static String format(
            SimpleOntologyPropertyGraph simpleOntologyPropertyGraph, 
            MapperTargetFormat targetFormat)
                    throws JsonProcessingException {
        
        // Native format
        if ( targetFormat.equals(MapperTargetFormat.NATIVE) )
            return RdfXmlNativeMapper.map(simpleOntologyPropertyGraph);
        
        // JSON-based formats
        else {
            
            // Get the property graph format
            
            PropertyGraphFormat propertyGraphFormat = null;
            switch (targetFormat) {
                case GRAPHSON:
                    propertyGraphFormat = new GraphSONGraph();
                    break;
                case VIS:
                    propertyGraphFormat = new VisDatasetGraph();
                    break;
                default:
                    throw new OntologyMapperInvalidTargetFormatException();
            }
            
            // Generate the vertices
            for (SimpleOntologyVertex simpleOntologyVertex : 
                    simpleOntologyPropertyGraph.getVertices().values()) {
                simpleOntologyVertex.preparePropertiesForModelling();
                propertyGraphFormat.addVertex(
                        simpleOntologyVertex.getVertexId(), 
                        simpleOntologyVertex.getLabel(), 
                        simpleOntologyVertex.getProperties());
            }
            
            // Generate the edges
            long edgeId = 1;
            for (SimpleOntologyEdge simpleOntologyEdge : 
                    simpleOntologyPropertyGraph.getEdges()) {
                propertyGraphFormat.addEdge(
                        edgeId, 
                        simpleOntologyEdge.getLabel(), 
                        simpleOntologyEdge.getSourceVertexId(), 
                        simpleOntologyEdge.getTargetVertexId(), 
                        simpleOntologyEdge.getProperties());
                edgeId++;
            }
            
            // Return the GraphSON string
            PropertyGraph graph = new PropertyGraph(propertyGraphFormat);
            ObjectWriter writer = new ObjectMapper()
                    .writer().withDefaultPrettyPrinter();
            return writer.writeValueAsString(graph);
            
        }
        
    }

}
