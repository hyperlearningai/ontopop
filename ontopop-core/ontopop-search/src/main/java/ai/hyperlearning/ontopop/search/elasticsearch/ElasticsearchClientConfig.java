package ai.hyperlearning.ontopop.search.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * Elasticsearch Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value = "storage.search.service",
        havingValue = "elasticsearch")
public class ElasticsearchClientConfig {

    @Value("${storage.search.elasticsearch.url}")
    private String elasticsearchUrl;
    
    @Value("${storage.search.elasticsearch.username}")
    private String elasticsearchUsername;
    
    @Value("${storage.search.elasticsearch.password}")
    private String elasticsearchPassword;
    
    @Value("${storage.search.elasticsearch.ssl}")
    private Boolean elasticsearchSsl;

    @Bean
    public RestHighLevelClient client() {
        
        ClientConfiguration clientConfiguration = null;
        String elasticsearchHostPort = 
                removeHttpProtocolFromUrl(elasticsearchUrl);
        
        if ( Strings.isNullOrEmpty(elasticsearchUsername) )
            
            // No authentication
            clientConfiguration = Boolean.TRUE.equals(elasticsearchSsl) ? 
                    ClientConfiguration.builder()
                        .connectedTo(elasticsearchHostPort)
                        .usingSsl()
                        .build() : 
                            ClientConfiguration.builder()
                                .connectedTo(elasticsearchHostPort)
                                .build();
                            
        else
            
            // Basic authentication
            clientConfiguration = Boolean.TRUE.equals(elasticsearchSsl) ? 
                    ClientConfiguration.builder()
                        .connectedTo(elasticsearchHostPort)
                        .usingSsl()
                        .withBasicAuth(elasticsearchUsername, 
                                elasticsearchPassword)
                        .build() : 
                            ClientConfiguration.builder()
                                .connectedTo(elasticsearchHostPort)
                                .withBasicAuth(elasticsearchUsername, 
                                        elasticsearchPassword)
                                .build();
        
        return RestClients.create(clientConfiguration).rest();
        
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }

    private static String removeHttpProtocolFromUrl(String url) {
        return url.replaceFirst(
                "^(http[s]?://www\\.|http[s]?://|www\\.)", "");
    }

}
