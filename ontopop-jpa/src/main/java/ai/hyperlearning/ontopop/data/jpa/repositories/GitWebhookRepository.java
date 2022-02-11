package ai.hyperlearning.ontopop.data.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ai.hyperlearning.ontopop.model.git.GitWebhook;

/**
 * Webhook Event Repository
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Repository
public interface GitWebhookRepository
        extends CrudRepository<GitWebhook, Long> {

    @Query("SELECT w FROM GitWebhook w WHERE w.ontology.id = ?1")
    List<GitWebhook> findByOntologyId(int ontologyId);

    @Query("SELECT w FROM GitWebhook w WHERE w.ontology.id = ?1 AND w.id = ?2")
    Optional<GitWebhook> findByOntologyIdAndGitWebhookId(
            int ontologyId, long gitWebhookId);

}
