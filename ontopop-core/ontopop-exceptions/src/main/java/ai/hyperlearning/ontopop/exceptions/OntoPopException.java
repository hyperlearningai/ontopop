package ai.hyperlearning.ontopop.exceptions;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Custom OntoPop Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public abstract class OntoPopException extends RuntimeException {
    
    private static final long serialVersionUID = 6703200985692274276L;
    private static final String RESOURCE_BUNDLE_FILE_NAME_PREFIX = 
            "ErrorMessages";
    private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle
            .getBundle(RESOURCE_BUNDLE_FILE_NAME_PREFIX, Locale.ROOT);
    private static final String DEFAULT_ERROR_KEY_SUFFIX = "Default";
    private final String errorKey;
    
    protected OntoPopException(String className) {
        super(DEFAULT_BUNDLE.getString(className + DEFAULT_ERROR_KEY_SUFFIX));
        this.errorKey = className + DEFAULT_ERROR_KEY_SUFFIX;
    }
    
    protected OntoPopException(String className, String errorKey) {
        super(DEFAULT_BUNDLE.getString(className + errorKey));
        this.errorKey = className + errorKey;
    }
    
    public String getErrorKey() {
        return errorKey;
    }

}
