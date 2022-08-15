package ai.hyperlearning.ontopop.exceptions.graph;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Invalid Gremlin query
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidGremlinQueryException extends OntoPopException {

    private static final long serialVersionUID = -7230432565242085128L;
    private static final String CLASS_NAME = 
            InvalidGremlinQueryException.class.getSimpleName();
    
    public InvalidGremlinQueryException() {
        super(CLASS_NAME);
    }

}
