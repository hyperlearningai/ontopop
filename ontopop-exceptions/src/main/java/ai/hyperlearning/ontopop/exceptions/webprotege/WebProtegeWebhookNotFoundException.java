package ai.hyperlearning.ontopop.exceptions.webprotege;

/**
 * WebProtege Webhook Event Model - Not Found Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeWebhookNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2840255248486905938L;
    
    public WebProtegeWebhookNotFoundException(long id) {
        super("WebProtege Webhook with ID " + id + " not found.");
    }

}
