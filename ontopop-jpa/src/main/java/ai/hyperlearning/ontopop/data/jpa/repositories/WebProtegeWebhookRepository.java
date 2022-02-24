package ai.hyperlearning.ontopop.data.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ai.hyperlearning.ontopop.model.webprotege.WebProtegeMaxRevisionNumber;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;

/**
 * WebProtege Webhook Repository
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Repository
public interface WebProtegeWebhookRepository 
        extends CrudRepository<WebProtegeWebhook, Long> {
    
    @Query("SELECT w FROM WebProtegeWebhook w WHERE w.projectId = ?1")
    List<WebProtegeWebhook> findByWebProtegeProjectId(String webProtegeProjectId);
    
    @Query("SELECT w FROM WebProtegeWebhook w WHERE w.projectId = ?1 AND w.revisionNumber = ?2")
    List<WebProtegeWebhook> findByWebProtegeProjectIdAndRevisionNumber(String webProtegeProjectId, int revisionNumber);
    
    @Query("SELECT w FROM WebProtegeWebhook w WHERE w.projectId = ?1 AND w.userId = ?2")
    List<WebProtegeWebhook> findByWebProtegeProjectIdAndUserId(String webProtegeProjectId, String userId);
    
    @Query("SELECT new ai.hyperlearning.ontopop.model.webprotege.WebProtegeMaxRevisionNumber(w.projectId, max(w.revisionNumber)) FROM WebProtegeWebhook w")
    List<WebProtegeMaxRevisionNumber> getMaxRevisionNumberPerWebProtegeWebhook();

}
