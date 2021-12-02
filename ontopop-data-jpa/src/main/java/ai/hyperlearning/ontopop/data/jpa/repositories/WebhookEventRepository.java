package ai.hyperlearning.ontopop.data.jpa.repositories;

import org.springframework.data.repository.CrudRepository;

import ai.hyperlearning.ontopop.model.git.WebhookEvent;

/**
 * Webhook Event Repository
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface WebhookEventRepository extends CrudRepository<WebhookEvent, Long> {

}
