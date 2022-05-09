package ai.hyperlearning.ontopop.exceptions.webprotege;

/**
 * WebProtege Project Access Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeProjectAccessException extends RuntimeException {

    private static final long serialVersionUID = -8904849874968126154L;
    
    public WebProtegeProjectAccessException() {
        super("Access to the specified WebProtege project ID is unauthorized. "
                + "Please share the WebProtege project with "
                + "service@ontopop.com and try again.");
    }
    
    public WebProtegeProjectAccessException(String message) {
        super(message);
    }

}
