package ai.hyperlearning.ontopop.api.ontology.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataParsingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDataPropertyGraphModellingException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceFormatException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidSourceOntologyDataException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyMapperInvalidTargetFormatException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeAuthenticationException;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeInvalidProjectId;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeMissingCredentials;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeProjectAccessException;
import ai.hyperlearning.ontopop.owl.mappers.Mapper;
import ai.hyperlearning.ontopop.webprotege.WebProtegeDownloader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Mapping API Service - Mapping Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Controller
@RequestMapping("/mapping")
@Tag(name = "Mapping API", description = "API for undertaking common mapping operations")
public class OntologyMappingController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyMappingController.class);
    
    @Autowired
    private WebProtegeDownloader webProtegeDownloader;
    
    /**************************************************************************
     * 1. POST - Ontology Data Mapper
     *************************************************************************/
    
    @Operation(
            summary = "Ontology Data Mapper",
            description = "Map given ontology data from a given source format "
                    + "(e.g. RDF/XML) to a given target format (e.g. GRAPHSON).",
            tags = {"ontology", "mapping", "rdfxml", "graphson"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology data successfully mapped."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Ontology data mapping request invalid."), 
                    @ApiResponse(
                            responseCode = "401",
                            description = "Ontology data mapping request unauthorized."), 
                    @ApiResponse(
                            responseCode = "403",
                            description = "Ontology data mapping request forbidden."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            value = "/map", 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    MediaType.TEXT_PLAIN_VALUE, 
                    "application/ld+json", 
                    "application/n-triples", 
                    "application/n-quads", 
                    "application/owl+xml", 
                    "application/rdf+xml", 
                    "application/trig", 
                    "text/turtle"
            })
    public ResponseEntity<String> map(
            @Parameter(description = "Source format", required = true)
            @RequestParam(name = "source", required = true) String source, 
            @Parameter(description = "Target format", required = true)
            @RequestParam(name = "target", required = true) String target,
            @Parameter(description = "Source ontology file", required = false)
            @RequestParam(required = false) MultipartFile file, 
            @Parameter(description = "WebProtege project ID", required = false)
            @RequestParam(name = "webProtegeId", required = false) String webProtegeId) 
                    throws IOException, 
                    OWLOntologyCreationException, 
                    OWLOntologyStorageException {
        
        LOGGER.debug("New HTTP POST request - Map ontology data from {} to {}.", 
                source, target);
        
        // Download and map a RDF/XML OWL file exported from WebProtege
        String webProtegeDownloadedOwlFile = null;
        if ( webProtegeId != null ) {
            
            // Run the WebProtege downloader service
            try {
                webProtegeDownloadedOwlFile = webProtegeDownloader.run(
                        webProtegeId, null, null, false);
            } catch ( WebProtegeMissingCredentials |
                    WebProtegeAuthenticationException e) {
                return new ResponseEntity<>(e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR);
            } catch ( WebProtegeInvalidProjectId e ) {
                return new ResponseEntity<>(e.getMessage(), 
                        HttpStatus.BAD_REQUEST);
            } catch ( WebProtegeProjectAccessException e ) {
                return new ResponseEntity<>(e.getMessage(), 
                        HttpStatus.FORBIDDEN);
            }
            
        }
        
        // Map a given ontology file
        if ( (file != null && !file.isEmpty()) || 
                (webProtegeDownloadedOwlFile != null) ) {
            
            // Map the ontology file
            String mapped = null;
            Path temporaryFile = null;
            try {
                
                // Map the downloaded WebProtege OWL file
                if ( webProtegeDownloadedOwlFile != null )
                    mapped = Mapper.map("rdf-xml", target, 
                            webProtegeDownloadedOwlFile);
                else {
                    
                    // Convert the uploaded MultipartFile to a File and map
                    temporaryFile = Files.createTempFile("", 
                            file.getOriginalFilename());
                    file.transferTo(temporaryFile);
                    mapped = Mapper.map(source, target, 
                            temporaryFile.toAbsolutePath().toString());
                    
                }
                
                // Return the mapping result
                return new ResponseEntity<>(mapped, HttpStatus.OK);
            
            } catch (OntologyMapperInvalidSourceFormatException | 
                    OntologyMapperInvalidSourceOntologyDataException | 
                    OntologyMapperInvalidTargetFormatException e) {
                return new ResponseEntity<>(e.getMessage(), 
                        HttpStatus.BAD_REQUEST);
            } catch (OntologyDataParsingException | 
                    OntologyDataPropertyGraphModellingException e) {
                return new ResponseEntity<>(e.getMessage(), 
                        HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                
                // Delete the OWL file
                try {
                    if ( webProtegeDownloadedOwlFile != null )
                        Files.deleteIfExists(
                                Paths.get(webProtegeDownloadedOwlFile));
                    else
                        Files.deleteIfExists(temporaryFile);
                } catch (Exception e) {
                    
                }
                
            }
            
        }
        
        // If neither a WebProtege project ID or ontology file is provided
        else return new ResponseEntity<>("Neither a WebProtege project ID nor "
                + "an ontology data file were provided.", 
                HttpStatus.BAD_REQUEST);
        
    }

}
