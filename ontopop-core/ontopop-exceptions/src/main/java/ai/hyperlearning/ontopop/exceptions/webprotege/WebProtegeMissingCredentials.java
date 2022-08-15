package ai.hyperlearning.ontopop.exceptions.webprotege;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * WebProtege Missing Credentials Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeMissingCredentials extends OntoPopException {
    
    private static final long serialVersionUID = -8230218188485539299L;
    private static final String CLASS_NAME = 
            WebProtegeMissingCredentials.class.getSimpleName();

    public WebProtegeMissingCredentials() {
        super(CLASS_NAME);
    }

}
