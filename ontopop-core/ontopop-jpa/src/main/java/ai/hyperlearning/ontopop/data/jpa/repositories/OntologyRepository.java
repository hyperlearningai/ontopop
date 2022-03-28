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
    List<Ontology> findByRepoUrlOwnerPathBranch(String repoUrl,
            String repoOwner, String repoResourcePath, String repoBranch);
    
    @Query("SELECT o FROM Ontology o WHERE o.repoUrl = ?1 AND o.repoResourcePath = ?2 AND o.repoBranch = ?3 ")
    List<Ontology> findByRepoUrlPathBranch(String repoUrl, 
            String repoResourcePath, String repoBranch);
    
    @Query("SELECT o FROM Ontology o WHERE o.webProtegeProjectId = ?1")
    List<Ontology> findByWebProtegeProjectId(String webProtegeProjectId);
    
    // Spring Data JPA dynamic query creation
    // Reference: https://docs.spring.io/spring-data/jpa/docs/1.10.1.RELEASE/reference/html/#jpa.query-methods.query-creation
    List<Ontology> findByWebProtegeProjectIdIsNotNull();

}
