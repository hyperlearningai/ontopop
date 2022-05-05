package ai.hyperlearning.ontopop.owl.mappers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.riot.RDFFormat;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPropertyGraphModellingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceOntologyDataException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidTargetFormatException;
import ai.hyperlearning.ontopop.owl.OWLAPI;

/**
 * Ontology data (e.g. RDF/XML) to target format (e.g. GRAPHSON) mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class Mapper {
    
    private static final long MAX_FILE_SIZE_BYTES = 1048576;
    private static final Set<MapperTargetFormat> TARGET_GRAPH_FORMATS = 
            new HashSet<>(Arrays.asList(
                    MapperTargetFormat.GRAPHSON, 
                    MapperTargetFormat.NATIVE));
    
    private Mapper() {
        throw new IllegalStateException("The Mapper utility class "
            + "cannot be instantiated.");
    }
    
    /**
     * Map the given source ontology data (e.g. RDF/XML) 
     * into the given target format (e.g. GRAPHSON)
     * @param source
     * @param target
     * @param ontologyFile
     * @return
     * @throws OntologyMapperInvalidSourceFormatException
     * @throws OntologyMapperInvalidSourceOntologyDataException
     * @throws OntologyMapperInvalidTargetFormatException
     * @throws OntologyDataParsingException
     * @throws OntologyDataPropertyGraphModellingException
     * @throws IOException
     * @throws OWLOntologyStorageException 
     * @throws OWLOntologyCreationException 
     */
    
    public static String map(String source, String target, 
            String ontologyFile) throws 
        OntologyMapperInvalidSourceFormatException, 
        OntologyMapperInvalidSourceOntologyDataException, 
        OntologyMapperInvalidTargetFormatException, 
        OntologyDataParsingException, 
        OntologyDataPropertyGraphModellingException, 
        IOException, 
        OWLOntologyCreationException, 
        OWLOntologyStorageException {
        
        // Validate the source format
        MapperSourceFormat sourceFormat = MapperSourceFormat
                .valueOfLabel(source.strip().toUpperCase());
        if ( sourceFormat == null )
            throw new OntologyMapperInvalidSourceFormatException();
        
        // Validate the target format
        MapperTargetFormat targetFormat = MapperTargetFormat
                .valueOfLabel(target.strip().toUpperCase());
        if ( targetFormat == null )
            throw new OntologyMapperInvalidTargetFormatException();
        
        // Validate that the given source ontology file exists
        if ( !exists(ontologyFile) )
            throw new OntologyMapperInvalidSourceOntologyDataException(
                    "Invalid ontology data file provided - "
                    + "file does not exist.");
        
        // Validate the size of the given source ontology file
        if ( !isValidFileSize(ontologyFile) )
            throw new OntologyMapperInvalidSourceOntologyDataException(
                    "Invalid ontology data file provided - "
                    + "file size limit exceeded.");
        
        // Source format equals the target format
        if ( source.equalsIgnoreCase(target) )
            throw new OntologyMapperInvalidTargetFormatException();
        
        // RDF/XML to OWL/XML
        if ( sourceFormat.equals(MapperSourceFormat.RDF_XML) && 
                targetFormat.equals(MapperTargetFormat.OWL_XML) )
            return OWLAPI.toOwlXml(ontologyFile);
        
        // OWL/XML to RDF/XML
        else if ( sourceFormat.equals(MapperSourceFormat.OWL_XML) && 
                targetFormat.equals(MapperTargetFormat.RDF_XML) )
            return OWLAPI.toRdfXml(ontologyFile);
            
        // Any other source format to OWL/XML
        else if ( targetFormat.equals(MapperTargetFormat.OWL_XML) )
            throw new OntologyMapperInvalidSourceFormatException();
        
        // OWL/XML to any other target format
        else if ( sourceFormat.equals(MapperSourceFormat.OWL_XML) )
            throw new OntologyMapperInvalidTargetFormatException();
        
        // Non-graph target formats
        else if ( !TARGET_GRAPH_FORMATS.contains(targetFormat) ) {
            
            // Use Apache Jena to perform the conversion
            switch (targetFormat) {
                case JSON_LD:
                    return OWLAPI.toRdfFormat(ontologyFile, 
                            RDFFormat.JSONLD11_PRETTY);
                case N_QUADS:
                    return OWLAPI.toRdfFormat(ontologyFile, 
                            RDFFormat.NQUADS);
                case N_TRIPLES:
                    return OWLAPI.toRdfFormat(ontologyFile, 
                            RDFFormat.NTRIPLES);
                case RDF_XML:
                    return OWLAPI.toRdfFormat(ontologyFile, 
                            RDFFormat.RDFXML_PRETTY);
                case TRIG:
                    return OWLAPI.toRdfFormat(ontologyFile, 
                            RDFFormat.TRIG_PRETTY);
                case TURTLE:
                    return OWLAPI.toRdfFormat(ontologyFile, 
                            RDFFormat.TURTLE_PRETTY);
                default:
                    throw new OntologyMapperInvalidTargetFormatException();
            }
            
        }
        
        // Graph-based target format
        else if ( TARGET_GRAPH_FORMATS.contains(targetFormat) ) {
            
            // Convert the source to RDF/XML if it isn't already
            if ( !sourceFormat.equals(MapperSourceFormat.RDF_XML) ) {
                String rdfXml = OWLAPI.toRdfFormat(ontologyFile, 
                        RDFFormat.RDFXML_PRETTY);
                Path temporaryFile = Files.createTempFile(null, ".owl");
                Files.write(temporaryFile, rdfXml.getBytes(
                        StandardCharsets.UTF_8));
                return RdfXmlToPropertyGraphMapper.map(
                        temporaryFile.toAbsolutePath().toString(), target);
            } else
                return RdfXmlToPropertyGraphMapper.map(ontologyFile, target);
            
        }
        
        else throw new OntologyMapperInvalidTargetFormatException();
        
    }
    
    /**
     * Validate that the given ontology file exists
     * @param ontologyFile
     * @return
     */
    
    public static boolean exists(String ontologyFile) {
        Path path = Paths.get(ontologyFile);
        return Files.exists(path);
    }
    
    
    /**
     * Validate that the given ontology file size is less than
     * the maximum file size permitted.
     * @param ontologyFile
     * @return
     * @throws IOException 
     */
    
    public static boolean isValidFileSize(
            String ontologyFile) throws IOException {
        Path path = Paths.get(ontologyFile);
        long fileSizeBytes = Files.size(path);
        return fileSizeBytes <= MAX_FILE_SIZE_BYTES;
    }

}
