package ai.hyperlearning.ontology.services.security.auth.framework;

import java.util.Optional;

import ai.hyperlearning.ontology.services.model.vocabulary.Synonym;

/**
 * Synonym Management Authorisation Framework
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class SynonymManagementAuthorisationFramework {
	
	/**
	 * Check whether the given user is authorised to take
	 * synonym management actions on the given synonym
	 * @param synonym
	 * @param userId
	 * @return
	 */
	
	public static boolean isSynonymManagementAuthorized(
			Synonym synonym, String userId) {
		return synonym != null && synonym.getUserId().equals(userId);
	}
	
	/**
	 * Check whether the given user is authorised to take
	 * synonym management actions on the given optional synonym object
	 * @param optionalSynonym
	 * @param userId
	 * @return
	 */
	
	public static boolean isSynonymManagementAuthorized(
			Optional<Synonym> optionalSynonym, String userId) {
		if (!optionalSynonym.isEmpty()) {
			Synonym synonym = optionalSynonym.get();
			return synonym != null && synonym.getUserId().equals(userId);
		} else return false;
	}

}
