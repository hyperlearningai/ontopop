package ai.hyperlearning.ontopop.data.ontology.ingestor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunction;
import ai.hyperlearning.ontopop.data.ontology.ingestor.function.OntologyIngestorFunctionModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Ingestion Service - Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "ingest", description = "Ontology Ingestion Service API")
public class OntologyIngestorController {

    @Autowired
    private OntologyIngestorFunction ontologyIngestorFunction;

    /**************************************************************************
     * 1. POST - Ontology Ingestion Webhook
     *************************************************************************/

    @Operation(
            summary = "Ontology ingestion webhook",
            description = "Ontology ingestion webhook",
            tags = {"ingest", "ontology", "webhook"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology ingestion request accepted")})
    @PostMapping(
            value = "/ingest",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ingest(
            @RequestHeader Map<String, String> headers,
            @RequestBody(required = true) String payload) {

        // Execute the Ontology Ingestor Function
        OntologyIngestorFunctionModel ontologyIngestorFunctionModel = 
                new OntologyIngestorFunctionModel(headers, payload);
        ontologyIngestorFunction.accept(ontologyIngestorFunctionModel);

        // Return a response entity
        return new ResponseEntity<String>(
                "Ontology ingestion request accepted.", HttpStatus.OK);

    }

}
