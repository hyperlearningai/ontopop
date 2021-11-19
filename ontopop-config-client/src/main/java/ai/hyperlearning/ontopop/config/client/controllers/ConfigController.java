package ai.hyperlearning.ontopop.config.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Configuration Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/config")
@Tag(name = "config", description = "Configuration API")
public class ConfigController {
	
	@Value("${ontopop.services.compute.etl.ingest.io.targetUri}")
    private String servicesComputeEtlIngestIoTargetUri;
	
	/**************************************************************************
	 * HTTP GET REQUESTS
	 *************************************************************************/
	
	@Operation(
			summary = "ETL Ingest Target URI",
			description = "Get the ETL service ingest target URI",
			tags = { "service", "compute", "etl", "ingest" })
	@GetMapping(
			value = "/services/compute/etl/ingest/io/targetUri", 
			produces = MediaType.TEXT_PLAIN_VALUE)
	public String getServicesComputeEtlIngestIoTargetUri() {
		return servicesComputeEtlIngestIoTargetUri;
	}

}
