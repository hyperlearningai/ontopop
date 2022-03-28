package ai.hyperlearning.ontopop.exceptions.security;

/**
 * Invalid Client Name Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class InvalidClientNameException extends RuntimeException {

    private static final long serialVersionUID = -13718853019643429L;
    
    public InvalidClientNameException() {
        super("Missing or invalid client name.");
    }
    
    public InvalidClientNameException(String message) {
        super(message);
    }

}
