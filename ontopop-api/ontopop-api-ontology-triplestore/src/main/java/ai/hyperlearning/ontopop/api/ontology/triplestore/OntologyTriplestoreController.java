package ai.hyperlearning.ontopop.api.ontology.triplestore;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.ontology.diff.OntologyDiffService;
import ai.hyperlearning.ontopop.data.ontology.downloader.OntologyDownloaderService;
import ai.hyperlearning.ontopop.data.ontology.git.OntologyGitPushService;
import ai.hyperlearning.ontopop.data.ontology.management.OntologyManagementService;
import ai.hyperlearning.ontopop.exceptions.git.GitWebhookNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDiffInvalidRequestException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDownloadException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.exceptions.triplestore.InvalidSparqlQueryException;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyLeftRightDiff;
import ai.hyperlearning.ontopop.model.owl.diff.SimpleOntologyTimestampDiff;
import ai.hyperlearning.ontopop.model.triplestore.OntologyTriplestoreSparqlQuery;
import ai.hyperlearning.ontopop.owl.mappers.OntologyDataMapper;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthenticationEngine;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthentication;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthenticationFactory;
import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyUtils;
import ai.hyperlearning.ontopop.triplestore.TriplestoreService;
import ai.hyperlearning.ontopop.triplestore.TriplestoreServiceFactory;
import ai.hyperlearning.ontopop.triplestore.TriplestoreServiceType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/triplestore/ontologies")
@Tag(name = "Triplestore API", description = "API for querying the OntoPop RDF Triplestore")
public class OntologyTriplestoreController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyTriplestoreController.class);
    
    private static final String RESPONSE_HEADER_LATEST_GIT_WEBHOOK_ID = 
            "x-ontopop-latest-git-webhook-id";
    private static final DateTimeFormatter DIFF_TIMESTAMP_DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private OntologyManagementService ontologyManagementService;
    
    @Autowired
    private TriplestoreServiceFactory triplestoreServiceFactory;
    
    @Autowired
    private OntologyDownloaderService ontologyDownloaderService;
    
    @Autowired
    private OntologyRepository ontologyRepository;
    
    @Autowired
    private GitWebhookRepository gitWebhookRepository;
    
    @Autowired
    private OntologyDiffService ontologyDiffService;
    
    @Autowired
    private ApiKeyAuthenticationFactory apiKeyAuthenticationServiceFactory;
    
    @Autowired
    private OntologyGitPushService ontologyGitPushService;
    
    @Value("${storage.triplestore.service}")
    private String storageTriplestoreService;
    
    @Value("${security.authentication.api.enabled:false}")
    private Boolean apiAuthenticationEnabled;
    
    @Value("${security.authentication.api.engine:secrets}")
    private String apiAuthenticationEngine;
    
    private TriplestoreService triplestoreService;
    
    private ApiKeyAuthentication apiKeyAuthenticationService = null;
    
    @PostConstruct
    private void postConstruct() {
        
        // Instantiate the triplestore service
        TriplestoreServiceType triplestoreServiceType = TriplestoreServiceType
                .valueOfLabel(storageTriplestoreService.toUpperCase());
        triplestoreService = triplestoreServiceFactory
                .getTriplestoreService(triplestoreServiceType);
        LOGGER.info("Using the {} triplestore service.",
                triplestoreServiceType);
        
        // Instantiate the API Key authentication service
        if ( Boolean.TRUE.equals(apiAuthenticationEnabled) ) {
            ApiKeyAuthenticationEngine apiKeyAuthenticationServiceType =
                    ApiKeyAuthenticationEngine
                            .valueOfLabel(apiAuthenticationEngine.toUpperCase());
            apiKeyAuthenticationService = apiKeyAuthenticationServiceFactory
                    .getApiKeyAuthenticationService(apiKeyAuthenticationServiceType);
            LOGGER.info("Using the {} API Key authentication service.",
                    apiKeyAuthenticationServiceType);
        }
        
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
            value = "/{id}/query/sparql", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    "application/sparql-results+json", 
                    "application/sparql-results+xml"})
    public ResponseEntity<String> query(
            HttpServletResponse response, 
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
        
        // Parse the JSON request body and get the SPARQL query
        if ( ontologyTriplestoreSparqlQuery == null || 
                StringUtils.isBlank(ontologyTriplestoreSparqlQuery.getQuery()) )
            throw new InvalidSparqlQueryException();
        String sparqlQuery = ontologyTriplestoreSparqlQuery.getQuery();
        LOGGER.debug("New HTTP POST request - SPARQL query request for "
                + "ontology ID {}: {}", id, sparqlQuery);
        
        // Execute the SPARQL query
        response.addHeader(RESPONSE_HEADER_LATEST_GIT_WEBHOOK_ID, 
                String.valueOf(ontologyManagementService
                        .getLatestGitWebhookId(id)));
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
            value = "/{id}/query/sparql", 
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    "application/sparql-results+json", 
                    "application/sparql-results+xml"})
    public ResponseEntity<String> query(
            HttpServletResponse response, 
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
        
        // Parse the form data and get the SPARQL query
        if ( formData == null || !formData.containsKey("query") || 
                StringUtils.isBlank(formData.getFirst("query")) )
            throw new InvalidSparqlQueryException();
        String sparqlQuery = formData.getFirst("query");
        LOGGER.debug("New HTTP POST request - SPARQL query request for "
                + "ontology ID {}: {}", id, sparqlQuery);
        
        // Execute the SPARQL query
        response.addHeader(RESPONSE_HEADER_LATEST_GIT_WEBHOOK_ID, 
                String.valueOf(ontologyManagementService
                        .getLatestGitWebhookId(id)));
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
            value = "/{id}/data/rdf", 
            produces = {
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE, 
                    MediaType.TEXT_PLAIN_VALUE, 
                    "application/n-quads", 
                    "application/ld+json", 
                    "application/trig"})
    public ResponseEntity<String> getRdfData(
            HttpServletResponse response, 
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
        response.addHeader(RESPONSE_HEADER_LATEST_GIT_WEBHOOK_ID, 
                String.valueOf(ontologyManagementService
                        .getLatestGitWebhookId(id)));
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
            value = "/{id}/data/owl", 
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getOwlData(
            HttpServletResponse response, 
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
                            .orElseThrow(GitWebhookNotFoundException::new);
            if ( gitWebhook != null ) {
                
                // Download the OWL file from persistent storage
                String downloadedUri = ontologyDownloaderService
                        .retrieveOwlFile(gitWebhook);
                
                // Return the contents of the OWL file as a string
                response.addHeader(RESPONSE_HEADER_LATEST_GIT_WEBHOOK_ID, 
                        String.valueOf(ontologyManagementService
                                .getLatestGitWebhookId(id)));
                return new ResponseEntity<>(
                        FileUtils.readFileToString(
                                new File(downloadedUri), StandardCharsets.UTF_8), 
                        HttpStatus.OK);
                
            } else 
                throw new OntologyDownloadException();
            
        } catch (Exception e) {
            
            LOGGER.error("An error was encountered when attempting to "
                    + "retrieve the OWL file for ontology ID {}.", id, e);
            throw new OntologyDownloadException();
            
        }
        
    }
    
    /**************************************************************************
     * 2.3. GET - Get TEMPORAL DIFF
     *************************************************************************/
    
    @Operation(
            summary = "Get temporal ontological diff",
            description = "Perform a temporal ontological diff given an "
                    + "ontology ID and a timestamp.",
            tags = {"ontology", "diff"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontological temporal diff successfully processed.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = SimpleOntologyTimestampDiff.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid timestamp.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Ontological temporal diff operation unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}/diff/temporal", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleOntologyTimestampDiff getTemporalDiff(
            @Parameter(
                    description = "ID of the ontology on which to perform the diff.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @Parameter(
                    description = "Timestamp (UTC) to use for processing the temporal "
                            + "ontological diff.", 
                    required = false)
            @RequestParam(name = "timestamp", required = false) String timestamp, 
            @Parameter(
                    description = "Git webhook ID to use for processing the temporal "
                            + "ontological diff.", 
                    required = false)
            @RequestParam(name = "gitWebhookId", required = false) Long gitWebhookId) {
        
        // Timestamp
        if ( timestamp != null ) {
            LOGGER.debug("New HTTP GET request - Temporal DIFF for "
                    + "ontology ID {} given timestamp {}.", id, timestamp);
            try {
                LocalDateTime requestedTimestamp = LocalDateTime.parse(
                        timestamp, DIFF_TIMESTAMP_DATE_TIME_FORMATTER);
                return ontologyDiffService.run(id, requestedTimestamp);
            } catch (DateTimeParseException e) {
                throw new OntologyDiffInvalidRequestException(
                        OntologyDiffInvalidRequestException
                            .ErrorKey.INVALID_TIMESTAMP);
            }
        } 
        
        // Git webhook ID
        else if ( gitWebhookId != null ) {
            LOGGER.debug("New HTTP GET request - Temporal DIFF for "
                    + "ontology ID {} given Git webhhok ID {}.", id, gitWebhookId);
            return ontologyDiffService.run(id, gitWebhookId);
        } 
        
        // Invalid request parameters
        else
            throw new OntologyDiffInvalidRequestException(
                    OntologyDiffInvalidRequestException
                        .ErrorKey.MISSING_PARAMETER);
        
    }
    
    /**************************************************************************
     * 2.4. GET - Get LEFT-RIGHT DIFF
     *************************************************************************/
    
    @Operation(
            summary = "Get left-right ontological diff",
            description = "Perform a side-by-side ontological diff given an "
                    + "ontology ID and two Git webhook IDs.",
            tags = {"ontology", "diff"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontological left-right diff successfully processed.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = SimpleOntologyLeftRightDiff.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Ontological left-right diff operation unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology and/or Git webhooks not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}/diff/compare", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleOntologyLeftRightDiff getLeftRightDiff(
            @Parameter(
                    description = "ID of the ontology on which to perform the diff.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @Parameter(
                    description = "Left Git webhook ID for processing the "
                            + "left-right ontological diff.", 
                    required = true)
            @RequestParam(name = "leftGitWebhookId", required = true) long leftGitWebhookId, 
            @Parameter(
                    description = "Right Git webhook ID for processing the "
                            + "left-right ontological diff.", 
                    required = true)
            @RequestParam(name = "rightGitWebhookId", required = true) long rightGitWebhookId) {
        
        LOGGER.debug("New HTTP GET request - Left-Right DIFF for "
                + "ontology ID {} given left Git webhook ID {} and "
                + "right Git webhook ID {}.", id, 
                leftGitWebhookId, rightGitWebhookId);
        return ontologyDiffService.run(id, leftGitWebhookId, rightGitWebhookId); 
        
    }
    
    /**************************************************************************
     * 3.1. PUT - Put Ontology Data
     *************************************************************************/
    
    @Operation(
            summary = "Put ontology data",
            description = "Overwrite or create the Git-managed ontology data "
                    + "file for the given ontology ID.",
            tags = {"ontology", "owl", "rdf", "xml", "data"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology data successfully written.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Ontology data operation unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(
            value = "/{id}", 
            consumes = {
                    MediaType.TEXT_PLAIN_VALUE, 
                    MediaType.APPLICATION_JSON_VALUE, 
                    MediaType.APPLICATION_XML_VALUE}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putOntologyData(
            Principal principal, 
            @Parameter(
                    description = "ID of the ontology to overwrite.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @RequestParam(name = "format", required = true) String format, 
            @RequestParam(name = "client", required = false) String client, 
            @RequestParam(name = "author", required = true) String author, 
            @RequestParam(name = "message", required = false) String message, 
            @RequestBody(required = true) String ontologyData) 
                    throws Exception {
        
        LOGGER.debug("New HTTP PUT request - Put ontology data for "
                + "ontology ID {}.", id);
        
        // Get the ontology
        Ontology ontology = ontologyRepository.findById(id)
                .orElseThrow(OntologyNotFoundException::new);
        
        // Get the client name
        String clientName = ApiKeyUtils.getClientName(
                apiAuthenticationEnabled, apiKeyAuthenticationService, 
                principal.getName(), client);
            
        // Get the latest Git webhook object for the given ontology
        GitWebhook gitWebhook = ontologyDownloaderService
                .getLatestGitWebhook(id);
        if ( gitWebhook != null ) {
            
            // Download the latest OWL file from persistent storage
            // and load it as a string object
            String downloadedOwlFileUri = ontologyDownloaderService
                    .retrieveOwlFile(gitWebhook);
            String existingOwlRdfXml = FileUtils.readFileToString(
                    new File(downloadedOwlFileUri), StandardCharsets.UTF_8);
            
            // Download the latest parsed SimpleOntology JSON file from 
            // persistent storage and load it as a SimpleOntology object
            String downloadedSimpleOntologyFileUri = ontologyDownloaderService
                    .retrieveParsedSimpleOntologyFile(gitWebhook);
            ObjectMapper mapper = new ObjectMapper();
            SimpleOntology existingSimpleOntology = mapper.readValue(
                    new File(downloadedSimpleOntologyFileUri), 
                    SimpleOntology.class);
            
            // Generate the RDF/XML string
            String targetOwlRdfXml = OntologyDataMapper.toOwlRdfXml(
                    format, ontologyData, clientName, 
                    existingOwlRdfXml, existingSimpleOntology);
                
            // Push the RDF/XML string to the Git repository
            ontologyGitPushService.run(ontology, targetOwlRdfXml,
                    author, message);
            return new ResponseEntity<>("Ontology data successfully "
                    + "written.", HttpStatus.OK);
            
        } else 
            throw new OntologyDownloadException();
        
    }
    
}
