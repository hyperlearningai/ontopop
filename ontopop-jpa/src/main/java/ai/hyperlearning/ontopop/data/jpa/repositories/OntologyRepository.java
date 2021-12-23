package ai.hyperlearning.ontopop.data.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ai.hyperlearning.ontopop.model.ontology.Ontology;

/**
 * Ontology Repository
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Repository
public interface OntologyRepository extends CrudRepository<Ontology, Integer> {
	
	@Query("SELECT o FROM Ontology o WHERE o.repoUrl = ?1 AND o.repoOwner = ?2 AND o.repoResourcePath = ?3 AND o.repoBranch = ?4")
	List<Ontology> findByRepoUrlOwnerPathBranch(
			String repoUrl, String repoOwner, 
			String repoResourcePath, String repoBranch);

}
