package ai.hyperlearning.ontopop.search.elasticsearch;

import static org.elasticsearch.index.query.QueryBuilders.fuzzyQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.simpleQueryStringQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.search.SearchService;
import ai.hyperlearning.ontopop.search.model.SimpleIndexVertex;

/**
 * Elasticsearch Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.search.service",
        havingValue = "elasticsearch")
public class ElasticsearchService implements SearchService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ElasticsearchService.class);

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    /**************************************************************************
     * SEARCH CLIENT MANAGEMENT
     *************************************************************************/

    @Override
    public void cleanup() throws Exception {

    }

    /**************************************************************************
     * SEARCH INDEX MANAGEMENT
     *************************************************************************/

    @Override
    public void createIndex(String indexName) {
        if (!elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName))
                .exists()) {
            elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName))
                    .create();
        } else {
            LOGGER.warn("Index '{}' already exists. Skipping index creation.",
                    indexName);
        }
    }
    
    public void createIndex(String indexName, int shards, int replicas) {
        Map<String, Object> settings = Map.of( 
                "number_of_shards" , shards, 
                "number_of_replicas", replicas);
        if (!elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName))
                .exists()) {
            elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName))
                    .create(settings);
        } else {
            LOGGER.warn("Index '{}' already exists. Skipping index creation.",
                    indexName);
        }
    }

    @Override
    public void deleteIndex(String indexName) {
        if (elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName))
                .exists()) {
            elasticsearchTemplate.indexOps(IndexCoordinates.of(indexName))
                    .delete();
        } else {
            LOGGER.warn("Index '{}' does not exist. Skipping index deletion.",
                    indexName);
        }
    }

    /**************************************************************************
     * DOCUMENT MANAGEMENT
     *************************************************************************/

    @Override
    public SimpleIndexVertex getDocument(String indexName, long vertexId) {
        return elasticsearchTemplate.get(String.valueOf(vertexId),
                SimpleIndexVertex.class, IndexCoordinates.of(indexName));
    }

    @Override
    public void indexDocuments(String indexName,
            Set<SimpleIndexVertex> vertices) {
        elasticsearchTemplate.save(vertices, IndexCoordinates.of(indexName));
    }

    @Override
    public void indexDocument(String indexName, SimpleIndexVertex vertex) {
        elasticsearchTemplate.save(vertex, IndexCoordinates.of(indexName));
    }

    @Override
    public void deleteAllDocuments(String indexName) {
        final Query deleteQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery()).build();
        long documentCount = elasticsearchTemplate.count(deleteQuery, 
                IndexCoordinates.of(indexName));
        if ( documentCount > 0 )
            elasticsearchTemplate.delete(deleteQuery,
                    IndexCoordinates.of(indexName));
    }
    
    @Override
    public void deleteAllDocuments(String indexName, Class<?> cls) {
        final Query deleteQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery()).build();
        long documentCount = elasticsearchTemplate.count(deleteQuery, 
                IndexCoordinates.of(indexName));
        if ( documentCount > 0 )
            elasticsearchTemplate.delete(deleteQuery, cls, 
                    IndexCoordinates.of(indexName));
    }
    
    @Override
    public void deleteDocumentsByPropertyKeyValue(String indexName, 
            String propertyKey, Object propertyValue) {
        final Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery(propertyKey, propertyValue))
                .build();
        long documentCount = elasticsearchTemplate.count(searchQuery, 
                IndexCoordinates.of(indexName));
        if ( documentCount > 0 )
            elasticsearchTemplate.delete(searchQuery,
                    IndexCoordinates.of(indexName));
        else {
            final Query searchQueryAttempt2 = new NativeSearchQueryBuilder()
                    .withQuery(matchQuery("properties." + propertyKey, propertyValue))
                    .build();
            long documentCountAttempt2 = elasticsearchTemplate.count(
                    searchQueryAttempt2, IndexCoordinates.of(indexName));
            if ( documentCountAttempt2 > 0 )
                elasticsearchTemplate.delete(searchQueryAttempt2,
                        IndexCoordinates.of(indexName));
        }
    }
    
    @Override
    public void deleteDocumentsByPropertyKeyValue(String indexName, 
            String propertyKey, Object propertyValue, Class<?> cls) {
        final Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery(propertyKey, propertyValue))
                .build();
        long documentCount = elasticsearchTemplate.count(searchQuery, 
                IndexCoordinates.of(indexName));
        if ( documentCount > 0 )
            elasticsearchTemplate.delete(searchQuery, cls, 
                    IndexCoordinates.of(indexName));
        else {
            final Query searchQueryAttempt2 = new NativeSearchQueryBuilder()
                    .withQuery(matchQuery("properties." + propertyKey, propertyValue))
                    .build();
            long documentCountAttempt2 = elasticsearchTemplate.count(
                    searchQueryAttempt2, IndexCoordinates.of(indexName));
            if ( documentCountAttempt2 > 0 )
                elasticsearchTemplate.delete(searchQueryAttempt2, cls, 
                        IndexCoordinates.of(indexName));
        }
    }
    
    /**************************************************************************
     * SEARCH
     *************************************************************************/
    
    @Override
    public Object search(String indexName, String query) {
        
        // Simple fault-tolerant search across all documents
        SimpleQueryStringBuilder simpleQueryStringBuilder = 
                simpleQueryStringQuery(query);
        final Query searchQuery = new NativeSearchQueryBuilder().withQuery(
                simpleQueryStringBuilder)
                .build();
        return elasticsearchTemplate.search(searchQuery,
                SimpleIndexVertex.class, IndexCoordinates.of(indexName));
        
    }
    
    @Override
    public SearchHits<SimpleIndexVertex> search(String indexName,
            List<String> propertyKeys, String query, boolean and) {
        
        // Build the query across multiple fields
        Operator operator = and ? Operator.AND : Operator.OR;
        MultiMatchQueryBuilder multiMatchQueryBuilder = multiMatchQuery(query)
                .operator(operator)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for ( String propertyKey : propertyKeys ) {
            multiMatchQueryBuilder = multiMatchQueryBuilder.field(propertyKey);
            highlightBuilder = highlightBuilder.field(propertyKey);
        }
            
        // Build and execute the search query
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .build();
        final Query searchQuery = nativeSearchQuery;
        return elasticsearchTemplate.search(searchQuery,
                SimpleIndexVertex.class, IndexCoordinates.of(indexName));
        
    }

    @Override
    public SearchHits<SimpleIndexVertex> search(String indexName,
            String propertyKey, String query, boolean exact, boolean and) {

        Operator operator = and ? Operator.AND : Operator.OR;
        final Query searchQuery = exact
                ? new NativeSearchQueryBuilder().withQuery(
                        matchQuery(propertyKey, query).operator(operator))
                        .withHighlightFields(
                                new HighlightBuilder.Field(propertyKey))
                        .build()
                : new NativeSearchQueryBuilder()
                        .withQuery(fuzzyQuery(propertyKey, query))
                        .withHighlightFields(
                                new HighlightBuilder.Field(propertyKey))
                        .build();
        return elasticsearchTemplate.search(searchQuery,
                SimpleIndexVertex.class, IndexCoordinates.of(indexName));

    }

    @Override
    public SearchHits<SimpleIndexVertex> search(String indexName,
            String propertyKey, String query, boolean and,
            int minimumShouldMatchPercentage) {

        Operator operator = and ? Operator.AND : Operator.OR;
        String minimumShouldMatchPct =
                String.valueOf(minimumShouldMatchPercentage) + "%";
        final Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery(propertyKey, query).operator(operator)
                        .minimumShouldMatch(minimumShouldMatchPct))
                .withHighlightFields(
                        new HighlightBuilder.Field(propertyKey))
                .build();
        return elasticsearchTemplate.search(searchQuery,
                SimpleIndexVertex.class, IndexCoordinates.of(indexName));

    }

}
