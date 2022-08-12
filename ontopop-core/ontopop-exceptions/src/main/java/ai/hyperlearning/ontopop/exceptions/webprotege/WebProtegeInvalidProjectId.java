package ai.hyperlearning.ontopop.exceptions.webprotege;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * WebProtege Invalid Project ID Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeInvalidProjectId extends OntoPopException {
    
    private static final long serialVersionUID = -8737889790985389924L;
    private static final String CLASS_NAME = 
            WebProtegeInvalidProjectId.class.getSimpleName();

    public enum ErrorKey {
        
        IS_EMPTY("IsEmpty"), 
        INVALID_CHARACTER("InvalidCharacter"), 
        INVALID_LENGTH("InvalidLength"); 
        
        private final String key;
        
        private ErrorKey(String key) {
            this.key = key;
        }
        
        public String getKey() {
            return key;
        }
        
    }
    
    public WebProtegeInvalidProjectId() {
        super(CLASS_NAME);
    }
    
    public WebProtegeInvalidProjectId(ErrorKey errorKey) {
        super(CLASS_NAME, errorKey.getKey());
    }

}
