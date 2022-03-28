package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Data Invalid Format Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataInvalidFormatException extends RuntimeException {

    private static final long serialVersionUID = -7712862841481291014L;
    
    public OntologyDataInvalidFormatException() {
        super("Invalid ontology data format provided.");
    }
    
    public OntologyDataInvalidFormatException(String message) {
        super(message);
    }

}
