package ai.hyperlearning.ontology.services.search.apps;

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
import org.springframework.context.annotation.FilterType;

import ai.hyperlearning.ontology.services.etl.parsers.implementations.HighwaysEnglandDataCatalogueParserApp;
import ai.hyperlearning.ontology.services.graphdb.apps.GraphDatabaseLoaderApp;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseImplementation;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.graphdb.impl.cosmosdb.CosmosDbGraphDatabaseManager;
import ai.hyperlearning.ontology.services.graphdb.impl.tinkergraph.TinkerGraphDatabaseManager;
import ai.hyperlearning.ontology.services.search.impl.SearchEngine;
import ai.hyperlearning.ontology.services.search.impl.SearchServiceClient;
import ai.hyperlearning.ontology.services.search.impl.azuresearch.AzureSearchServiceClient;
import ai.hyperlearning.ontology.services.utils.AzureSearchProperties;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;

/**
 * Search Indexer Spring Boot Application
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
								HighwaysEnglandDataCatalogueParserApp.class})})
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class, 
	    DataSourceTransactionManagerAutoConfiguration.class, 
	    HibernateJpaAutoConfiguration.class
	})
public class SearchIndexerApp implements CommandLineRunner {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(SearchIndexerApp.class);
	private GraphDatabaseManager graphDatabaseManager = null;
	private SearchServiceClient searchServiceClient = null;
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private AzureSearchProperties azureSearchProperties;
	
	/**
	 * Spring Boot Application Builder
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception {
		
		new SpringApplicationBuilder(SearchIndexerApp.class)
			.properties("spring.config.name:ontology-framework")
			.build()
			.run(args);
	
	}
	
	/**
	 * Application Runner
	 */
	
	public void run(String... args) throws Exception {
		
		/**********************************************************************
		 * OPEN THE GRAPH DATABASE
		 *********************************************************************/
		
		// Open the graph database
		GraphDatabaseImplementation graphDatabaseImplementation = 
				GraphDatabaseImplementation.valueOf(globalProperties
						.getGraphDbEngine().toUpperCase().strip());
		
		// Instantiate the relevant Graph Database Manager
		switch(graphDatabaseImplementation) {
			case TINKERGRAPH:
				LOGGER.info("Using Apache TinkerPop TinkerGraph");
				graphDatabaseManager = new TinkerGraphDatabaseManager(
						globalProperties.getGraphDbConfigurationFilename());
				break;
			case COSMOSDB:
				LOGGER.info("Using Microsoft Azure Cosmos DB");
				graphDatabaseManager = new CosmosDbGraphDatabaseManager(
						globalProperties.getGraphDbConfigurationFilename());
				break;
			default:
				LOGGER.info("Using Apache TinkerPop TinkerGraph");
				graphDatabaseManager = new TinkerGraphDatabaseManager(
						globalProperties.getGraphDbConfigurationFilename());
				break;
		}
		
		// Open the graph engine
		LOGGER.info("Opening the graph database engine");
		graphDatabaseManager.openGraph();
		
		/**********************************************************************
		 * INDEX THE ENTIRE GRAPH DATABASE
		 *********************************************************************/
		
		// Open the search engine
		SearchEngine searchEngine = SearchEngine.valueOf(globalProperties
				.getSearchEngine().toUpperCase().strip());
		
		// Instantiate the relevant search service client
		switch(searchEngine) {
			case AZURESEARCH:
				LOGGER.info("Using Azure Search");
				searchServiceClient = new AzureSearchServiceClient(
						azureSearchProperties);
				break;
			default:
				LOGGER.info("Using Azure Search");
				searchServiceClient = new AzureSearchServiceClient(
						azureSearchProperties);
				break;
		}
		
		// Create a new search indexer instance
		SearchIndexer searchIndexer = new SearchIndexer();
		
		// Create the search index if it does not already exist
		searchIndexer.createIndexIfNotExists(searchServiceClient);
		
		// Index all vertices as search documents
		LOGGER.info("Indexing all vertices");
		searchIndexer.indexVerticesAsDocuments(
				searchServiceClient, graphDatabaseManager);
		
		// Index all edges as search documents
		// LOGGER.info("Indexing all edges");
		// searchIndexer.indexEdgesAsDocuments(
		// 		searchServiceClient, graphDatabaseManager);
		
		/**********************************************************************
		 * CLEANUP RESOURCES
		 *********************************************************************/
		
		// Close the graph engine
		try {
			if (graphDatabaseManager != null)
				graphDatabaseManager.closeGraph();
		} catch (Exception e) {
		
		} finally {
			graphDatabaseManager = null;
		}
		
		// Exit the application
		System.exit(0);	
		
	}

}
