package ai.hyperlearning.ontopop.exceptions.triplestore;

/**
 * Triplestore SPARQL Query Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class TriplestoreSparqlQueryException extends RuntimeException {

    private static final long serialVersionUID = 1496014563960578497L;
    
    public TriplestoreSparqlQueryException() {
        super("An error was encountered when executing the SPARQL query "
                + "against the triplestore.");
    }
    
    public TriplestoreSparqlQueryException(String message) {
        super(message);
    }

}
