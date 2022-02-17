package ai.hyperlearning.ontopop.search;

import java.util.Set;

import ai.hyperlearning.ontopop.search.model.SimpleIndexVertex;

/**
 * Search Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface SearchService {

    /**************************************************************************
     * SEARCH CLIENT MANAGEMENT
     *************************************************************************/

    public void cleanup() throws Exception;

    /**************************************************************************
     * SEARCH INDEX MANAGEMENT
     *************************************************************************/

    public void createIndex(String indexName);
    
    public void createIndex(String indexName, int shards, int replicas);

    public void deleteIndex(String indexName);

    /**************************************************************************
     * DOCUMENT MANAGEMENT
     *************************************************************************/

    public Object getDocument(String indexName, long vertexId);

    public Object search(String indexName, String propertyKey, String query,
            boolean exact, boolean and);

    public Object search(String indexName, String propertyKey, String query,
            boolean and, int minimumShouldMatchPercentage);

    public void indexDocuments(String indexName,
            Set<SimpleIndexVertex> vertices);

    public void indexDocument(String indexName, SimpleIndexVertex vertex);

    public void deleteAllDocuments(String indexName);

}
