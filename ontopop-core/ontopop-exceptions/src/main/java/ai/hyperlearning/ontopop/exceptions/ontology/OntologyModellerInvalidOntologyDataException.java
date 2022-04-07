package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Modelling Invalid Ontology Data Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyModellerInvalidOntologyDataException extends RuntimeException {

    private static final long serialVersionUID = -2705361597503590370L;
    
    public OntologyModellerInvalidOntologyDataException() {
        super("Invalid OWL file provided.");
    }
    
    public OntologyModellerInvalidOntologyDataException(String message) {
        super(message);
    }

}
