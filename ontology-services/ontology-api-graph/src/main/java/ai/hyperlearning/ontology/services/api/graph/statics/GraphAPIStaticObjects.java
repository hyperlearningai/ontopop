package ai.hyperlearning.ontology.services.api.graph.statics;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontology.services.graphdb.apps.GraphDatabaseLoader;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseImplementation;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.graphdb.impl.tinkergraph.TinkerGraphDatabaseManager;
import ai.hyperlearning.ontology.services.model.datamanagement.Dataset;
import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import ai.hyperlearning.ontology.services.owlapi.OWLAPIPipelines;
import ai.hyperlearning.ontology.services.search.apps.SearchIndexer;
import ai.hyperlearning.ontology.services.search.impl.SearchEngine;
import ai.hyperlearning.ontology.services.search.impl.SearchServiceClient;
import ai.hyperlearning.ontology.services.search.impl.azuresearch.AzureSearchServiceClient;
import ai.hyperlearning.ontology.services.utils.AzureSearchProperties;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;
import ai.hyperlearning.ontology.services.utils.TinkerGraphProperties;

/**
 * Graph API Application Component
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@Component
public class GraphAPIStaticObjects {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(GraphAPIStaticObjects.class);
	private static final String DATA_CATALOGUE_PARSE_METHOD_NAME = 
			"parseDataCatalogue";
	
	private static GraphDatabaseManager graphDatabaseManager;
	private static ParsedRDFOwlOntology parsedRDFOwlOntology;
	private GraphDatabaseLoader graphDatabaseLoader;
	private static SearchServiceClient searchServiceClient;
	private static SearchIndexer searchIndexer;
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private TinkerGraphProperties tinkerGraphProperties;
	
	@Autowired
	private AzureSearchProperties azureSearchProperties;
	
	@PostConstruct
	public void init() throws ConfigurationException, URISyntaxException, 
	InconsistentOntologyException, OWLOntologyCreationException, JAXBException {
		
		/**********************************************************************
		 * MVP PHASE - LOAD ONTOLOGY FROM LOCAL FILESYSTEM
		 *********************************************************************/
		
		GraphDatabaseImplementation graphDatabaseImplementation = 
				GraphDatabaseImplementation.valueOf(globalProperties
						.getGraphDbEngine().toUpperCase().strip());
		String graphConfigurationFilename = 
				globalProperties.getGraphDbConfigurationFilename();
		String ontologyOwlFilename = 
				globalProperties.getGraphDbAppsLoaderOntologyOwlFilename();
		load(graphDatabaseImplementation, 
				graphConfigurationFilename, 
				ontologyOwlFilename);
		
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
		 * INSTANTIATE A SEARCH SERVICE CLIENT
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
		searchIndexer = new SearchIndexer();
		
		// Index all vertices as search documents if the index 
		// does not already exist
		try {
			
			if ( !searchServiceClient.doesIndexExist() ) {
				
				// Create the search index
				LOGGER.info("Creating index");
				searchIndexer.createIndexIfNotExists(searchServiceClient);
				
				// Index the graph
				LOGGER.info("Indexing all vertices");
				searchIndexer.indexVerticesAsDocuments(
						searchServiceClient, graphDatabaseManager);
				
			}
			
		} catch (ExecutionException | TimeoutException e) {
			
			// The first Gremlin request may timeout (to be investigated).
			// As a quick MVP fix, try indexing the vertices again
			try {
				
				// Index the graph
				LOGGER.info("Indexing all vertices");
				searchIndexer.indexVerticesAsDocuments(
						searchServiceClient, graphDatabaseManager);
				
			} catch (Exception e2) {
				
			}
			
		} catch (Exception e1) {
			
		}
		
	}
	
	public static GraphDatabaseManager getGraphDatabaseManager() {
		return graphDatabaseManager;
	}
	
	public static ParsedRDFOwlOntology getParsedRDFOwlOntology() {
		return parsedRDFOwlOntology;
	}
	
	public static SearchServiceClient getSearchServiceClient() {
		return searchServiceClient;
	}
	
	public static SearchIndexer getSearchIndexer() {
		return searchIndexer;
	}
	
	/**
	 * Parse and load the ontology into the graph database
	 * from the local filesystem
	 * @param graphDatabaseImplementation
	 * @param graphConfigurationFilename
	 * @param ontologyOwlFilename
	 */
	
	public void load(
			GraphDatabaseImplementation graphDatabaseImplementation,  
			String graphConfigurationFilename, 
			String ontologyOwlFilename) {
		
		String serializedGraphFile = null;
		
		try {
			
			// Instantiate the relevant Graph Database Manager
			switch(graphDatabaseImplementation) {
				case TINKERGRAPH:
					LOGGER.info("Using Apache TinkerPop TinkerGraph");
					graphDatabaseManager = new TinkerGraphDatabaseManager(
							graphConfigurationFilename);
					serializedGraphFile = tinkerGraphProperties
							.getGremlinTinkerGraphGraphLocation();
					break;
				default:
					LOGGER.info("Using Apache TinkerPop TinkerGraph");
					graphDatabaseManager = new TinkerGraphDatabaseManager(
							graphConfigurationFilename);
					serializedGraphFile = tinkerGraphProperties
							.getGremlinTinkerGraphGraphLocation();
					break;
			}
			
			// Open the graph
			LOGGER.info("Opening the graph database engine");
			graphDatabaseManager.openGraph();
			
			// Create the graph schema
			LOGGER.info("Creating the graph database schema");
			graphDatabaseManager.createSchema();
			
			// First check if the graph has previously been serialised to disk
			// In this case, load from the on-disk serialisation
			if ( serializedGraphFile == null 
					|| !(new File(serializedGraphFile).exists()) ) {
				
				// Parse the ontology in either batch or event-driven mode
				LOGGER.info("Parsing ontology {}", ontologyOwlFilename);
				parsedRDFOwlOntology = OWLAPIPipelines
						.parseRdfOwlOntology(ontologyOwlFilename);
				
				// Load the parsed ontology into the graph database
				LOGGER.info("Loading ontology {} into the graph database", 
						ontologyOwlFilename);
				graphDatabaseLoader = new GraphDatabaseLoader(
						parsedRDFOwlOntology);
				graphDatabaseLoader.loadParsedOntologyClassesAsVertices(
						graphDatabaseManager);
				graphDatabaseLoader.loadParsedOntologyClassesAsEdges(
						graphDatabaseManager);
				
			} else
				
				LOGGER.info("Loading graph from serialized graph file "
						+ "found at {}", serializedGraphFile);
			
		} catch (Exception e) {
			
			LOGGER.error("An error was encountered running the Graph Database "
					+ "loader app", e);
			
		} finally {
			
			// Close the graph database loader
			try {
				graphDatabaseLoader.clearLoadedVertexMap();
			} catch (Exception e) {
				
			}
			
			
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
			
			// First check if the graph has previously been serialised to disk
			// In this case, load from the on-disk serialisation
			String serializedGraphFile = tinkerGraphProperties
					.getGremlinTinkerGraphGraphLocation();
			if ( serializedGraphFile == null 
					|| !(new File(serializedGraphFile).exists()) ) {
			
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
				
			} else
				
				LOGGER.info("Loading data catalogue from serialized graph file "
						+ "found at {}", serializedGraphFile);
			
		} catch (Exception e) {
			
			LOGGER.error("An error was encountered running the Graph Database "
					+ "loader app and when loading the data catalogue", e);
			
		} finally {
			
			// Close the graph database loader
			try {
				graphDatabaseLoader.clearLoadedVertexLabelMap();
			} catch (Exception e) {
				
			}
			
		}
		
	}
	
	/**
	 * Close the graph database manager
	 */
	
	public static void closeGraphDatabaseManager() {
		try {
			graphDatabaseManager.closeGraph();
		} catch (Exception e) {
		
		} finally {
			graphDatabaseManager = null;
		}
	}

}
