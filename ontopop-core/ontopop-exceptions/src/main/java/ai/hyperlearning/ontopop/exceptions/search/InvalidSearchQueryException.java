package ai.hyperlearning.ontopop.exceptions.search;

/**
 * Custom Exception - Invalid Search query
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidSearchQueryException extends RuntimeException {

    private static final long serialVersionUID = -7901295309403144312L;
    
    public InvalidSearchQueryException(String message) {
        super(message);
    }

}
