package ai.hyperlearning.ontology.services.jpa.repositories.collaboration;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ai.hyperlearning.ontology.services.model.collaboration.Note;

/**
 * Note Repository
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public interface NoteRepository extends CrudRepository<Note, Integer> {
	
	@Query("SELECT n FROM Note n WHERE n.type = ?1")
	List<Note> findByType(String type);
	
	@Query("SELECT n FROM Note n WHERE n.type = ?1 AND n.nodeId = ?2")
	List<Note> findByTypeAndNodeId(String type, int nodeId);
	
	@Query("SELECT n FROM Note n WHERE n.type = ?1 AND n.edgeId = ?2")
	List<Note> findByTypeAndEdgeId(String type, int edgeId);

}
