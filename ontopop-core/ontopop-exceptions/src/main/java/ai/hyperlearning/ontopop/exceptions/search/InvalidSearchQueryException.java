package ai.hyperlearning.ontopop.exceptions.search;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Invalid Search query
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidSearchQueryException extends OntoPopException {

    private static final long serialVersionUID = -7901295309403144312L;
    private static final String CLASS_NAME = 
            InvalidSearchQueryException.class.getSimpleName();
    
    public InvalidSearchQueryException() {
        super(CLASS_NAME);
    }

}
