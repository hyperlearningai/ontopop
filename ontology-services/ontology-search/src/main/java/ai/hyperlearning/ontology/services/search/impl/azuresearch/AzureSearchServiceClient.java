package ai.hyperlearning.ontology.services.search.impl.azuresearch;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontology.services.search.impl.SearchServiceClient;
import ai.hyperlearning.ontology.services.utils.AzureSearchProperties;
import ai.hyperlearning.ontology.services.utils.HttpUtils;

/**
 * Azure Search Service Client
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class AzureSearchServiceClient extends SearchServiceClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(
			AzureSearchServiceClient.class);
	private AzureSearchProperties azureSearchProperties;
	private static final String AZURE_INDEX_DEFINITION_FILENAME = 
			"search-azure-search-index-schema.json";
	private static final String AZURE_SEARCH_INDEX_EXISTS_URL = 
			"https://%s.search.windows.net/indexes/%s/docs?api-version=%s&search=*";
	private static final String AZURE_SEARCH_INDEX_DELETION_URL = 
			"https://%s.search.windows.net/indexes/%s?api-version=%s";
	private static final String AZURE_SEARCH_INDEX_CREATION_URL = 
			"https://%s.search.windows.net/indexes/%s?api-version=%s";
	private static final String AZURE_SEARCH_INDEX_DOCUMENT_INDEXING_URL = 
			"https://%s.search.windows.net/indexes/%s/docs/index?api-version=%s";
	
	public AzureSearchServiceClient(
			AzureSearchProperties azureSearchProperties) {
		this.azureSearchProperties = azureSearchProperties;
	}

	/**************************************************************************
	 * SEARCH MANAGEMENT
	 *************************************************************************/
	
	@Override
	public boolean doesIndexExist() throws Exception {
		
		// Check whether the given Azure Search index already exists
		URI uri = HttpUtils.buildURI(strFormatter -> strFormatter.format(
				AZURE_SEARCH_INDEX_EXISTS_URL, 
				azureSearchProperties.getServiceName(), 
				azureSearchProperties.getIndexName(), 
				azureSearchProperties.getApiVersion()));
		HttpRequest request = HttpUtils.httpRequest(
				uri, azureSearchProperties.getAdminKey(), "HEAD", "");
		HttpResponse<String> response = HttpUtils.sendRequest(request);
		return HttpUtils.isSuccessResponse(response);
		
	}

	@Override
	public boolean createIndex() throws Exception {
		
		// Create a new Azure Search index using the index definition JSON file
		LOGGER.info("Creating new Azure Search Index {}...", 
				azureSearchProperties.getIndexName());
		URI uri = HttpUtils.buildURI(strFormatter -> strFormatter.format(
				AZURE_SEARCH_INDEX_CREATION_URL, 
				azureSearchProperties.getServiceName(), 
				azureSearchProperties.getIndexName(), 
				azureSearchProperties.getApiVersion()));
		InputStream inputStream = AzureSearchServiceClient.class
				.getResourceAsStream("/" + AZURE_INDEX_DEFINITION_FILENAME);
		String indexDefinition = new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		HttpRequest request = HttpUtils.httpRequest(uri, 
				azureSearchProperties.getAdminKey(), "PUT", indexDefinition);
		HttpResponse<String> response = HttpUtils.sendRequest(request);
		return HttpUtils.isSuccessResponse(response);
		
	}

	@Override
	public boolean deleteIndex() throws Exception {
		
		// Delete an existing Azure Search index
		LOGGER.info("Deleting Azure Search Index {}...", 
				azureSearchProperties.getIndexName());
		URI uri = HttpUtils.buildURI(strFormatter -> strFormatter.format(
				AZURE_SEARCH_INDEX_DELETION_URL, 
				azureSearchProperties.getServiceName(), 
				azureSearchProperties.getIndexName(), 
				azureSearchProperties.getApiVersion()));
		HttpRequest request = HttpUtils.httpRequest(uri, 
				azureSearchProperties.getAdminKey(), "DELETE", "*");
		HttpResponse<String> response = HttpUtils.sendRequest(request);
		return HttpUtils.isSuccessResponse(response);
		
	}
	
	/**************************************************************************
	 * SCHEMA MANAGEMENT
	 *************************************************************************/

	public static Set<String> getIndexSchemaFieldNames() throws Exception {
		
		// Get the set of index schema field names
		Set<String> fieldNames = new LinkedHashSet<String>();
		InputStream inputStream = AzureSearchServiceClient.class
				.getResourceAsStream("/" + AZURE_INDEX_DEFINITION_FILENAME);
		String indexDefinition = new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(indexDefinition);
		JsonNode fieldsArray = root.get("fields");
		for (JsonNode field : fieldsArray) {
			fieldNames.add(field.get("name").asText());
		}
		return fieldNames;
		
	}
	
	/**************************************************************************
	 * DOCUMENT MANAGEMENT
	 *************************************************************************/

	@Override
	public boolean indexDocuments(String documentsJson) throws Exception {
		
		// Index given JSON documents
		LOGGER.debug("Indexing documents into Azure Search Index {}...", 
				azureSearchProperties.getIndexName());
		URI endpoint = HttpUtils.buildURI(strFormatter -> strFormatter.format(
				AZURE_SEARCH_INDEX_DOCUMENT_INDEXING_URL, 
				azureSearchProperties.getServiceName(), 
				azureSearchProperties.getIndexName(), 
				azureSearchProperties.getApiVersion()));
		HttpRequest request = HttpUtils.httpRequest(endpoint, 
				azureSearchProperties.getAdminKey(), "POST", documentsJson);
		HttpResponse<String> response = HttpUtils.sendRequest(request);
		return HttpUtils.isSuccessResponse(response);
		
	}

	@Override
	public boolean deleteDocument(int id) throws Exception {
		
		// Delete a document from the index given its document ID (string)
		LOGGER.debug("Delete document {} from Azure Search Index {}...", 
				id, azureSearchProperties.getIndexName());
		String jsonRequestBody = "{"
				+ "\"value\":["
					+ "{"
						+ "\"@search.action\":\"delete\","
						+ "\"id\":\"" + id + "\""
					+ "}"
				+ "]}";
		URI endpoint = HttpUtils.buildURI(strFormatter -> strFormatter.format(
				AZURE_SEARCH_INDEX_DOCUMENT_INDEXING_URL, 
				azureSearchProperties.getServiceName(), 
				azureSearchProperties.getIndexName(), 
				azureSearchProperties.getApiVersion()));
		HttpRequest request = HttpUtils.httpRequest(endpoint, 
				azureSearchProperties.getAdminKey(), "POST", jsonRequestBody);
		HttpResponse<String> response = HttpUtils.sendRequest(request);
		return HttpUtils.isSuccessResponse(response);
		
	}
	
}
