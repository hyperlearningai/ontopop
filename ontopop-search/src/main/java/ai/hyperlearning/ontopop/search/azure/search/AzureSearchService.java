package ai.hyperlearning.ontopop.search.azure.search;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.azure.search.documents.SearchClient;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.models.FieldBuilderOptions;
import com.azure.search.documents.indexes.models.SearchField;
import com.azure.search.documents.indexes.models.SearchIndex;
import com.azure.search.documents.models.IndexDocumentsResult;
import com.azure.search.documents.models.SearchResult;

import ai.hyperlearning.ontopop.search.SearchService;
import ai.hyperlearning.ontopop.search.model.SimpleIndexVertex;

/**
 * Microsoft Azure Search Service.
 * 
 * NOTE - Azure Search only supports fields with pre-defined EDM types.
 * https://docs.microsoft.com/en-gb/rest/api/searchservice/supported-data-types
 * unlike Elasticsearch that will create implicit properties (e.g. from a hash
 * map of properties) if not explicitly defined beforehand. Therefore for Azure
 * Search, we must explicitly create the schema. The primary (and major)
 * disadvantage when using Azure Search is that we cannot index dynamic fields.
 * Therefore our AzureSimpleIndexVertex class must be an exhaustive as possible.
 * Furthermore, Azure Search only supports string-based Keys. It is strongly
 * recommended therefore to use ElasticSearch over Azure Search if possible.
 * 
 * TODO - Refactor boilerplate code to use bean mappers when mapping from
 * SimpleIndexVertex to AzureSimpleIndexVertex POJOs.
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.search.service",
        havingValue = "azure-search")
public class AzureSearchService implements SearchService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AzureSearchService.class);

    @Autowired
    @Qualifier("azureSearchIndexNoIndexClient")
    private SearchIndexClient searchIndexClient;

    @Autowired
    private BeanFactory beanFactory;

    /**************************************************************************
     * SEARCH CLIENT MANAGEMENT
     *************************************************************************/

    @Override
    public void cleanup() throws Exception {

    }

    /**************************************************************************
     * SEARCH INDEX MANAGEMENT
     *************************************************************************/

    /**
     * Create the new index with an explicit schema. Azure Search only supports
     * fields with well-defined EDM types.
     * https://docs.microsoft.com/en-gb/rest/api/searchservice/supported-data-types
     * unlike Elasticsearch that will create implicit properties (e.g. from a
     * hashmap of properties) if not explicitly defined. Therefore for Azure
     * Search, we must explicitly create the schema. The primary disadvantage
     * when using Azure Search is that we cannot index dynamic fields. Therefore
     * our AzureSimpleIndexVertex must be an exhaustive as possible.
     * Furthermore, Azure Search only supports string-based Keys. It is strongly
     * recommended therefore to use ElasticSearch over Azure Search.
     */

    @Override
    public void createIndex(String indexName) {

        // Check if the search index already exists
        SearchIndex existingIndex = null;
        try {
            existingIndex = searchIndexClient.getIndex(indexName);
        } catch (Exception e) {
            LOGGER.debug(
                    "Index does not already exist. " + "Creating index: {}.",
                    indexName);
        }

        // Do not create the index if it already exists
        if (existingIndex != null) {
            LOGGER.warn("Index '{}' already exists. Skipping index creation.",
                    indexName);
            return;
        }

        // Create the search index using the AzureSimpleIndexVertex model
        List<SearchField> indexFields = SearchIndexClient.buildSearchFields(
                AzureSimpleIndexVertex.class, new FieldBuilderOptions());
        SearchIndex newIndex = new SearchIndex(indexName, indexFields);
        searchIndexClient.createIndex(newIndex);

    }
    
    @Override
    public void createIndex(String indexName, int shards, int replicas) {
        createIndex(indexName);
    }

    @Override
    public void deleteIndex(String indexName) {
        searchIndexClient.deleteIndex(indexName);
    }

    /**************************************************************************
     * DOCUMENT MANAGEMENT
     *************************************************************************/

    @Override
    public AzureSimpleIndexVertex getDocument(String indexName, long vertexId) {
        SearchClient client =
                beanFactory.getBean(SearchClient.class, indexName);
        return client.getDocument(String.valueOf(vertexId),
                AzureSimpleIndexVertex.class);
    }

    /**
     * Simple free-text search TODO - Refactor to use Azure Search
     * SearchOptions()
     */

    @Override
    public Object search(String indexName, String propertyKey, String query,
            boolean exact, boolean and) {
        SearchClient client =
                beanFactory.getBean(SearchClient.class, indexName);
        Set<AzureSimpleIndexVertex> vertices = new LinkedHashSet<>();
        for (SearchResult searchResult : client.search("luxury")) {
            AzureSimpleIndexVertex vertex =
                    searchResult.getDocument(AzureSimpleIndexVertex.class);
            vertices.add(vertex);
        }
        return vertices;
    }

    /**
     * Simple free-text search TODO - Refactor to use Azure Search
     * SearchOptions()
     */

    @Override
    public Object search(String indexName, String propertyKey, String query,
            boolean and, int minimumShouldMatchPercentage) {
        SearchClient client =
                beanFactory.getBean(SearchClient.class, indexName);
        Set<AzureSimpleIndexVertex> vertices = new LinkedHashSet<>();
        for (SearchResult searchResult : client.search("luxury")) {
            AzureSimpleIndexVertex vertex =
                    searchResult.getDocument(AzureSimpleIndexVertex.class);
            vertices.add(vertex);
        }
        return vertices;
    }

    @Override
    public void indexDocuments(String indexName,
            Set<SimpleIndexVertex> vertices) {

        // Generate AzureSimpleIndexVertex POJOs
        Set<AzureSimpleIndexVertex> azureVertices = new LinkedHashSet<>();
        for (SimpleIndexVertex vertex : vertices) {

            // TODO - Refactor boilerplate code to use bean mappers
            AzureSimpleIndexVertex azureVertex = new AzureSimpleIndexVertex();
            Map<String, Object> properties = vertex.getProperties();

            // Common vertex properties
            azureVertex.setVertexId(String.valueOf(vertex.getVertexId()));
            azureVertex.setLabel(vertex.getLabel());
            azureVertex.setRdfsLabel(properties.containsKey("label")
                    ? (String) properties.get("label")
                    : null);
            azureVertex.setIri(properties.containsKey("iri")
                    ? (String) properties.get("iri")
                    : null);
            azureVertex.setOntologyId(properties.containsKey("ontologyId")
                    ? (Integer) properties.get("ontologyId")
                    : null);
            azureVertex.setVertexKey(properties.containsKey("vertexKey")
                    ? (String) properties.get("vertexKey")
                    : null);
            azureVertex.setLatestGitWebhookId(
                    properties.containsKey("latestGitWebhookId")
                            ? (Long) properties.get("latestGitWebhookId")
                            : null);

            // Example domain-specific vertex properties
            azureVertex.setDefinition(properties.containsKey("definition")
                    ? (String) properties.get("definition")
                    : null);
            azureVertex.setBusinessArea(properties.containsKey("businessArea")
                    ? (String) properties.get("businessArea")
                    : null);
            azureVertex.setSubdomain(properties.containsKey("subdomain")
                    ? (String) properties.get("subdomain")
                    : null);
            azureVertex.setDataSource(properties.containsKey("dataSource")
                    ? (String) properties.get("dataSource")
                    : null);
            azureVertex.setSynonym(properties.containsKey("synonym")
                    ? (String) properties.get("synonym")
                    : null);
            azureVertex.setExample(properties.containsKey("example")
                    ? (String) properties.get("example")
                    : null);

            // Add to the set of vertices to be indexed
            azureVertices.add(azureVertex);

        }

        // Bulk index the vertices
        SearchClient client =
                beanFactory.getBean(SearchClient.class, indexName);
        IndexDocumentsResult result =
                client.mergeOrUploadDocuments(azureVertices);
        LOGGER.debug("Indexed {} document(s).", result.getResults().size());

    }

    @Override
    public void indexDocument(String indexName, SimpleIndexVertex vertex) {
        indexDocuments(indexName, new HashSet<>(Arrays.asList(vertex)));
    }

    /**
     * Naively delete all documents in a given index TODO - Refactor to use a
     * matchAllQuery analogous to ElasticSearch
     */

    @Override
    public void deleteAllDocuments(String indexName) {
        // deleteIndex(indexName);
        // createIndex(indexName);
    }

}
