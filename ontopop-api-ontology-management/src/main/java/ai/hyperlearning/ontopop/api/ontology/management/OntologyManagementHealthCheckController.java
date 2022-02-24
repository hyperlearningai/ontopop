package ai.hyperlearning.ontopop.api.ontology.management;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.model.status.HealthCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Ontology Management API Service - Health Check Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
public class OntologyManagementHealthCheckController {
    
    private static final String NAME = "Ontology Management API";
    
    @Value("${spring.application.version:2.0.0}")
    private String version;
    
    /**************************************************************************
     * Health Check
     *************************************************************************/
    
    @Operation(
            summary = "Ontology Management API Health Check",
            description = "Ontology Management API Health Check",
            tags = {"ontology", "management"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Health check successfully performed.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = HealthCheck.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Health check operation unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthCheck healthCheck() {
        return new HealthCheck(NAME, version);
    }

}
