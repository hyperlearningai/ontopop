package ai.hyperlearning.ontopop.api.ontology.triplestore;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.exceptions.triplestore.TriplestoreDataException;
import ai.hyperlearning.ontopop.exceptions.triplestore.TriplestoreSparqlQueryException;
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
@Tag(name = "triplestore", description = "Triplestore API")
public class OntologyTriplestoreController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyTriplestoreController.class);
    
    @Autowired
    private TriplestoreServiceFactory triplestoreServiceFactory;
    
    @Value("${storage.triplestore.service}")
    private String storageTriplestoreService;
    
    private TriplestoreService triplestoreService;
    
    @PostConstruct
    private void postConstruct() {
        TriplestoreServiceType triplestoreServiceType = TriplestoreServiceType
                .valueOfLabel(storageTriplestoreService.toUpperCase());
        triplestoreService = triplestoreServiceFactory
                .getTriplestoreService(triplestoreServiceType);
        LOGGER.debug("Using the {} triplestore service.",
                triplestoreServiceType);
    }
    
    /**************************************************************************
     * 1. POST - Triplestore SPARQL Query
     *************************************************************************/
    
    @Operation(
            summary = "Triplestore SPARQL Query",
            description = "Execute a general SPARQL query against the "
                    + "OntoPop triplestore loaded with the ontology for the "
                    + "given ontology ID.",
            tags = {"ontology", "triplestore"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "SPARQL query successfully executed."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "SPARQL query request unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            value = "/{id}/triplestore/query/sparql", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(
            @Parameter(
                    description = "ID of the ontology to query.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @Parameter(
                    description = "SPARQL query.", 
                    required = true, 
                    schema = @Schema(implementation = OntologyTriplestoreSparqlQuery.class))
            @Valid @RequestBody(required = true) OntologyTriplestoreSparqlQuery ontologyTriplestoreSparqlQuery) {
        String sparqlQuery = ontologyTriplestoreSparqlQuery.getQuery();
        LOGGER.debug("New HTTP POST request - SPARQL query request for ontology ID {}: {}", 
                id, sparqlQuery);
        try {
            return triplestoreService.query(id, sparqlQuery);
        } catch (IOException e) {
            LOGGER.error("An error was encountered when attempting to execute "
                    + "the SPARQL query '{}' against the triplestore for "
                    + "ontology ID {}.", sparqlQuery, id, e);
            throw new TriplestoreSparqlQueryException();
        }   
    }
    
    /**************************************************************************
     * 2. GET - Get all ontology data in graph store protocol format
     *************************************************************************/
    
    @Operation(
            summary = "Get Triplestore Graph Store Protocol Data",
            description = "Get all the graph store protocol data in the "
                    + "OntoPop triplestore loaded with the ontology for the "
                    + "given ontology ID.",
            tags = {"ontology", "triplestore"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Graph store protocol data successfully retrieved."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of graph store protocol data unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}/triplestore/data", 
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getData(
            @Parameter(
                    description = "ID of the ontology for which to retrieve data.", 
                    required = true)
            @PathVariable(required = true) int id) {
        LOGGER.debug("New HTTP GET request - Get Graph Store Protocol data "
                + "for ontology ID {}.", id);
        try {
            return triplestoreService.getData(id);
        } catch (IOException e) {
            LOGGER.error("An error was encountered when attempting to "
                    + "retrieve the graph store protocol data for "
                    + "ontology ID {}.", id, e);
            throw new TriplestoreDataException();
        }
    }

}
