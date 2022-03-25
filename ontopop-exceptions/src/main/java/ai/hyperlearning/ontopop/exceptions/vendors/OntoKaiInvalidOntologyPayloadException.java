package ai.hyperlearning.ontopop.exceptions.vendors;

/**
 * Invalid OntoKai Ontology Payload Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiInvalidOntologyPayloadException extends RuntimeException {

    private static final long serialVersionUID = 7837043488748154108L;
    
    public OntoKaiInvalidOntologyPayloadException() {
        super("Missing or invalid OntoKai ontology payload.");
    }
    
    public OntoKaiInvalidOntologyPayloadException(String message) {
        super(message);
    }

}
