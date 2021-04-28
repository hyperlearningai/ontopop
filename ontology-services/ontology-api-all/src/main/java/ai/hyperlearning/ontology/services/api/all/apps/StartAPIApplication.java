package ai.hyperlearning.ontology.services.api.all.apps;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * All API Endpoints Spring Boot Application
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@ComponentScan(
		basePackages = {"ai.hyperlearning.ontology"}, 
		excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE,
						value = {
								ai.hyperlearning.ontology.services.graphdb.apps.GraphDatabaseLoaderApp.class, 
								ai.hyperlearning.ontology.services.etl.parsers.implementations.HighwaysEnglandDataCatalogueParserApp.class,
								ai.hyperlearning.ontology.services.search.apps.SearchIndexerApp.class, 
								ai.hyperlearning.ontology.services.api.auth.config.ServerFactoryConfig.class, 
								ai.hyperlearning.ontology.services.api.collaboration.config.ServerFactoryConfig.class, 
								ai.hyperlearning.ontology.services.api.graph.config.ServerFactoryConfig.class, 
								ai.hyperlearning.ontology.services.api.ontology.config.ServerFactoryConfig.class, 
								ai.hyperlearning.ontology.services.api.ui.config.ServerFactoryConfig.class, 
								ai.hyperlearning.ontology.services.api.auth.apps.StartAPIApplication.class,
								ai.hyperlearning.ontology.services.api.collaboration.apps.StartAPIApplication.class,
								ai.hyperlearning.ontology.services.api.graph.apps.StartAPIApplication.class,
								ai.hyperlearning.ontology.services.api.ontology.apps.StartAPIApplication.class,
								ai.hyperlearning.ontology.services.api.ui.apps.StartAPIApplication.class
								})}) 
@EntityScan("ai.hyperlearning.ontology")
@EnableScheduling
@SpringBootApplication
public class StartAPIApplication {
	
	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(
				ai.hyperlearning.ontology.services.api.all.apps.StartAPIApplication.class);
        app.run(args);
        
    }
	
	/**
	 * Callback method invoked at application startup after the
	 * Spring application context is initialised
	 * @return
	 */
	
	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			
		};
		
	}

}
