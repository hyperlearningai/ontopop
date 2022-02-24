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

import ai.hyperlearning.ontopop.data.jpa.repositories.WebProtegeWebhookRepository;
import ai.hyperlearning.ontopop.exceptions.webprotege.WebProtegeWebhookNotFoundException;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Management API Service - WebProtege Webhook Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/management/ontologies")
@Tag(name = "Ontology Management API - WebProtege Webhooks", description = "API for managing OntoPop ontology WebProtege webhooks")
public class OntologyWebProtegeWebhookManagementController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    OntologyWebProtegeWebhookManagementController.class);
    
    @Autowired
    private WebProtegeWebhookRepository webProtegeWebhookRepository;
    
    /**************************************************************************
     * 1.1. GET - Get WebProtege Webhooks
     *************************************************************************/

    @Operation(
            summary = "Get all WebProtege webhooks",
            description = "Get all the WebProtege webhooks consumed across all ontologies "
                    + "managed by OntoPop.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "WebProtege Webhooks successfully retrieved.", 
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = WebProtegeWebhook.class)))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of WebProtege webhooks unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/webhooks/webprotege",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WebProtegeWebhook> getWebProtegeWebhooks() {
        LOGGER.debug("New HTTP GET request: Get all WebProtege webhooks.");
        return (List<WebProtegeWebhook>) webProtegeWebhookRepository.findAll();
    }

    /**************************************************************************
     * 1.2. GET - Get WebProtege Webhook
     *************************************************************************/

    @Operation(
            summary = "Get a WebProtege webhook",
            description = "Get a WebProtege webhook consumed by OntoPop given its "
                    + "WebProtege webhook ID.",
            tags = {"ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "WebProtege Webhook successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = WebProtegeWebhook.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of WebProtege webhook unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "WebProtege Webhook not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/webhooks/webprotege/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebProtegeWebhook getWebProtegeWebhook(
            @Parameter(
                    description = "ID of the WebProtege webhook to retrieve.", 
                    required = true)
            @PathVariable(required = true) long id) {
        LOGGER.debug("New HTTP GET request: Get WebProtege webhook by ID.");
        return webProtegeWebhookRepository.findById(id)
                .orElseThrow(() -> new WebProtegeWebhookNotFoundException(id));
    }

}
