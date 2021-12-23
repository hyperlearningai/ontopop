package ai.hyperlearning.ontopop.search.elasticsearch;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.search.SearchService;
import ai.hyperlearning.ontopop.search.model.SimpleIndexedVertex;

/**
 * Elasticsearch Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class ElasticsearchService implements SearchService {
	
	@Autowired
	private ElasticsearchOperations elasticsearchTemplate;
	
	/**************************************************************************
	 * SEARCH INDEX MANAGEMENT
	 *************************************************************************/

	@Override
	public void createIndex(String indexName) {
		elasticsearchTemplate.indexOps(
				IndexCoordinates.of(indexName)).create();
	}
	
	@Override
	public void deleteIndex(String indexName) {
		elasticsearchTemplate.indexOps(
				IndexCoordinates.of(indexName)).delete();
	}
	
	/**************************************************************************
	 * DOCUMENT MANAGEMENT
	 *************************************************************************/

	@Override
	public void indexDocuments(String indexName, 
			Set<SimpleIndexedVertex> vertices) {
		elasticsearchTemplate.save(vertices, IndexCoordinates.of(indexName));
	}

	@Override
	public void indexDocument(String indexName, 
			SimpleIndexedVertex vertex) {
		elasticsearchTemplate.save(vertex, IndexCoordinates.of(indexName));
	}

}
