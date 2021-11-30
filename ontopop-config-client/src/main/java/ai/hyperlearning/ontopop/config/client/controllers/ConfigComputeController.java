package ai.hyperlearning.ontopop.config.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Configuration Controller - Compute Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/config/services/compute")
@Tag(name = "config", description = "Configuration API - Compute Services")
public class ConfigComputeController {
	
	@Value("${ontopop.services.compute.ingestors.ontology.storage.outputDirectory}")
    private String ingestorOntologyStorageOutputDirectory;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	@Operation(
			summary = "Ingestor - Ontology Storage Output Directory",
			description = "Get the ontology ingestion service storage output directory path",
			tags = { "service", "compute", "ontology", "ingest" })
	@GetMapping(
			value = "/ingestors/ontology/storage/outputDirectory", 
			produces = MediaType.TEXT_PLAIN_VALUE)
	public String getIngestorOntologyStorageOutputDirectory() {
		return ingestorOntologyStorageOutputDirectory;
	}

}
