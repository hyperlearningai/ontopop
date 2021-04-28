package ai.hyperlearning.ontology.services.jpa.repositories.vocabulary;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ai.hyperlearning.ontology.services.model.vocabulary.Term;

/**
 * Related Terms Repository
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */


public interface TermRepository extends CrudRepository<Term, Integer> {
	
	@Query("SELECT t FROM Term t WHERE t.nodeId = ?1")
	List<Term> findByNodeId(int nodeId);

}
