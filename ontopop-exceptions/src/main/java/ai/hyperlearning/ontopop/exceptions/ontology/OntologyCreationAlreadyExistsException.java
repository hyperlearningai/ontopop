package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Management Exception - Create Already Exists Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyCreationAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 6879076162546979381L;
    
    public OntologyCreationAlreadyExistsException() {
        super("An error was encountered when creating a new ontology - "
                + "this ontology already exists.");
    }
    
    public OntologyCreationAlreadyExistsException(String message) {
        super(message);
    }

}
