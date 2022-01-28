package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Model - Update Secret Data Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyUpdateSecretDataException extends RuntimeException {

    private static final long serialVersionUID = 499327861970184179L;

    public OntologyUpdateSecretDataException(int id) {
        super("An error was encountered when updating the secret attributes "
                + "of the ontology with ID " + id + ".");
    }

}
