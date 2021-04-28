package ai.hyperlearning.ontology.services.etl.parsers.implementations;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontology.services.model.datamanagement.Dataset;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;

/**
 * Highways England Data Catalogue Parser Spring Boot Application
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@ComponentScan("ai.hyperlearning.ontology")
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class, 
	    DataSourceTransactionManagerAutoConfiguration.class, 
	    HibernateJpaAutoConfiguration.class
	})
public class HighwaysEnglandDataCatalogueParserApp implements CommandLineRunner {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(
					HighwaysEnglandDataCatalogueParserApp.class);
	
	@Autowired
	private GlobalProperties globalProperties;
	
	/**
	 * Spring Boot Application Builder
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception {
		
		new SpringApplicationBuilder(
				HighwaysEnglandDataCatalogueParserApp.class)
			.properties("spring.config.name:ontology-framework")
			.build()
			.run(args);
	
	}
	
	/**
	 * Application Runner
	 */
	
	public void run(String... args) {
		
		LOGGER.info("Running the Highways England Data Catalogue Parser App");
		HighwaysEnglandDataCatalogueParser parser = 
				new HighwaysEnglandDataCatalogueParser();
		Set<Dataset> datasets = parser.parseDataCatalogue(globalProperties
				.getGraphDbAppsLoaderDataCatalogueFilename());
		LOGGER.info(datasets.toString());
		LOGGER.info("Closing the Highways England Data Catalogue Parser App");
		System.exit(0);	
		
	}

}
