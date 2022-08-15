package ai.hyperlearning.ontopop.exceptions.webprotege;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * WebProtege Project Access Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeProjectAccessException extends OntoPopException {

    private static final long serialVersionUID = -8904849874968126154L;
    private static final String CLASS_NAME = 
            WebProtegeProjectAccessException.class.getSimpleName();
    
    public WebProtegeProjectAccessException() {
        super(CLASS_NAME);
    }

}
