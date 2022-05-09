package ai.hyperlearning.ontopop.owl.mappers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.riot.RDFFormat;
import org.apache.tika.Tika;
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
    
    // File size limit
    private static final long MAX_FILE_SIZE_BYTES = 1048576;
    
    // Valid source file extensions
    private static final Map<MapperSourceFormat, Set<String>> VALID_FILE_EXTENSIONS = 
            Map.ofEntries(
                    new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.N_QUADS, 
                            new HashSet<>(Arrays.asList("NQ"))),
                    new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.N_TRIPLES, 
                            new HashSet<>(Arrays.asList("NT"))),
                    new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.OWL_XML, 
                            new HashSet<>(Arrays.asList("OWL", "XML"))),
                    new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.RDF_XML, 
                            new HashSet<>(Arrays.asList("OWL", "RDF", "XML"))),
                    new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.TRIG, 
                            new HashSet<>(Arrays.asList("TRIG"))),
                    new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.TURTLE, 
                            new HashSet<>(Arrays.asList("TTL")))
                );
    
    // Valid source file MIME types
    private static final Map<MapperSourceFormat, Set<String>> VALID_MIME_TYPES = 
        Map.ofEntries(
            new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.N_QUADS, 
                    new HashSet<>(Arrays.asList("APPLICATION/N-QUADS", "TEXT/X-NQUADS", "TEXT/PLAIN"))),
            new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.N_TRIPLES, 
                    new HashSet<>(Arrays.asList("APPLICATION/N-TRIPLES", "TEXT/PLAIN"))),
            new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.OWL_XML, 
                    new HashSet<>(Arrays.asList("APPLICATION/OWL+XML", "TEXT/XML", "TEXT/PLAIN"))),
            new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.RDF_XML, 
                    new HashSet<>(Arrays.asList("APPLICATION/RDF+XML", "TEXT/XML", "TEXT/PLAIN"))),
            new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.TRIG, 
                    new HashSet<>(Arrays.asList("TEXT/TRIG", "TEXT/PLAIN"))),
            new AbstractMap.SimpleEntry<MapperSourceFormat, Set<String>>(MapperSourceFormat.TURTLE, 
                    new HashSet<>(Arrays.asList("APPLICATION/TURTLE", "APPLICATION/X-TURTLE", "APPLICATION/RDF+N3", 
                            "TEXT/TURTLE", "TEXT/N3", "TEXT/RDF+N3", "TEXT/PLAIN")))
        );
    
    // Target graph formats
    private static final Set<MapperTargetFormat> TARGET_GRAPH_FORMATS = 
            new HashSet<>(Arrays.asList(
                    MapperTargetFormat.GRAPHSON, 
                    MapperTargetFormat.NATIVE, 
                    MapperTargetFormat.VIS));
    
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
        
        // Validate the mapping request
        MapperSourceFormat sourceFormat = MapperSourceFormat
                .valueOfLabel(source.strip().toUpperCase());
        MapperTargetFormat targetFormat = MapperTargetFormat
                .valueOfLabel(target.strip().toUpperCase());
        validate(source, target, ontologyFile);
        
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
     * Validate the Mapping request
     * @param source
     * @param target
     * @param ontologyFile
     * @throws OntologyMapperInvalidSourceFormatException
     * @throws OntologyMapperInvalidSourceOntologyDataException
     * @throws OntologyMapperInvalidTargetFormatException
     * @throws OntologyDataParsingException
     * @throws OntologyDataPropertyGraphModellingException
     * @throws IOException
     */
    
    public static void validate(String source, String target, 
            String ontologyFile) throws 
        OntologyMapperInvalidSourceFormatException, 
        OntologyMapperInvalidSourceOntologyDataException, 
        OntologyMapperInvalidTargetFormatException, 
        OntologyDataParsingException, 
        OntologyDataPropertyGraphModellingException, 
        IOException {
        
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
        
        // Validate the source ontology file extension
        if ( !isValidFileExtension(ontologyFile, sourceFormat) )
            throw new OntologyMapperInvalidSourceOntologyDataException(
                    "Invalid ontology data file provided - "
                    + "file extension does not match the specified "
                    + "source format.");
        
        // Validate the size of the given source ontology file
        if ( !isValidFileSize(ontologyFile) )
            throw new OntologyMapperInvalidSourceOntologyDataException(
                    "Invalid ontology data file provided - "
                    + "file size limit exceeded.");
        
        // Validate the source file MIME type
        if ( !isValidMimeType(ontologyFile, sourceFormat) )
            throw new OntologyMapperInvalidSourceOntologyDataException(
                    "Invalid ontology data file provided - "
                    + "MIME type does not match the specified "
                    + "source format.");
        
        // Validate that the given source ontology file is not blank
        if ( isBlank(ontologyFile) )
            throw new OntologyMapperInvalidSourceOntologyDataException(
                    "Invalid ontology data file provided - "
                    + "file is blank.");
        
        // Source format equals the target format
        if ( source.equalsIgnoreCase(target) )
            throw new OntologyMapperInvalidTargetFormatException();
        
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
     * Validate that the given ontology file is not blank
     * @param owlFile
     * @return
     * @throws IOException
     */
    
    public static boolean isBlank(String ontologyFile) 
            throws IOException {
        Path path = Paths.get(ontologyFile);
        String ontology = Files.readString(path, StandardCharsets.UTF_8);
        return StringUtils.isBlank(ontology);
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
    
    /**
     * Validate the file extension of the given ontology file
     * @param ontologyFile
     * @param sourceFormat
     * @return
     */
    
    public static boolean isValidFileExtension(String ontologyFile, 
            MapperSourceFormat sourceFormat) {
        String fileExtension = FilenameUtils.getExtension(ontologyFile);
        return VALID_FILE_EXTENSIONS.get(sourceFormat).contains(
                fileExtension.toUpperCase());
    }
    
    /**
     * Validate the MIME type of the given ontology file
     * @param owlFile
     * @return
     * @throws IOException
     */
    
    public static boolean isValidMimeType(String ontologyFile, 
            MapperSourceFormat sourceFormat) throws IOException {
        
        // OS-specific implementation
        Path path = Paths.get(ontologyFile);
        String mimeType = Files.probeContentType(path);
        
        // Apache Tika implementation
        if ( mimeType == null ) {
            Tika tika = new Tika();
            mimeType = tika.detect(path);
        }
        
        // Validate the MIME type
        if ( mimeType != null )
            return VALID_MIME_TYPES.get(sourceFormat).contains(
                    mimeType.toUpperCase());
        
        return false;
        
    }

}
