package ai.hyperlearning.ontology.services.graphdb.apps;

import java.lang.reflect.Method;
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

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseImplementation;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.graphdb.impl.cosmosdb.CosmosDbGraphDatabaseManager;
import ai.hyperlearning.ontology.services.graphdb.impl.tinkergraph.TinkerGraphDatabaseManager;
import ai.hyperlearning.ontology.services.model.datamanagement.Dataset;
import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import ai.hyperlearning.ontology.services.owlapi.OWLAPIPipelines;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;

/**
 * Graph Database Loader Spring Boot Application
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@ComponentScan("ai.hyperlearning.ontology")
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class, 
	    DataSourceTransactionManagerAutoConfiguration.class, 
	    HibernateJpaAutoConfiguration.class
	})
public class GraphDatabaseLoaderApp implements CommandLineRunner {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(GraphDatabaseLoaderApp.class);
	private static final String DATA_CATALOGUE_PARSE_METHOD_NAME = 
			"parseDataCatalogue";
	private GraphDatabaseManager graphDatabaseManager = null;
	private GraphDatabaseLoader graphDatabaseLoader = null;
	private ParsedRDFOwlOntology parsedRDFOwlOntology = null;
	
	@Autowired
	private GlobalProperties globalProperties;
	
	/**
	 * Spring Boot Application Builder
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception {
		
		new SpringApplicationBuilder(GraphDatabaseLoaderApp.class)
			.properties("spring.config.name:ontology-framework")
			.build()
			.run(args);
	
	}
	
	/**
	 * Application Runner
	 */
	
	public void run(String... args) {
		
		/**********************************************************************
		 * MVP PHASE - LOAD ONTOLOGY FROM LOCAL FILESYSTEM
		 *********************************************************************/
		
		LOGGER.info("Running the graph database loader app");
		GraphDatabaseImplementation graphDatabaseImplementation = 
				GraphDatabaseImplementation.valueOf(globalProperties
						.getGraphDbEngine().toUpperCase().strip());
		GraphDatabaseLoaderMode graphDatabaseLoaderMode =
				GraphDatabaseLoaderMode.valueOf(globalProperties
						.getGraphDbAppsLoaderMode().toUpperCase().strip());
		load(graphDatabaseImplementation, 
				globalProperties.getGraphDbConfigurationFilename(), 
				graphDatabaseLoaderMode, 
				globalProperties.getGraphDbAppsLoaderOntologyOwlFilename());
		
		/**********************************************************************
		 * MVP PHASE - LOAD DATA CATALOGUE FROM LOCAL FILESYSTEM
		 *********************************************************************/
		
		if ( globalProperties.isGraphDbAppsLoaderLoadDataCatalogue() )
			loadDataCatalogue(
					globalProperties
						.getGraphDbAppsLoaderDataCatalogueFilename(), 
					globalProperties
						.getGraphDbAppsLoaderDataCatalogueParseClass());
		
		/**********************************************************************
		 * CLEANUP RESOURCES
		 *********************************************************************/
		
		closeGraphDatabaseManager();
		LOGGER.info("Closing the graph database loader app");
		System.exit(0);	
	
	}
	
	/**
	 * Load a parsed ontology model into a given 
	 * Apache TinkerPop-compliant graph database implementation
	 * @param graphDatabaseImplementation
	 * @param graphDatabaseLoaderMode
	 * @param graphConfigurationFilename
	 */
	
	public void load(
			GraphDatabaseImplementation graphDatabaseImplementation,  
			String graphConfigurationFilename, 
			GraphDatabaseLoaderMode graphDatabaseLoaderMode,
			String ontologyOwlFilename) {
		
		try {
			
			// Instantiate the relevant Graph Database Manager
			switch(graphDatabaseImplementation) {
				case TINKERGRAPH:
					LOGGER.info("Using Apache TinkerPop TinkerGraph");
					graphDatabaseManager = new TinkerGraphDatabaseManager(
							graphConfigurationFilename);
					break;
				case COSMOSDB:
					LOGGER.info("Using Microsoft Azure Cosmos DB");
					graphDatabaseManager = new CosmosDbGraphDatabaseManager(
							graphConfigurationFilename);
					break;
				default:
					LOGGER.info("Using Apache TinkerPop TinkerGraph");
					graphDatabaseManager = new TinkerGraphDatabaseManager(
							graphConfigurationFilename);
					break;
			}
			
			// Open the graph
			LOGGER.info("Opening the graph database engine");
			graphDatabaseManager.openGraph();
			
			// Create the graph schema
			LOGGER.info("Creating the graph database schema");
			graphDatabaseManager.createSchema();
			
			// Parse the ontology in either batch or event-driven mode
			switch(graphDatabaseLoaderMode) {
				case BATCH:
					LOGGER.info("Parsing ontology {}", ontologyOwlFilename);
					parsedRDFOwlOntology = OWLAPIPipelines
							.parseRdfOwlOntology(ontologyOwlFilename);
					break;
				default:
					break;
			}
			
			// Load the parsed ontology into the graph database
			LOGGER.info("Loading ontology {} into the graph database", 
					ontologyOwlFilename);
			graphDatabaseLoader = new GraphDatabaseLoader(parsedRDFOwlOntology);
			graphDatabaseLoader.loadParsedOntologyClassesAsVertices(
					graphDatabaseManager);
			graphDatabaseLoader.loadParsedOntologyClassesAsEdges(
					graphDatabaseManager);
			
		} catch (Exception e) {
			
			LOGGER.error("An error was encountered running the Graph Database "
					+ "loader app", e);
			
		} finally {
			
			// Close the graph database loader
			graphDatabaseLoader.clearLoadedVertexMap();
			
		}
		
	}
	
	/**
	 * Parse and load the data catalogue into the graph database
	 * from the local filesystem
	 * @param dataCatalogueFilename
	 * @param dataCatalogueParserClass
	 */
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void loadDataCatalogue(String dataCatalogueFilename, 
			String dataCatalogueParserClass) {
		
		try {
			
			// Parse the data catalogue by invoking the given parser class
			LOGGER.info("Parsing data catalogue {}", dataCatalogueFilename);
			Class<?> parserClass = Class.forName(dataCatalogueParserClass);
			Object parserObject = parserClass.newInstance();
			Method parserMethod = parserClass.getDeclaredMethod(
					DATA_CATALOGUE_PARSE_METHOD_NAME, String.class);
			Set<Dataset> datasets = (Set<Dataset>) parserMethod.invoke(
					parserObject, dataCatalogueFilename);
			
			// Load the parsed data catalogue into the graph database
			LOGGER.info("Loading data catalogue {} into the graph database",
					dataCatalogueFilename);
			graphDatabaseLoader.loadDataCatalogue(
					datasets, graphDatabaseManager);
			
		} catch (Exception e) {
			
			LOGGER.error("An error was encountered running the Graph Database "
					+ "loader app and when loading the data catalogue", e);
			
		} finally {
			
			// Close the graph database loader
			graphDatabaseLoader.clearLoadedVertexLabelMap();
			
		}
		
	}
	
	/**
	 * Close the graph database manager
	 */
	
	public void closeGraphDatabaseManager() {
		try {
			if (graphDatabaseManager != null)
				graphDatabaseManager.closeGraph();
		} catch (Exception e) {
		
		} finally {
			graphDatabaseManager = null;
		}
	}

}
