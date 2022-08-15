package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Download Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDownloadException extends OntoPopException {

    private static final long serialVersionUID = -1446897825165441187L;
    private static final String CLASS_NAME = 
            OntologyDownloadException.class.getSimpleName();
    
    public OntologyDownloadException() {
        super(CLASS_NAME);
    }

}
