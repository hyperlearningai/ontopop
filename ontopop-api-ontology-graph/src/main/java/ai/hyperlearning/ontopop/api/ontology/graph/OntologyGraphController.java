package ai.hyperlearning.ontopop.api.ontology.graph;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.data.ontology.downloader.OntologyDownloaderService;
import ai.hyperlearning.ontopop.exceptions.git.GitWebhookNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDownloadException;
import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceFactory;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceType;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Graph API Service - Graph Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "Graph API", description = "API for querying the OntoPop Graph Database")
public class OntologyGraphController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphController.class);
    
    @Autowired
    private OntologyDownloaderService ontologyDownloaderService;
    
    @Autowired
    private GitWebhookRepository gitWebhookRepository;
    
    @Autowired
    private GraphDatabaseServiceFactory graphDatabaseServiceFactory;
    
    @Value("${storage.graph.service}")
    private String storageGraphService;
    
    private GraphDatabaseServiceType graphDatabaseServiceType;
    private GraphDatabaseService graphDatabaseService;
    
    @PostConstruct
    private void postConstruct() throws IOException {
        
        // Instantiate and open the relevant graph database service
        graphDatabaseServiceType = GraphDatabaseServiceType
                .valueOfLabel(storageGraphService.toUpperCase());
        graphDatabaseService = graphDatabaseServiceFactory
                .getGraphDatabaseService(graphDatabaseServiceType);
        graphDatabaseService.openGraph();
        LOGGER.debug("Using the {} graph database service.",
                graphDatabaseServiceType);
        
    }
    
    /**************************************************************************
     * 1. GET REQUESTS
     *************************************************************************/
    
    /**************************************************************************
     * 1.1. GET Graph
     *************************************************************************/
    
    @Operation(
            summary = "Get property graph",
            description = "Get the property graph model of an ontology "
                    + "given the ontology ID.",
            tags = {"ontology", "graph"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Property graph successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = SimpleOntologyPropertyGraph.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of property graph unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}/graph", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public SimpleOntologyPropertyGraph getGraph(
            @Parameter(
                    description = "ID of the ontology to retrieve as a property graph.", 
                    required = true)
            @PathVariable(required = true) int id, 
            @RequestParam(name = "gitWebhookId", required = false, defaultValue = "-1") long gitWebhookId) 
                    throws InterruptedException, ExecutionException {
        
        
        LOGGER.debug("New HTTP GET request: Get ontology property graph for "
                + "ontology ID: {}.", id);
        
        try {
            
            // Get the Git webhook event object
            GitWebhook gitWebhook = ( gitWebhookId == -1 ) ? 
                    ontologyDownloaderService.getLatestGitWebhook(id) : 
                        gitWebhookRepository.findById(gitWebhookId)
                            .orElseThrow(() -> new GitWebhookNotFoundException(id));
            if ( gitWebhook != null ) {
                
                // Download the property graph file from persistent storage
                String downloadedUri = ontologyDownloaderService
                        .retrieveModelledPropertyGraphFile(gitWebhook);
                
                // Deserialize the Simple Ontology Property Graph object from JSON
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(new File(downloadedUri),
                        SimpleOntologyPropertyGraph.class);
                
            } else {
                throw new OntologyDownloadException();
            }
            
        } catch (Exception e) {
            
            LOGGER.error("An error was encountered when attempting to retrieve "
                    + "the property graph file for ontology ID {}.", id, e);
            throw new OntologyDownloadException();
            
        }
    
    }

}
