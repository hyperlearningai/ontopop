package ai.hyperlearning.ontopop.data.ontology;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.data.jpa.repositories.WebhookEventRepository;
import ai.hyperlearning.ontopop.exceptions.git.WebhookEventNotFoundException;
import ai.hyperlearning.ontopop.model.git.WebhookEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Service - Webhook Event Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "webhook", description = "Ontology Service Webhooks REST API")
public class OntologyWebhookEventController {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyWebhookEventController.class);
	
	@Autowired
    private WebhookEventRepository webhookEventRepository;
	
	/**************************************************************************
	 * 1.1. GET - Get Webhooks
	 *************************************************************************/
	
	@Operation(
			summary = "Get all webhooks",
			description = "Get all webhooks",
			tags = { "ontology", "webhook" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Webhooks successfully retrieved"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Webhooks retrieval request unauthorized")})
	@GetMapping(value = "/webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<WebhookEvent> getWebhooks() {
		LOGGER.debug("New HTTP GET request: Get all webhooks.");
		return (List<WebhookEvent>) webhookEventRepository.findAll();
	}
	
	/**************************************************************************
	 * 1.2. GET - Get Webhook
	 *************************************************************************/
	
	@Operation(
			summary = "Get webhook",
			description = "Get a webhook by webhook ID",
			tags = { "ontology", "webhook" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Webhook successfully retrieved"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Webhook retrieval request unauthorized"), 
	        @ApiResponse(
	        		responseCode = "404",
	        		description = "Webhook not found")})
	@GetMapping(value = "/webhooks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebhookEvent getWebhook(
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
			description = "Get all ontology webhooks",
			tags = { "ontology", "webhook" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Ontology webhooks successfully retrieved"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontology webhooks retrieval request unauthorized")})
	@GetMapping(value = "/{ontologyId}/webhooks", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<WebhookEvent> getOntologyWebhooks(
			@PathVariable(required = true) int ontologyId) {
		LOGGER.debug("New HTTP GET request: Get all ontology webhooks.");
		return webhookEventRepository.findByOntologyId(ontologyId);
	}
	
	/**************************************************************************
	 * 1.4. GET - Get Ontology Webhook
	 *************************************************************************/
	
	@Operation(
			summary = "Get ontology webhook",
			description = "Get ontology webhook",
			tags = { "ontology", "webhook" })
	@ApiResponses(value = {
	        @ApiResponse(
	        		responseCode = "200",
	        		description = "Ontology webhook successfully retrieved"), 
	        @ApiResponse(
	        		responseCode = "401",
	        		description = "Ontology webhook retrieval request unauthorized"), 
	        @ApiResponse(
	        		responseCode = "404",
	        		description = "Ontology webhook not found")})
	@GetMapping(value = "/{ontologyId}/webhooks/{webhookEventId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public WebhookEvent getOntologyWebhook(
			@PathVariable(required = true) int ontologyId, 
			@PathVariable(required = true) long webhookEventId) {
		LOGGER.debug("New HTTP GET request: Get ontology webhooks.");
		return webhookEventRepository
				.findByOntologyIdAdnWebhookEventId(ontologyId, webhookEventId)
                	.orElseThrow(() -> 
                		new WebhookEventNotFoundException(webhookEventId));
	}

}
