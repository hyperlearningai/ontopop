package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Model - Create Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyCreationException extends RuntimeException {

    private static final long serialVersionUID = -151692995690722571L;

    public OntologyCreationException() {
        super("An error was encountered when creating a new ontology.");
    }

}
