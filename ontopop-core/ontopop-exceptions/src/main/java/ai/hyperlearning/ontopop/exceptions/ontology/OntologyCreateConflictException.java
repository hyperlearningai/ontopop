package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Management Exception - Create Ontology Conflict Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyCreateConflictException extends OntoPopException {
    
    private static final long serialVersionUID = 6879076162546979381L;
    private static final String CLASS_NAME = 
            OntologyCreateConflictException.class.getSimpleName();
    
    public OntologyCreateConflictException() {
        super(CLASS_NAME);
    }

}
