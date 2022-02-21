package ai.hyperlearning.ontopop.exceptions.graph;

/**
 * Custom Exception - Invalid Gremlin query
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidGremlinQueryException extends RuntimeException {

    private static final long serialVersionUID = -7230432565242085128L;
    
    public InvalidGremlinQueryException(String message) {
        super(message);
    }

}
