package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Custom Exception - Ontology Diff Invalid Temporal Diff Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDiffInvalidRequestParametersException extends RuntimeException {

    private static final long serialVersionUID = -7643909324750869755L;
    
    public OntologyDiffInvalidRequestParametersException(String message) {
        super(message);
    }

}
