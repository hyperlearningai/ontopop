package ai.hyperlearning.ontology.services.security.auth.framework;

import java.util.Optional;

import ai.hyperlearning.ontology.services.model.collaboration.Note;

/**
 * Note Management Authorisation Framework
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class NoteManagementAuthorisationFramework {
	
	/**
	 * Check whether the given user is authorised to take
	 * note management actions on the given note
	 * @param note
	 * @param userId
	 * @return
	 */
	
	public static boolean isNoteManagementAuthorized(
			Note note, String userId) {
		return note != null && note.getUserId().equals(userId);
	}
	
	/**
	 * Check whether the given user is authorised to take
	 * note management actions on the given optional note object
	 * @param optionalNote
	 * @param userId
	 * @return
	 */
	
	public static boolean isNoteManagementAuthorized(
			Optional<Note> optionalNote, String userId) {
		if (!optionalNote.isEmpty()) {
			Note note = optionalNote.get();
			return note != null && note.getUserId().equals(userId);
		} else return false;
	}

}
