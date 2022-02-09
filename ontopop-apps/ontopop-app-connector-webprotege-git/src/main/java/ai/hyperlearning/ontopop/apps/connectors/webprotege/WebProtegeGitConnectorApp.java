package ai.hyperlearning.ontopop.apps.connectors.webprotege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * WebProtege to Git Connector - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class WebProtegeGitConnectorApp {
    
    public static void main(String[] args) {
        SpringApplication.run(WebProtegeGitConnectorApp.class, args);
    }

}
