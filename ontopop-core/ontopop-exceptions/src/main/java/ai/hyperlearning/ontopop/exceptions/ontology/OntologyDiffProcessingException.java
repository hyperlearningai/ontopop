package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Custom Exception - Ontology Diff Processing Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDiffProcessingException extends RuntimeException {

    private static final long serialVersionUID = -5191943095095430857L;
    
    public OntologyDiffProcessingException(String message) {
        super(message);
    }

}
