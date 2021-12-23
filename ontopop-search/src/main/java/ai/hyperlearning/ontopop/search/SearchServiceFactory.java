package ai.hyperlearning.ontopop.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.search.elasticsearch.ElasticsearchService;

/**
 * Search Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class SearchServiceFactory {
	
	@Autowired
	private ElasticsearchService elasticsearchService;
	
	/**
	 * Select the relevant object storage service
	 * @param type
	 * @return
	 */
	
	public SearchService getSearchService(String type) {
		
		SearchServiceType searchServiceType = 
				SearchServiceType.valueOfLabel(type.toUpperCase());
		switch ( searchServiceType ) {
			case ELASTICSEARCH:
				return elasticsearchService;
			default:
				return elasticsearchService;
		}
		
	}
	
	public SearchService getSearchService(
			SearchServiceType searchServiceType) {
			
		switch ( searchServiceType ) {
			case ELASTICSEARCH:
				return elasticsearchService;
			default:
				return elasticsearchService;
		}
		
	}

}
