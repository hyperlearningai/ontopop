package ai.hyperlearning.ontopop.exceptions.webprotege;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * WebProtege Authentication Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeAuthenticationException extends OntoPopException {

    private static final long serialVersionUID = 5124154463252833532L;
    private static final String CLASS_NAME = 
            WebProtegeAuthenticationException.class.getSimpleName();
    
    public WebProtegeAuthenticationException() {
        super(CLASS_NAME);
    }

}
