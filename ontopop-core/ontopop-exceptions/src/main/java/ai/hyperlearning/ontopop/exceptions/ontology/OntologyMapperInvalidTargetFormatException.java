package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Modelling Invalid Target Format Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMapperInvalidTargetFormatException extends RuntimeException {

    private static final long serialVersionUID = -2192403054284782094L;
    
    public OntologyMapperInvalidTargetFormatException() {
        super("Invalid target format provided.");
    }
    
    public OntologyMapperInvalidTargetFormatException(String message) {
        super(message);
    }

}
