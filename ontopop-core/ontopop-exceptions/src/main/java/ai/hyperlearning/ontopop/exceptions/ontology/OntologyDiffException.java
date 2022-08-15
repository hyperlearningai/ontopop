package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Ontology Diff Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDiffException extends OntoPopException {

    private static final long serialVersionUID = -5191943095095430857L;
    private static final String CLASS_NAME = 
            OntologyDiffException.class.getSimpleName();
    
    public OntologyDiffException() {
        super(CLASS_NAME);
    }

}
