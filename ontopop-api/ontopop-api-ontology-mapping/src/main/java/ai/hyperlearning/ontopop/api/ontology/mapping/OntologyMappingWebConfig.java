package ai.hyperlearning.ontopop.api.ontology.mapping;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Ontology Mapping API Service - Web Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@EnableWebMvc
public class OntologyMappingWebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .allowedHeaders("*");
    }

}
