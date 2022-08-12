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
    private static final String DEFAULT_I18N_KEY = "Default";
    private final String i18nKey;
    
    protected OntoPopException(String className) {
        super(DEFAULT_BUNDLE.getString(className + DEFAULT_I18N_KEY));
        this.i18nKey = className + DEFAULT_I18N_KEY;
    }
    
    protected OntoPopException(String className, String i18nKey) {
        super(DEFAULT_BUNDLE.getString(className + i18nKey));
        this.i18nKey = className + i18nKey;
    }
    
    public String getI18nKey() {
        return i18nKey;
    }

}
