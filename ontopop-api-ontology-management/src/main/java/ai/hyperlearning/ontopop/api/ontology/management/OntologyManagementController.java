package ai.hyperlearning.ontopop.api.ontology.management;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.ontology.management.OntologyManagementService;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreationAlreadyExistsException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreationException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyDeletionException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyUpdateSecretDataException;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyNonSecretData;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Management API Service - Ontology Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/management/ontologies")
@Tag(name = "Ontology Management API - Ontologies", description = "API for managing OntoPop ontologies")
public class OntologyManagementController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyManagementController.class);

    @Autowired
    private OntologyRepository ontologyRepository;

    @Autowired
    private OntologyManagementService ontologyManagementService;

    /**************************************************************************
     * 1. POST - Create Ontology
     *************************************************************************/

    @Operation(
            summary = "Create a new ontology to manage",
            description = "Create a new ontology instance that OntoPop should "
                    + "manage by providing the details of the Git repository "
                    + "containing the relevant W3C Web Ontology Language "
                    + "(OWL) file.",
            tags = {"ontology"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Ontology successfully created.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = Ontology.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Creation of ontology unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "409",
                            description = "Ontology already exists.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Ontology createOntology(
            @Parameter(
                    description = "New ontology that OntoPop should manage.", 
                    required = true, 
                    schema = @Schema(implementation = Ontology.class))
            @Valid @RequestBody(required = true) Ontology ontology) {
        LOGGER.debug("New HTTP POST request: Create a new ontology.");
        try {
            return ontologyManagementService.create(ontology);
        } catch (OntologyCreationAlreadyExistsException e) {
            LOGGER.error("An error was encountered when attempting to "
                    + "create a new ontology.", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("An error was encountered when attempting to "
                    + "create a new ontology.", e);
            throw new OntologyCreationException();
        }
    }

    /**************************************************************************
     * 2.1. GET - Get Ontologies
     *************************************************************************/

    @Operation(
            summary = "Get all ontologies",
            description = "Get all the ontologies managed by OntoPop.",
            tags = {"ontology"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontologies successfully retrieved.", 
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Ontology.class)))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of ontologies unauthorized.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Ontology> getOntologies() {
        LOGGER.debug("New HTTP GET request: Get all ontologies.");
        return (List<Ontology>) ontologyRepository.findAll();
    }

    /**************************************************************************
     * 2.2. GET - Get Ontology
     *************************************************************************/

    @Operation(
            summary = "Get an ontology",
            description = "Get an ontology managed by OntoPop given its "
                    + "ontology ID.",
            tags = {"ontology"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology successfully retrieved.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = Ontology.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Retrieval of ontology unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Ontology getOntology(
            @Parameter(
                    description = "ID of the ontology to retrieve.", 
                    required = true)
            @PathVariable(required = true) int id) {
        LOGGER.debug("New HTTP GET request: Get ontology by ID.");
        return ontologyRepository.findById(id)
                .orElseThrow(() -> new OntologyNotFoundException(id));
    }

    /**************************************************************************
     * 3.1. PATCH - Update Ontology (non-sensitive attributes)
     *************************************************************************/

    @Operation(
            summary = "Update an ontology",
            description = "Update the non-sensitive attributes of an ontology "
                    + "managed by OntoPop given its ontology ID.", 
            tags = {"ontology"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology successfully updated.", 
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                    schema = @Schema(implementation = Ontology.class))),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Update of ontology unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(
            value = "/{id}", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Ontology updateOntology(
            @Parameter(
                    description = "ID of the ontology to update.", 
                    required = true)
            @PathVariable(required = true) int id,
            @Parameter(
                    description = "Updated non-sensitive ontology attributes.", 
                    required = true, 
                    schema = @Schema(implementation = OntologyNonSecretData.class))
            @RequestBody(required = true) OntologyNonSecretData ontologyNonSecretData) {
        LOGGER.debug("New HTTP PATCH request: Update ontology by ID.");
        return ontologyManagementService.update(id, ontologyNonSecretData);
    }

    /**************************************************************************
     * 3.2. PATCH - Update Ontology (secrets)
     *************************************************************************/

    @Operation(
            summary = "Update ontology secrets",
            description = "Update the secret attributes of an ontology "
                    + "managed by OntoPop given its ontology ID.",
            tags = {"ontology"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology secrets successfully updated."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Update of ontology unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(
            value = "/{id}/secrets",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateOntologySecrets(
            @Parameter(
                    description = "ID of the ontology to update.", 
                    required = true)
            @PathVariable(required = true) int id,
            @Parameter(
                    description = "Updated sensitive ontology attributes.", 
                    required = true, 
                    schema = @Schema(implementation = OntologySecretData.class))
            @RequestBody OntologySecretData ontologySecretData) {
        LOGGER.debug("New HTTP PATCH request: Update ontology secrets by ID.");
        try {
            ontologyManagementService.update(id, ontologySecretData);
            return new ResponseEntity<>("Ontology secrets successfully "
                    + "updated.", HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("An error was encountered when attempting to update "
                    + "the secrets for this ontology.", e);
            throw new OntologyUpdateSecretDataException(id);
        }
    }

    /**************************************************************************
     * 4. DELETE - Delete Ontology
     *************************************************************************/

    @Operation(
            summary = "Delete an ontology",
            description = "Delete an ontology managed by OntoPop given its "
                    + "ontology ID.",
            tags = {"ontology"})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ontology successfully deleted."),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Deletion of ontology unauthorized.", 
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ontology not found.", 
                            content = @Content), 
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.", 
                            content = @Content)})
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(
            value = "/{id}", 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteOntology(
            @Parameter(
                    description = "ID of the ontology to delete.", 
                    required = true)
            @PathVariable(required = true) int id) {
        LOGGER.debug("New HTTP DELETE request: Delete ontology by ID.");
        try {
            ontologyManagementService.delete(id);
            return new ResponseEntity<>("Ontology successfully deleted.", 
                    HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("An error was encountered when attempting to "
                    + "delete this ontology.", e);
            throw new OntologyDeletionException(id);
        }
    }

}
