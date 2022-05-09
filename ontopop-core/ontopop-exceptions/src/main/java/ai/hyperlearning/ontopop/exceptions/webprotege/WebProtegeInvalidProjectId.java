package ai.hyperlearning.ontopop.exceptions.webprotege;

/**
 * WebProtege Invalid Project ID Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeInvalidProjectId extends RuntimeException {
    
    private static final long serialVersionUID = -8737889790985389924L;

    public WebProtegeInvalidProjectId() {
        super("Invalid WebProtege project ID.");
    }
    
    public WebProtegeInvalidProjectId(String message) {
        super(message);
    }

}
