package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Model - Not Found Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -4019048840462057702L;
	
	public OntologyNotFoundException(int id) {
		super("Ontology with ID " + id + " not found.");
	}
	
}
