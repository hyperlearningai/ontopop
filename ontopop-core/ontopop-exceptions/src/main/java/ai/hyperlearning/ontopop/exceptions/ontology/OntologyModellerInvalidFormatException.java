package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Modelling Invalid Format Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyModellerInvalidFormatException extends RuntimeException {

    private static final long serialVersionUID = -2192403054284782094L;
    
    public OntologyModellerInvalidFormatException() {
        super("Invalid target format provided.");
    }
    
    public OntologyModellerInvalidFormatException(String message) {
        super(message);
    }

}
