package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Data Parsing Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataParsingException extends OntoPopException {

    private static final long serialVersionUID = 2883984532509572567L;
    private static final String CLASS_NAME = 
            OntologyDataParsingException.class.getSimpleName();
    
    public OntologyDataParsingException() {
        super(CLASS_NAME);
    }

}
