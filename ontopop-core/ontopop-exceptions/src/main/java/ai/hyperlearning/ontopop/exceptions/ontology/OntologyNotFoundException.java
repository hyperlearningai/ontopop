package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Management Exception - Not Found Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyNotFoundException extends OntoPopException {

    private static final long serialVersionUID = -4019048840462057702L;
    private static final String CLASS_NAME = 
            OntologyNotFoundException.class.getSimpleName();
    
    public OntologyNotFoundException() {
        super(CLASS_NAME);
    }

}
