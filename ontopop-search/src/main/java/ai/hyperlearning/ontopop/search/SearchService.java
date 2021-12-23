package ai.hyperlearning.ontopop.search;

import java.util.Set;

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
	
	public void indexDocuments(String indexName, 
			Set<SimpleIndexedVertex> vertices);
	
	public void indexDocument(String indexName, 
			SimpleIndexedVertex vertex);
	
}
