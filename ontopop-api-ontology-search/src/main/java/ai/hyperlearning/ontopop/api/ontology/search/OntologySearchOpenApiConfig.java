package ai.hyperlearning.ontopop.api.ontology.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Ontology Search API Service - Open API Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class OntologySearchOpenApiConfig {
    
    @Bean
    public OpenAPI getOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Search API")
                        .description("API for querying the OntoPop Search Index"));
    }

}
