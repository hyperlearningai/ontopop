package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Management Exception - Update Secret Data Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyUpdateException extends OntoPopException {

    private static final long serialVersionUID = 499327861970184179L;
    private static final String CLASS_NAME = 
            OntologyUpdateException.class.getSimpleName();
    
    public enum ErrorKey {
        
        NON_SENSITIVE("NonSensitive"), 
        SENSITIVE("Sensitive"); 
        
        private final String key;
        
        private ErrorKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
        
    }

    public OntologyUpdateException() {
        super(CLASS_NAME);
    }
    
    public OntologyUpdateException(ErrorKey errorKey) {
        super(CLASS_NAME, errorKey.getKey());
    }

}
