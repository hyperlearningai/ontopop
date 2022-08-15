package ai.hyperlearning.ontopop.exceptions.security;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Invalid Client Name Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidClientNameException extends OntoPopException {

    private static final long serialVersionUID = -13718853019643429L;
    private static final String CLASS_NAME = 
            InvalidClientNameException.class.getSimpleName();
    
    public InvalidClientNameException() {
        super(CLASS_NAME);
    }

}
