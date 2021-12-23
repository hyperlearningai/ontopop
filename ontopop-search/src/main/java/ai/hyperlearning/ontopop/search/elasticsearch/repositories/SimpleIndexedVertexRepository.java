package ai.hyperlearning.ontopop.search.elasticsearch.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ai.hyperlearning.ontopop.search.model.SimpleIndexedVertex;

/**
 * Simple Indexed Vertex Search Repository
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface SimpleIndexedVertexRepository extends ElasticsearchRepository<SimpleIndexedVertex, String> {

}
