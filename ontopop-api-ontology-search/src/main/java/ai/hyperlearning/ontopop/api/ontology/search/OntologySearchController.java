package ai.hyperlearning.ontopop.api.ontology.search;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.exceptions.search.InvalidSearchQueryException;
import ai.hyperlearning.ontopop.model.search.OntologySearchQuery;
import ai.hyperlearning.ontopop.search.SearchService;
import ai.hyperlearning.ontopop.search.SearchServiceFactory;
import ai.hyperlearning.ontopop.search.SearchServiceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Search API Service - Search Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/search/ontologies")
@Tag(name = "Search API", description = "API for querying the OntoPop Search Index")
public class OntologySearchController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologySearchController.class);
    
    @Autowired
    private SearchServiceFactory searchServiceFactory;
    
    @Value("${storage.search.service}")
    private String storageSearchService;
    
    @Value("${storage.search.indexNamePrefix}")
    private String searchIndexNamePrefix;
    
    private SearchServiceType searchServiceType;
    private SearchService searchService;
    
    @PostConstruct
    private void postConstruct() {
        
        // Instantiate the search service
        searchServiceType = SearchServiceType.valueOfLabel(
                storageSearchService.toUpperCase());
        searchService = searchServiceFactory.
                getSearchService(searchServiceType);
        LOGGER.debug("Using the {} search service.", searchServiceType);
        
    }
    
    /**************************************************************************
     * GET Search Query
     *************************************************************************/
    
    @Operation(
            summary = "Search Query",
            description = "Execute a basic search query against the "
                    + "ontology with the given ontology ID.",
            tags = {"ontology", "search"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search query successfully executed."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Search query request unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}", 
            consumes = MediaType.APPLICATION_JSON_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Object search(
            @Parameter(
                    description = "ID of the ontology to query.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @Parameter(
                    description = "Search query.", 
                    required = true, 
                    schema = @Schema(implementation = OntologySearchQuery.class))
            @Valid @RequestBody(required = true) OntologySearchQuery ontologySearchQuery) {
        
        LOGGER.debug("New HTTP GET request - Search query request for "
                + "ontology ID {}: {}", id, ontologySearchQuery);
        if ( ontologySearchQuery != null && 
                !ontologySearchQuery.getQuery().isBlank() ) {
            
            // Execute the search query using the 
            // Spring Data High-Level REST client
            String indexName = searchIndexNamePrefix + id;
            
            // At least one field to search has been provided
            if ( !ontologySearchQuery.getFields().isEmpty() ) {
                
                // Prefix the search fields to match the nested properties
                // map as defined in the SimpleIndexVertex.class model
                if (searchServiceType.equals(SearchServiceType.ELASTICSEARCH)) {
                    ontologySearchQuery.prefixFields();
                    LOGGER.debug("Transformed search query request for "
                            + "Elasticsearch: {}", ontologySearchQuery);
                }
                
                // Execute a multi-match search
                return ontologySearchQuery.getFields().size() > 1 ? 
                        searchService.search(indexName, 
                            ontologySearchQuery.getFields(), 
                            ontologySearchQuery.getQuery(), 
                            ontologySearchQuery.isAnd()) :
                                searchService.search(indexName, 
                                    ontologySearchQuery.getFields().get(0), 
                                    ontologySearchQuery.getQuery(), 
                                    ontologySearchQuery.isExact(), 
                                    ontologySearchQuery.isAnd());
                
            }
            
            // No fields have been provided, in which case search
            // across all the fields
            else return searchService.search(indexName, 
                    ontologySearchQuery.getQuery());
            
        }
        
        // Throw an invalid search query exception
        throw new InvalidSearchQueryException("Invalid Search query.");
        
    }

}
