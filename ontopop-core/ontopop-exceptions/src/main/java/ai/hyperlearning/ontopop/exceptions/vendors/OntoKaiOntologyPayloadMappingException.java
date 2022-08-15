package ai.hyperlearning.ontopop.exceptions.vendors;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * OntoKai Ontology Payload Mapping Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyPayloadMappingException extends OntoPopException {

    private static final long serialVersionUID = -4661079657140803812L;
    private static final String CLASS_NAME = 
            OntoKaiOntologyPayloadMappingException.class.getSimpleName();
    
    public OntoKaiOntologyPayloadMappingException() {
        super(CLASS_NAME);
    }

}
