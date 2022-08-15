package ai.hyperlearning.ontopop.exceptions.vendors;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Invalid OntoKai Ontology Payload Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyInvalidPayloadException extends OntoPopException {

    private static final long serialVersionUID = 7837043488748154108L;
    private static final String CLASS_NAME = 
            OntoKaiOntologyInvalidPayloadException.class.getSimpleName();
    
    public OntoKaiOntologyInvalidPayloadException() {
        super(CLASS_NAME);
    }

}
