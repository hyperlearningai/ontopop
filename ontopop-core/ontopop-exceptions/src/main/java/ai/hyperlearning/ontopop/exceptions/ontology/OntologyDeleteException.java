package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Management Exception - Delete Ontology Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDeleteException extends OntoPopException {

    private static final long serialVersionUID = -7585100726564618450L;
    private static final String CLASS_NAME = 
            OntologyDeleteException.class.getSimpleName();

    public OntologyDeleteException() {
        super(CLASS_NAME);
    }

}
