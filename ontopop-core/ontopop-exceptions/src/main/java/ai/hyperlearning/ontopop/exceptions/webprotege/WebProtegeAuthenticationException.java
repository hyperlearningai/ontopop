package ai.hyperlearning.ontopop.exceptions.webprotege;

/**
 * WebProtege Authentication Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeAuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 5124154463252833532L;
    
    public WebProtegeAuthenticationException() {
        super("Could not authenticate with WebProtege.");
    }
    
    public WebProtegeAuthenticationException(String message) {
        super(message);
    }

}
