package ai.hyperlearning.ontopop.owl.mappers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPropertyGraphModellingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceOntologyDataException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidTargetFormatException;

/**
 * Ontology data (e.g. RDF/XML) to target format (e.g. GRAPHSON) mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class Mapper {
    
    private static final long MAX_FILE_SIZE_BYTES = 1048576;
    
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
     */
    
    public static String map(String source, String target, 
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
        
        // Map the source ontology data into the given target format
        switch (sourceFormat) {
            case RDF_XML:
                return RdfXmlMapper.map(ontologyFile, target);
            default:
                throw new OntologyMapperInvalidSourceFormatException();
        }
        
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
