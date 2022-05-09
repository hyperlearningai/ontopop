package ai.hyperlearning.ontopop.exceptions.webprotege;

/**
 * WebProtege Missing Credentials Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeMissingCredentials extends RuntimeException {
    
    private static final long serialVersionUID = -8230218188485539299L;

    public WebProtegeMissingCredentials() {
        super("WebProtege credentials have not been set as "
                + "environmental variables.");
    }
    
    public WebProtegeMissingCredentials(String message) {
        super(message);
    }

}
