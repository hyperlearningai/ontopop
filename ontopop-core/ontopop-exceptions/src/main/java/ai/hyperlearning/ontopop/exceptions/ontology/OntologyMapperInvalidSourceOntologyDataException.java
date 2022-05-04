package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Modelling Invalid Ontology Data Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMapperInvalidSourceOntologyDataException extends RuntimeException {

    private static final long serialVersionUID = -2705361597503590370L;
    
    public OntologyMapperInvalidSourceOntologyDataException() {
        super("Invalid ontology data file provided.");
    }
    
    public OntologyMapperInvalidSourceOntologyDataException(String message) {
        super(message);
    }

}
