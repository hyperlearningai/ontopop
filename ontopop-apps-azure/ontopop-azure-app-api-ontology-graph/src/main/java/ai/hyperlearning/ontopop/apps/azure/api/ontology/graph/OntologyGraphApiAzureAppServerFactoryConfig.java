package ai.hyperlearning.ontopop.apps.azure.api.ontology.graph;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * Microsoft Ontology Graph API Service Web Application - Server Factory Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologyGraphApiAzureAppServerFactoryConfig 
        implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    
    @Value("${web.azure.webapps.port:80}")
    private int port;
    
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(port);
    }

}
