package ai.hyperlearning.ontopop.exceptions.ontology;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Custom Exception - Ontology Diff Invalid Request Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDiffInvalidRequestException extends OntoPopException {

    private static final long serialVersionUID = -7643909324750869755L;
    private static final String CLASS_NAME = 
            OntologyDiffInvalidRequestException.class.getSimpleName();
    
    public enum ErrorKey {
        
        INVALID_TIMESTAMP("InvalidTimestamp"), 
        MISSING_PARAMETER("MissingParameter"); 
        
        private final String key;
        
        private ErrorKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
        
    }

    public OntologyDiffInvalidRequestException() {
        super(CLASS_NAME);
    }
    
    public OntologyDiffInvalidRequestException(ErrorKey errorKey) {
        super(CLASS_NAME, errorKey.getKey());
    }

}
