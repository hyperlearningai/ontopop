package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Ontology Data PUT Author Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataInvalidException extends OntoPopException {

    private static final long serialVersionUID = 2237417330087616965L;
    private static final String CLASS_NAME = 
            OntologyDataInvalidException.class.getSimpleName();
    
    public enum ErrorKey {
        
        AUTHOR("Author"), 
        FORMAT("Format"); 
        
        private final String key;
        
        private ErrorKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
        
    }
    
    public OntologyDataInvalidException() {
        super(CLASS_NAME);
    }
    
    public OntologyDataInvalidException(ErrorKey errorKey) {
        super(CLASS_NAME, errorKey.getKey());
    }

}
