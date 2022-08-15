package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Management Exception - Create Ontology Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyCreateException extends OntoPopException {

    private static final long serialVersionUID = -151692995690722571L;
    private static final String CLASS_NAME = 
            OntologyCreateException.class.getSimpleName();

    public OntologyCreateException() {
        super(CLASS_NAME);
    }

}
