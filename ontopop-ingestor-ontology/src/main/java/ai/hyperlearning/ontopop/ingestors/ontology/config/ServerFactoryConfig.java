package ai.hyperlearning.ontopop.ingestors.ontology.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * ETL Ingest Service - Server Factory Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class ServerFactoryConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
	
	@Value("${ontopop.services.compute.etl.ingest.server.port}")
    private int etlIngestServerPort;
	
	@Override
    public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(etlIngestServerPort);
	}

}
