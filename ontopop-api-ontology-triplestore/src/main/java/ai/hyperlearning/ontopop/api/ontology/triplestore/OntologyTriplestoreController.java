package ai.hyperlearning.ontopop.api.ontology.triplestore;

import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.data.ontology.downloader.OntologyDownloaderService;
import ai.hyperlearning.ontopop.exceptions.git.GitWebhookNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDownloadException;
import ai.hyperlearning.ontopop.exceptions.triplestore.InvalidSparqlQueryException;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.triplestore.OntologyTriplestoreSparqlQuery;
import ai.hyperlearning.ontopop.triplestore.TriplestoreService;
import ai.hyperlearning.ontopop.triplestore.TriplestoreServiceFactory;
import ai.hyperlearning.ontopop.triplestore.TriplestoreServiceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Triplestore API Service - Triplestiore Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "Triplestore API", description = "API for querying the OntoPop RDF Triplestore")
public class OntologyTriplestoreController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyTriplestoreController.class);
    
    @Autowired
    private TriplestoreServiceFactory triplestoreServiceFactory;
    
    @Autowired
    private OntologyDownloaderService ontologyDownloaderService;
    
    @Autowired
    private GitWebhookRepository gitWebhookRepository;
    
    @Value("${storage.triplestore.service}")
    private String storageTriplestoreService;
    
    private TriplestoreService triplestoreService;
    
    @PostConstruct
    private void postConstruct() {
        
        // Instantiate the triplestore service
        TriplestoreServiceType triplestoreServiceType = TriplestoreServiceType
                .valueOfLabel(storageTriplestoreService.toUpperCase());
        triplestoreService = triplestoreServiceFactory
                .getTriplestoreService(triplestoreServiceType);
        LOGGER.debug("Using the {} triplestore service.",
                triplestoreServiceType);
        
    }
    
    /**************************************************************************
     * 1. POST - Triplestore SPARQL Query
     * 
     * Supported content types:
     *  application/sparql-results+json (default)
     *  application/sparql-results+xml
     *************************************************************************/
    
    /**
     * 1.1. JSON Request Body
     * @param acceptHeader
     * @param id
     * @param ontologyTriplestoreSparqlQuery
     * @return
     */
    
    @Operation(
            summary = "Triplestore SPARQL Query",
            description = "Execute a general SPARQL query against the "
                    + "ontology with the given ontology ID.",
            tags = {"ontology", "triplestore", "sparql"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SPARQL query successfully executed."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "SPARQL query request unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            value = "/{id}/triplestore/data/query/sparql", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    "application/sparql-results+json", 
                    "application/sparql-results+xml"})
    public ResponseEntity<String> query(
            @Parameter(
                    description = "Content type that should be returned.", 
                    required = false)
            @RequestHeader(value = "Accept", required = false) String acceptHeader, 
            @Parameter(
                    description = "ID of the ontology to query.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @Parameter(
                    description = "SPARQL query.", 
                    required = true, 
                    schema = @Schema(implementation = OntologyTriplestoreSparqlQuery.class))
            @Valid @RequestBody(required = true) OntologyTriplestoreSparqlQuery ontologyTriplestoreSparqlQuery) {
        String sparqlQuery = null;
        
        // JSON Request Body
        if ( ontologyTriplestoreSparqlQuery != null ) {
            if ( !ontologyTriplestoreSparqlQuery.getQuery().isBlank() ) 
                sparqlQuery = ontologyTriplestoreSparqlQuery.getQuery();
        }
        
        // Otherwise throw an invalid SPARQL exception
        if ( sparqlQuery == null )
            throw new InvalidSparqlQueryException("Invalid SPARQL query.");
        
        LOGGER.debug("New HTTP POST request - SPARQL query request for "
                + "ontology ID {}: {}", id, sparqlQuery);
        return triplestoreService.query(id, sparqlQuery, acceptHeader);
        
    }
    
    /**
     * X-WWW-FORM-URLENCODED request body
     * @param acceptHeader
     * @param id
     * @param formData
     * @return
     */
    
    @Operation(
            summary = "Triplestore SPARQL Query",
            description = "Execute a general SPARQL query against the "
                    + "ontology with the given ontology ID.",
            tags = {"ontology", "triplestore", "sparql"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "SPARQL query successfully executed."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "SPARQL query request unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            value = "/{id}/triplestore/data/query/sparql", 
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    "application/sparql-results+json", 
                    "application/sparql-results+xml"})
    public ResponseEntity<String> query(
            @Parameter(
                    description = "Content type that should be returned.", 
                    required = false)
            @RequestHeader(value = "Accept", required = false) String acceptHeader, 
            @Parameter(
                    description = "ID of the ontology to query.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @Parameter(
                    description = "SPARQL query.", 
                    required = true)
            @RequestBody(required = true) MultiValueMap<String, String> formData) {
        String sparqlQuery = null;
        
        // Form Data
        if ( formData != null ) {
            if ( formData.containsKey("query") && 
                    !formData.getFirst("query").isBlank() )
                sparqlQuery = formData.getFirst("query");
        }
        
        // Otherwise throw an invalid SPARQL exception
        if ( sparqlQuery == null )
            throw new InvalidSparqlQueryException("Invalid SPARQL query.");
        
        LOGGER.debug("New HTTP POST request - SPARQL query request for "
                + "ontology ID {}: {}", id, sparqlQuery);
        return triplestoreService.query(id, sparqlQuery, acceptHeader);
        
    }
    
    /**************************************************************************
     * 2.1. GET - Get all RDF data
     * 
     * Supported content types:
     *  application/n-quads
     *  application/ld+json
     *  application/trig (default)
     *************************************************************************/
    
    @Operation(
            summary = "Get Triplestore RDF Data",
            description = "Get all the RDF data via the graph store protocol "
                    + "for the ontology with the given ontology ID.",
            tags = {"ontology", "triplestore", "rdf"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "RDF data successfully retrieved."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of RDF data unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}/triplestore/data/rdf", 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    MediaType.TEXT_PLAIN_VALUE, 
                    "application/n-quads", 
                    "application/ld+json", 
                    "application/trig"})
    public ResponseEntity<String> getRdfData(
            @Parameter(
                    description = "Content type that should be returned.", 
                    required = false)
            @RequestHeader(value = "Accept", required = false) String acceptHeader, 
            @Parameter(
                    description = "ID of the ontology for which to retrieve data.", 
                    required = true)
            @PathVariable(required = true) int id) {
        LOGGER.debug("New HTTP GET request - Get triplestore RDF data "
                + "for ontology ID {}.", id);
        return triplestoreService.getData(id, acceptHeader);
    }
    
    /**************************************************************************
     * 2.2. GET - Get OWL RDF/XML data
     *************************************************************************/
    
    @Operation(
            summary = "Get OWL Data",
            description = "Get the original OWL data for the ontology with "
                    + "the given ontology ID and optional Git webhook ID.",
            tags = {"ontology", "triplestore", "owl"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OWL data successfully retrieved."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of OWL data unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}/triplestore/data/owl", 
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getOwlData(
            @Parameter(
                    description = "ID of the ontology for which to retrieve data.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @RequestParam(name = "gitWebhookId", required = false, defaultValue = "-1") long gitWebhookId) {
        
        LOGGER.debug("New HTTP GET request - Get OWL data for "
                + "ontology ID {}.", id);
        
        try {
            
            // Get the Git webhook event object
            GitWebhook gitWebhook = ( gitWebhookId == -1 ) ? 
                    ontologyDownloaderService.getLatestGitWebhook(id) : 
                        gitWebhookRepository.findById(gitWebhookId)
                            .orElseThrow(() -> new GitWebhookNotFoundException(id));
            if ( gitWebhook != null ) {
                
                // Download the OWL file from persistent storage
                String downloadedUri = ontologyDownloaderService
                        .retrieveOwlFile(gitWebhook);
                
                // Return the contents of the OWL file as a string
                return new ResponseEntity<>(
                        FileUtils.readFileToString(
                                new File(downloadedUri), StandardCharsets.UTF_8), 
                        HttpStatus.OK);
                
            } else {
                throw new OntologyDownloadException();
            }
            
        } catch (Exception e) {
            
            LOGGER.error("An error was encountered when attempting to "
                    + "retrieve the OWL file for ontology ID {}.", id, e);
            throw new OntologyDownloadException();
            
        }
        
    }
    
}
