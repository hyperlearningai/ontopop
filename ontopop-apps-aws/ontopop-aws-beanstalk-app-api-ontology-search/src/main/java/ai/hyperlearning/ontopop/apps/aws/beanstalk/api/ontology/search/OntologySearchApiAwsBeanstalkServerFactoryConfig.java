package ai.hyperlearning.ontopop.apps.aws.beanstalk.api.ontology.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * AWS Elastic Beanstalk Ontology Search API Service Web Application - Server Factory Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
public class OntologySearchApiAwsBeanstalkServerFactoryConfig 
        implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    
    @Value("${web.aws.beanstalk.port:5000}")
    private int port;
    
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        factory.setPort(port);
    }

}
