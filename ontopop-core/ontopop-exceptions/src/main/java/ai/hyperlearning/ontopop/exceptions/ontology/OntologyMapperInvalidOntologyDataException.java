package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Modelling Invalid Ontology Data Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMapperInvalidOntologyDataException extends RuntimeException {

    private static final long serialVersionUID = -2705361597503590370L;
    
    public OntologyMapperInvalidOntologyDataException() {
        super("Invalid OWL file provided.");
    }
    
    public OntologyMapperInvalidOntologyDataException(String message) {
        super(message);
    }

}
