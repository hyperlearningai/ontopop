package ai.hyperlearning.ontology.services.security.auth.framework;

import java.util.Optional;

import ai.hyperlearning.ontology.services.model.vocabulary.Term;

/**
 * Related Terms Management Authorisation Framework
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class TermManagementAuthorisationFramework {
	
	/**
	 * Check whether the given user is authorised to take
	 * term management actions on the given related term
	 * @param synonym
	 * @param userId
	 * @return
	 */
	
	public static boolean isTermManagementAuthorized(
			Term term, String userId) {
		return term != null && term.getUserId().equals(userId);
	}
	
	/**
	 * Check whether the given user is authorised to take
	 * term management actions on the given optional term object
	 * @param optionalSynonym
	 * @param userId
	 * @return
	 */
	
	public static boolean isTermManagementAuthorized(
			Optional<Term> optionalTerm, String userId) {
		if (!optionalTerm.isEmpty()) {
			Term term = optionalTerm.get();
			return term != null && term.getUserId().equals(userId);
		} else return false;
	}

}
