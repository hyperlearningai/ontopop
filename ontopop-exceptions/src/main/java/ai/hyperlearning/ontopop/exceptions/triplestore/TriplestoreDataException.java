package ai.hyperlearning.ontopop.exceptions.triplestore;

/**
 * Triplestore Data Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class TriplestoreDataException extends RuntimeException {

    private static final long serialVersionUID = 8759407420185700256L;
    
    public TriplestoreDataException() {
        super("An error was encountered when attempting to retrieve the "
                + "triplestore data for an ontology.");
    }
    
    public TriplestoreDataException(String message) {
        super(message);
    }

}
