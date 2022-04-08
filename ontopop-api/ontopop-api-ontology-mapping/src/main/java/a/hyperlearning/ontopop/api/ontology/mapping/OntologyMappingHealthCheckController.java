package a.hyperlearning.ontopop.api.ontology.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ai.hyperlearning.ontopop.model.status.HealthCheck;
import ai.hyperlearning.ontopop.model.status.ProjectVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Ontology Mapping API Service - Health Check Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
public class OntologyMappingHealthCheckController {
    
    private static final String NAME = "OntoPop Mapping API";
    
    @Autowired
    private ProjectVersion projectVersion;
    
    /**************************************************************************
     * Health Check
     *************************************************************************/
    
    @Operation(
            summary = "Mapping API Health Check",
            description = "Mapping API Health Check",
            tags = {"ontology", "mapping"})
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
            value = {"/", "/mapping/"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public HealthCheck healthCheck() {
        return new HealthCheck(NAME, projectVersion.getVersion());
    }

}
