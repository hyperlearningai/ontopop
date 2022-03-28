package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Custom Exception - Ontology Diff Invalid Timestamp Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDiffInvalidTimestampException extends RuntimeException {

    private static final long serialVersionUID = 5824918589754948403L;
    
    public OntologyDiffInvalidTimestampException(String message) {
        super(message);
    }

}
