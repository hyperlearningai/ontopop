package ai.hyperlearning.ontology.services.jpa.repositories.ui;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ai.hyperlearning.ontology.services.model.ui.Styling;

/**
 * Styling Repository
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Repository
public interface StylingRepository extends CrudRepository<Styling, String> {

}
