package ai.hyperlearning.ontology.services.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Open API Configuration
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Configuration
public class OpenAPIConfig {
	
	private static final String OPEN_API_TITLE = "Ontology Framework API";
	
	@Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title(OPEN_API_TITLE).description(
                		"Ontology Framework API Specifications"));
    }

}
