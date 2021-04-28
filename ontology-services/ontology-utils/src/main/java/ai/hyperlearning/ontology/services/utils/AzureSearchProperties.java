package ai.hyperlearning.ontology.services.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Microsoft Azure Search Service Properties
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Component
@PropertySource("classpath:search-azure-search.properties")
@ConfigurationProperties
@Validated
public class AzureSearchProperties {
	
	@Value("${search.azure.search.service.name}")
	private String serviceName;
	
	@Value("${search.azure.search.admin.key}")
	private String adminKey;
	
	@Value("${search.azure.search.query.key}")
	private String queryKey;
	
	@Value("${search.azure.search.index.name}")
	private String indexName;
	
	@Value("${search.azure.search.api.version}")
	private String apiVersion;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getAdminKey() {
		return adminKey;
	}

	public void setAdminKey(String adminKey) {
		this.adminKey = adminKey;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	@Override
	public String toString() {
		return "AzureSearchProperties ["
				+ "serviceName=" + serviceName + ", "
				+ "adminKey=" + adminKey + ", "
				+ "queryKey=" + queryKey + ", "
				+ "indexName=" + indexName + ", "
				+ "apiVersion=" + apiVersion + "]";
	}
	
}
