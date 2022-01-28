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

import ai.hyperlearning.ontopop.data.jpa.repositories.WebhookEventRepository;
import ai.hyperlearning.ontopop.exceptions.git.WebhookEventNotFoundException;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Management API Service - Webhook Event Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "webhook", description = "Ontology Management API")
public class OntologyWebhookEventManagementController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OntologyWebhookEventManagementController.class);

    @Autowired
    private WebhookEventRepository webhookEventRepository;

    /**************************************************************************
     * 1.1. GET - Get Webhooks
     *************************************************************************/

    @Operation(
            summary = "Get all webhooks",
            description = "Get all the webhooks consumed across all ontologies "
                    + "managed by OntoPop.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Webhooks successfully retrieved.", 
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = WebhookEvent.class)))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of webhooks unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/webhooks",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WebhookEvent> getWebhooks() {
        LOGGER.debug("New HTTP GET request: Get all webhooks.");
        return (List<WebhookEvent>) webhookEventRepository.findAll();
    }

    /**************************************************************************
     * 1.2. GET - Get Webhook
     *************************************************************************/

    @Operation(
            summary = "Get a webhook",
            description = "Get a webhook consumed by OntoPop given its "
                    + "webhook ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Webhook successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = WebhookEvent.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of webhook unauthorized."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Webhook not found."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/webhooks/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebhookEvent getWebhook(
            @Parameter(
                    description = "ID of the webhook to retrieve.", 
                    required = true)
            @PathVariable(required = true) long id) {
        LOGGER.debug("New HTTP GET request: Get webhook by ID.");
        return webhookEventRepository.findById(id)
                .orElseThrow(() -> new WebhookEventNotFoundException(id));
    }

    /**************************************************************************
     * 1.3. GET - Get Ontology Webhooks
     *************************************************************************/

    @Operation(
            summary = "Get all ontology webhooks",
            description = "Get all webhooks for a specific ontology consumed by "
                    + "OntoPop given the ontology ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology webhooks successfully retrieved.", 
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = WebhookEvent.class)))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of ontology webhooks unauthorized."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{ontologyId}/webhooks",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WebhookEvent> getOntologyWebhooks(
            @Parameter(
                    description = "ID of the ontology whose webhooks will be retrieved.", 
                    required = true)
            @PathVariable(required = true) int ontologyId) {
        LOGGER.debug("New HTTP GET request: Get all ontology webhooks.");
        return webhookEventRepository.findByOntologyId(ontologyId);
    }

    /**************************************************************************
     * 1.4. GET - Get Ontology Webhook
     *************************************************************************/

    @Operation(
            summary = "Get ontology webhook",
            description = "Get a specific webhook for a specific ontology "
                    + "consumed by OntoPop given the ontology ID and "
                    + "webhook event ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology webhook successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = WebhookEvent.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of ontology webhook unauthorized."),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology webhook not found."), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{ontologyId}/webhooks/{webhookEventId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebhookEvent getOntologyWebhook(
            @Parameter(
                    description = "ID of the ontology whose webhook will be retrieved.", 
                    required = true)
            @PathVariable(required = true) int ontologyId,
            @Parameter(
                    description = "ID of the webhook to be retrieved associated with this ontology.", 
                    required = true)
            @PathVariable(required = true) long webhookEventId) {
        LOGGER.debug("New HTTP GET request: Get ontology webhooks.");
        return webhookEventRepository
                .findByOntologyIdAdnWebhookEventId(ontologyId, webhookEventId)
                .orElseThrow(() -> new WebhookEventNotFoundException(
                        webhookEventId));
    }

}
