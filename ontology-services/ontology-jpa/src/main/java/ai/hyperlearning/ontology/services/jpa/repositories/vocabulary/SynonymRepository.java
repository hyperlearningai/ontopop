package ai.hyperlearning.ontology.services.jpa.repositories.vocabulary;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ai.hyperlearning.ontology.services.model.vocabulary.Synonym;

/**
 * Synonym Repository
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public interface SynonymRepository extends CrudRepository<Synonym, Integer> {
	
	@Query("SELECT s FROM Synonym s WHERE s.nodeId = ?1")
	List<Synonym> findByNodeId(int nodeId);

}
