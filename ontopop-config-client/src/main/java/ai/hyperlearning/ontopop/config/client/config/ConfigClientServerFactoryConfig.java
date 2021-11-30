package ai.hyperlearning.ontopop.config.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * Configuration Server Client Server Factory Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class ServerFactoryConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

	@Value("${ontopop.services.compute.config.client.server.port}")
    private int configClientServerPort;
	
	@Override
    public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(configClientServerPort);
	}
	
}
