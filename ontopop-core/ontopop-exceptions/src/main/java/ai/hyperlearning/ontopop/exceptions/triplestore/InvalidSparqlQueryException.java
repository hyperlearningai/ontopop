package ai.hyperlearning.ontopop.exceptions.triplestore;

/**
 * Custom Exception - Invalid SPARQL query
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidSparqlQueryException extends RuntimeException {

    private static final long serialVersionUID = 7212822775044940190L;
    
    public InvalidSparqlQueryException(String message) {
        super(message);
    }

}
