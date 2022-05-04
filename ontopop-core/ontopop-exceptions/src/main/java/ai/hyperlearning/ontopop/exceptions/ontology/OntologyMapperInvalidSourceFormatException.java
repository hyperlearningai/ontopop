package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Modelling Invalid Source Format Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMapperInvalidSourceFormatException extends RuntimeException {

    private static final long serialVersionUID = 494276030754015948L;
    
    public OntologyMapperInvalidSourceFormatException() {
        super("Invalid source format provided.");
    }
    
    public OntologyMapperInvalidSourceFormatException(String message) {
        super(message);
    }

}
