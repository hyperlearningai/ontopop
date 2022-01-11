package ai.hyperlearning.ontopop.search.azure.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;

/**
 * Microsoft Azure Search Index Client Beans
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value="storage.search.service", 
        havingValue = "azure-search")
public class AzureSearchIndexClientConfig {
    
    @Value("${storage.search.azure-search.endpoint}")
    private String endpoint;
    
    @Value("${storage.search.azure-search.key}")
    private String key;
    
    @Bean("azureSearchIndexNoIndexClient")
    public SearchIndexClient getSearchIndexNoIndexClient() {
        return new SearchIndexClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .buildClient();
    }
    
    @Bean("azureSearchClient")
    @Scope(value = "prototype")
    public SearchClient getSearchClient(String indexName) {
        return new SearchClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(key))
                .indexName(indexName)
                .buildClient();
    }

}
