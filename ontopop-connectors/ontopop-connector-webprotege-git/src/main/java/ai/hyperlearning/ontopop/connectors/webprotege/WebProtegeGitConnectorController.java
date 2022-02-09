package ai.hyperlearning.ontopop.connectors.webprotege;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhookPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * WebProtege to Git Connector - Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/connectors/webprotege")
@Tag(name = "webprotege", description = "Webprotege Connector API")
public class WebProtegeGitConnectorController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeGitConnectorController.class);
    
    /**************************************************************************
     * 1. POST - Push WebProtege project updates to Git
     *************************************************************************/
    
    @Operation(
            summary = "Push WebProtege project updates to Git",
            description = "Push the latest WebProtege project updates to Git.",
            tags = {"webprotege"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "WebProtege webhok payload successfully processed.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized operation.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            value = "/git",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> gitPush(
            @Parameter(
                    description = "WebProtege webhook payload.", 
                    required = true, 
                    schema = @Schema(implementation = WebProtegeWebhookPayload.class))
            @Valid @RequestBody(required = true) WebProtegeWebhookPayload webProtegeWebhookPayload) {
        
        // Log the payload for debugging purposes
        LOGGER.debug("WebProtege webhook payload received: {}", 
                webProtegeWebhookPayload);
        
        // Return a response entity
        return new ResponseEntity<String>(
                "WebProtege webhook payload successfully processed.", 
                HttpStatus.OK);
        
    }

}
