package ai.hyperlearning.ontopop.api.ontology.graph;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Ontology Graph API Service - Open API Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class OntologyGraphOpenApiConfig {
    
    @Bean
    public OpenAPI getOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Graph API")
                        .description("API for querying the OntoPop Graph Database"));
    }

}
