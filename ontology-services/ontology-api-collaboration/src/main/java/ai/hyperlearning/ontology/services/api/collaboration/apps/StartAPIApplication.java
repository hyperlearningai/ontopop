package ai.hyperlearning.ontology.services.api.collaboration.apps;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import ai.hyperlearning.ontology.services.etl.parsers.implementations.HighwaysEnglandDataCatalogueParserApp;
import ai.hyperlearning.ontology.services.graphdb.apps.GraphDatabaseLoaderApp;
import ai.hyperlearning.ontology.services.jpa.config.OntologyUIDataSourceConfiguration;
import ai.hyperlearning.ontology.services.jpa.config.OntologyVocabularyDataSourceConfiguration;
import ai.hyperlearning.ontology.services.security.controllers.AuthenticationController;

/**
 * Collaboration API Spring Boot Application
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
						value = {GraphDatabaseLoaderApp.class, 
								HighwaysEnglandDataCatalogueParserApp.class, 
								AuthenticationController.class, 
								OntologyUIDataSourceConfiguration.class, 
								OntologyVocabularyDataSourceConfiguration.class})})
@EntityScan("ai.hyperlearning.ontology")
@SpringBootApplication
public class StartAPIApplication {
	
	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(
				StartAPIApplication.class);
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
