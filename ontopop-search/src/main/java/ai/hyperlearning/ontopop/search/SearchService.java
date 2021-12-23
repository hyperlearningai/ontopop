package ai.hyperlearning.ontopop.search;

import java.util.Set;

import org.springframework.data.elasticsearch.core.SearchHits;

import ai.hyperlearning.ontopop.search.model.SimpleIndexedVertex;

/**
 * Search Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface SearchService {
	
	/**************************************************************************
	 * SEARCH INDEX MANAGEMENT
	 *************************************************************************/

	public void createIndex(String indexName);
	
	public void deleteIndex(String indexName);
	
	/**************************************************************************
	 * DOCUMENT MANAGEMENT
	 *************************************************************************/
	
	public SimpleIndexedVertex getDocument(String indexName, long vertexId);
	
	public SearchHits<SimpleIndexedVertex> search(String indexName, 
			String propertyKey, String query, boolean exact, boolean and);
	
	public SearchHits<SimpleIndexedVertex> search(String indexName, 
			String propertyKey, String query, boolean and,  
			int minimumShouldMatchPercentage);
	
	public void indexDocuments(String indexName, 
			Set<SimpleIndexedVertex> vertices);
	
	public void indexDocument(String indexName, 
			SimpleIndexedVertex vertex);
	
}
