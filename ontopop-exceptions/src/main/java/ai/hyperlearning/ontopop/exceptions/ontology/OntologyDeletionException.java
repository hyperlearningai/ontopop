package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Model - Delete Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDeletionException extends RuntimeException {

	private static final long serialVersionUID = -7585100726564618450L;
	
	public OntologyDeletionException(int id) {
		super("An error was encountered when deleting the ontology "
				+ "with ID " + id + ".");
	}

}
