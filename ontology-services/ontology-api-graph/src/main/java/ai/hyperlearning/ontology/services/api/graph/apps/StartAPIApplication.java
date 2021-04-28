package ai.hyperlearning.ontology.services.api.graph.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

import ai.hyperlearning.ontology.services.etl.parsers.implementations.HighwaysEnglandDataCatalogueParserApp;
import ai.hyperlearning.ontology.services.graphdb.apps.GraphDatabaseLoaderApp;
import ai.hyperlearning.ontology.services.jpa.config.OntologyCollaborationDataSourceConfiguration;
import ai.hyperlearning.ontology.services.jpa.config.OntologyUIDataSourceConfiguration;
import ai.hyperlearning.ontology.services.search.apps.SearchIndexerApp;
import ai.hyperlearning.ontology.services.security.controllers.AuthenticationController;

/**
 * Graph API Spring Boot Application
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
								SearchIndexerApp.class, 
								OntologyUIDataSourceConfiguration.class, 
								OntologyCollaborationDataSourceConfiguration.class, 
								AuthenticationController.class})})
@EnableScheduling
@EntityScan("ai.hyperlearning.ontology")
@SpringBootApplication
public class StartAPIApplication {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(StartAPIApplication.class);
	
	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(
				StartAPIApplication.class);
//        app.setDefaultProperties(Collections
//          .singletonMap("server.port", 
//        		  globalProperties.getOntologyKnowledgegraphApiServerPort()));
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
