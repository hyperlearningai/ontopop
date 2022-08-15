package ai.hyperlearning.ontopop.exceptions.triplestore;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Invalid SPARQL query
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidSparqlQueryException extends OntoPopException {

    private static final long serialVersionUID = 7212822775044940190L;
    private static final String CLASS_NAME = 
            InvalidSparqlQueryException.class.getSimpleName();
    
    public InvalidSparqlQueryException() {
        super(CLASS_NAME);
    }

}
