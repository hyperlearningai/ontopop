package ai.hyperlearning.ontopop.search.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@EnableElasticsearchRepositories(basePackages = "ai.hyperlearning.ontopop.search.elasticsearch.repositories")
public class ElasticsearchConfiguration {
	
	@Value("${storage.search.elasticsearch.url}")
	private String elasticsearchUrl;
	
	@Bean
    public RestHighLevelClient client() {
		ClientConfiguration clientConfiguration = ClientConfiguration.builder()
				.connectedTo(removeHttpProtocolFromUrl(elasticsearchUrl))
				.build();
        return RestClients
        		.create(clientConfiguration)
        		.rest();
    }
	
	@Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
	
	private static String removeHttpProtocolFromUrl(String url) {
		return url.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)","");
	}

}
