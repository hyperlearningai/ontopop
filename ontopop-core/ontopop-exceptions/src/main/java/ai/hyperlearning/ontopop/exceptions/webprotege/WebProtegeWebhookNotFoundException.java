package ai.hyperlearning.ontopop.exceptions.webprotege;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * WebProtege Webhook Event Model - Not Found Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeWebhookNotFoundException extends OntoPopException {

    private static final long serialVersionUID = 2840255248486905938L;
    private static final String CLASS_NAME = 
            WebProtegeWebhookNotFoundException.class.getSimpleName();
    
    public WebProtegeWebhookNotFoundException() {
        super(CLASS_NAME);
    }

}
