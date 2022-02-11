package ai.hyperlearning.ontopop.api.ontology.management;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.exceptions.git.GitWebhookNotFoundException;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Management API Service - Git Webhook Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "Ontology Management API - Git Webhooks", description = "API for managing OntoPop ontology Git webhooks")
public class OntologyGitWebhookManagementController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OntologyGitWebhookManagementController.class);

    @Autowired
    private GitWebhookRepository gitWebhookRepository;

    /**************************************************************************
     * 1.1. GET - Get Git Webhooks
     *************************************************************************/

    @Operation(
            summary = "Get all Git webhooks",
            description = "Get all the Git webhooks consumed across all ontologies "
                    + "managed by OntoPop.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Git Webhooks successfully retrieved.", 
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = GitWebhook.class)))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of Git webhooks unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/webhooks/git",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GitWebhook> getGitWebhooks() {
        LOGGER.debug("New HTTP GET request: Get all Git webhooks.");
        return (List<GitWebhook>) gitWebhookRepository.findAll();
    }

    /**************************************************************************
     * 1.2. GET - Get Git Webhook
     *************************************************************************/

    @Operation(
            summary = "Get a Git webhook",
            description = "Get a Git webhook consumed by OntoPop given its "
                    + "Git webhook ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Git Webhook successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = GitWebhook.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of Git webhook unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Git Webhook not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/webhooks/git/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GitWebhook getGitWebhook(
            @Parameter(
                    description = "ID of the Git webhook to retrieve.", 
                    required = true)
            @PathVariable(required = true) long id) {
        LOGGER.debug("New HTTP GET request: Get Git webhook by ID.");
        return gitWebhookRepository.findById(id)
                .orElseThrow(() -> new GitWebhookNotFoundException(id));
    }

    /**************************************************************************
     * 1.3. GET - Get Ontology Git Webhooks
     *************************************************************************/

    @Operation(
            summary = "Get all ontology Git webhooks",
            description = "Get all Git webhooks for a specific ontology consumed by "
                    + "OntoPop given the ontology ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology Git webhooks successfully retrieved.", 
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = GitWebhook.class)))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of ontology Git webhooks unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{ontologyId}/webhooks/git",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GitWebhook> getOntologyGitWebhooks(
            @Parameter(
                    description = "ID of the ontology whose Git webhooks will be retrieved.", 
                    required = true)
            @PathVariable(required = true) int ontologyId) {
        LOGGER.debug("New HTTP GET request: Get all ontology Git webhooks.");
        return gitWebhookRepository.findByOntologyId(ontologyId);
    }

    /**************************************************************************
     * 1.4. GET - Get Ontology Git Webhook
     *************************************************************************/

    @Operation(
            summary = "Get ontology Git webhook",
            description = "Get a specific Git webhook for a specific ontology "
                    + "consumed by OntoPop given the ontology ID and "
                    + "Git webhook ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology Git webhook successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = GitWebhook.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of ontology Git webhook unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology Git webhook not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{ontologyId}/webhooks/git/{gitWebhookId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GitWebhook getOntologyGitWebhook(
            @Parameter(
                    description = "ID of the ontology whose Git webhook will be retrieved.", 
                    required = true)
            @PathVariable(required = true) int ontologyId,
            @Parameter(
                    description = "ID of the Git webhook to be retrieved associated with this ontology.", 
                    required = true)
            @PathVariable(required = true) long gitWebhookId) {
        LOGGER.debug("New HTTP GET request: Get ontology Git webhooks.");
        return gitWebhookRepository
                .findByOntologyIdAndGitWebhookId(ontologyId, gitWebhookId)
                .orElseThrow(() -> new GitWebhookNotFoundException(
                        gitWebhookId));
    }

}
