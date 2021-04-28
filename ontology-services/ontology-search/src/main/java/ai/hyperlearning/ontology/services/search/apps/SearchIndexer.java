package ai.hyperlearning.ontology.services.search.apps;

import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.model.vocabulary.Synonym;
import ai.hyperlearning.ontology.services.model.vocabulary.Term;
import ai.hyperlearning.ontology.services.search.impl.SearchServiceClient;
import ai.hyperlearning.ontology.services.ui.GraphResponseModel2;
import ai.hyperlearning.ontology.services.ui.GraphResponseModelCoordinator;

/**
 * Search Indexer Utility Methods
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class SearchIndexer {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(SearchIndexer.class);
	private static final String PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION = 
			"objectPropertyRdfsLabel";
	public static final String GREMLIN_QUERY_GET_ALL_NODES = 
			"g.V()"
			+ ".valueMap(true)"
			+ ".by(unfold())";
	public static final String GREMLIN_QUERY_GET_NODE =
			"g.V(%d)"
			+ ".valueMap(true)"
			+ ".by(unfold())";
	public static final String GREMLIN_QUERY_GET_ALL_EDGES = 
			"g.V()"
			+ ".bothE()"
			+ ".project("
				+ "'edgeId', "
				+ "'role', "
				+ "'edgeProperties', "
				+ "'sourceNodeId', "
				+ "'targetNodeId')"
			+ ".by(id())"
			+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
			+ ".by(valueMap(true))"
			+ ".by(outV().id())"
			+ ".by(inV().id())"
			+ ".by(unfold())";
	public static final String GREMLIN_QUERY_GET_EDGE = 
			"g.E(%d)"
			+ ".project("
				+ "'edgeId', "
				+ "'role', "
				+ "'edgeProperties', "
				+ "'sourceNodeId', "
				+ "'targetNodeId')"
			+ ".by(id())"
			+ ".by(values('" + PROPERTY_KEY_DEFAULT_ROLE_DESCRIPTION + "'))"
			+ ".by(valueMap(true))"
			+ ".by(outV().id())"
			+ ".by(inV().id())"
			+ ".by(unfold())";
	
	public SearchIndexer() {
		
	}
	
	/**
	 * Create the search index if it does not already exist
	 * @param searchServiceClient
	 */
	
	public boolean createIndexIfNotExists(
			SearchServiceClient searchServiceClient) {
		
		try {
			if (!searchServiceClient.doesIndexExist()) {
				searchServiceClient.createIndex();
				Thread.sleep(5000L);
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("Could not create search index", e);
		}
		
		return false;
		
	}
	
	/**
	 * Index all vertices in the graph database as search documents
	 * @param vertices
	 * @return
	 */
	
	public void indexVerticesAsDocuments(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager) {
		
		try {
			
			// Get all vertices in the graph database modelled to conform
			// with the JSON schema for Azure Search indexing
			// Reference: https://docs.microsoft.com/en-us/azure/search/search-get-started-java#1---create-index
			String verticesJsonDocuments = GraphResponseModelCoordinator
					.transform(graphDatabaseManager
						.query(GREMLIN_QUERY_GET_ALL_NODES), 
						Vertex.class, 2);
			
			// Index these vertices
			searchServiceClient.indexDocuments(verticesJsonDocuments);
			
			
		} catch (Exception e) {
			LOGGER.error("Error when indexing all graph vertices", e);
		}
		
	}
	
	/**
	 * Index a specific vertex in the graph database as a search document
	 * @param searchServiceClient
	 * @param graphDatabaseManager
	 * @param vertexId
	 */
	
	public void indexVertexAsDocument(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager, 
			int vertexId) {
		
		try {
			
			// Get the given vertex from the graph database modelled to conform
			// with the JSON schema for Azure Search indexing
			String vertexJsonDocument = GraphResponseModelCoordinator.transform(
					graphDatabaseManager
						.query(String.format(GREMLIN_QUERY_GET_NODE, vertexId)), 
					Vertex.class, 2);
			
			// Index this vertex
			searchServiceClient.indexDocuments(vertexJsonDocument);
			
		} catch (Exception e) {
			LOGGER.error("Error when indexing graph vertex {}", vertexId,  e);
		}
		
	}
	
	/**
	 * Index a specific vertex in the graph database as a search document
	 * @param searchServiceClient
	 * @param graphDatabaseManager
	 * @param vertexJsonDocument
	 */
	
	public void indexVertexAsDocument(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager, 
			String vertexJsonDocument) {
		try {
			searchServiceClient.indexDocuments(vertexJsonDocument);
		} catch (Exception e) {
			LOGGER.error("Error when indexing graph vertex {}", 
					vertexJsonDocument,  e);
		}
	}
	
	/**
	 * Index a specific vertex in the graph database as a search document
	 * @param searchServiceClient
	 * @param graphDatabaseManager
	 * @param vertexId
	 * @param synonyms
	 * @param terms
	 */
	
	public String indexVertexAsDocument(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager, 
			int vertexId, List<Synonym> synonyms, List<Term> terms) {
		
		try {
			
			// Generate a search index-friendly JSON document of the vertex
			GraphResponseModel2 transformer = new GraphResponseModel2();
			String vertexJsonDocument = transformer
					.transformVertexCentricGraphTraversalResult(
							graphDatabaseManager.query(String.format(
									GREMLIN_QUERY_GET_NODE, vertexId)), 
							synonyms, terms);
			
			// Index the vertex
			searchServiceClient.indexDocuments(vertexJsonDocument);
			return vertexJsonDocument;
			
		} catch (Exception e) {
			LOGGER.error("Error when indexing graph vertex {}", vertexId,  e);
		}
		
		return null;
		
	}
	
	/**
	 * Index all edge in the graph database as search documents
	 * @param vertices
	 * @return
	 */
	
	public void indexEdgesAsDocuments(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager) {
		
		try {
			
			// Get all edges in the graph database modelled to conform
			// with the JSON schema for Azure Search indexing
			String edgesJsonDocuments = GraphResponseModelCoordinator.transform(
					graphDatabaseManager
						.query(GREMLIN_QUERY_GET_ALL_EDGES), 
					Edge.class, 2);
			
			// Index these vertices
			searchServiceClient.indexDocuments(edgesJsonDocuments);
			
		} catch (Exception e) {
			LOGGER.error("Error when indexing all graph edges", e);
		}
		
	}
	
	/**
	 * Index a specific edge in the graph database as a search document
	 * @param searchServiceClient
	 * @param graphDatabaseManager
	 * @param vertexId
	 */
	
	public void indexEdgeAsDocument(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager, 
			int edgeId) {
		
		try {
			
			// Get the given edge from the graph database modelled to conform
			// with the JSON schema for Azure Search indexing
			String edgeJsonDocument = GraphResponseModelCoordinator.transform(
					graphDatabaseManager
						.query(String.format(GREMLIN_QUERY_GET_EDGE, edgeId)), 
					Edge.class, 2);
			
			// Index this edge
			searchServiceClient.indexDocuments(edgeJsonDocument);
			
		} catch (Exception e) {
			LOGGER.error("Error when indexing graph edge {}", edgeId,  e);
		}
		
	}
	
	/**
	 * Index a specific edge in the graph database as a search document
	 * @param searchServiceClient
	 * @param graphDatabaseManager
	 * @param vertexJsonDocument
	 */
	
	public void indexEdgeAsDocument(
			SearchServiceClient searchServiceClient, 
			GraphDatabaseManager graphDatabaseManager, 
			String edgeJsonDocument) {
		try {
			searchServiceClient.indexDocuments(edgeJsonDocument);
		} catch (Exception e) {
			LOGGER.error("Error when indexing graph edge {}", 
					edgeJsonDocument,  e);
		}
	}

}
