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
public class ConfigClientServerFactoryConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

	@Value("${ontopop.services.compute.config.client.network.httpPort}")
    private int configClientServerHttpPort;
	
	@Override
    public void customize(ConfigurableWebServerFactory factory) {
		factory.setPort(configClientServerHttpPort);
	}
	
}
