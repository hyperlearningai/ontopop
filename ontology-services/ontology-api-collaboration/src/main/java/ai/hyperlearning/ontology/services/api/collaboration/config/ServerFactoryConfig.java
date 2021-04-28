package ai.hyperlearning.ontology.services.api.collaboration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontology.services.utils.GlobalProperties;

/**
 * Embedded Server Configuration
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Component
public class ServerFactoryConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
	
	@Autowired
	private GlobalProperties globalProperties;

	@Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(globalProperties.getOntologyApiCollaborationHttpPort());
    }
	
}
