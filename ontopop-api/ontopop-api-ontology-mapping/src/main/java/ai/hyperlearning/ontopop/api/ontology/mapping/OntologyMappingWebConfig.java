package ai.hyperlearning.ontopop.api.ontology.mapping;

import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${security.cors.enabled:true}")
    private Boolean corsEnabled;
    
    @Value("${security.cors.allowedOrigins}")
    private String allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        // Route - health check
        registry.addMapping("/")
            .allowedOrigins("*")
            .allowedMethods("GET")
            .allowedHeaders("*");
        
        // CORS enabled
        if ( Boolean.TRUE.equals(corsEnabled) ) {
        
            // Route - all mapping API endpoints
            registry.addMapping("/mapping/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("OPTIONS", "POST")
                .allowedHeaders("*");
        
        }
        
        // CORS disabled
        else {
            
            // Route - all mapping API endpoints
            registry.addMapping("/mapping/**")
                .allowedOrigins("*")
                .allowedMethods("OPTIONS", "POST")
                .allowedHeaders("*");
            
        }
    
    }

}
