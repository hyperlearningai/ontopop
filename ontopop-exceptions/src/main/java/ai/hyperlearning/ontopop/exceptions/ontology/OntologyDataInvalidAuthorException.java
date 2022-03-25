package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Data PUT Author Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataInvalidAuthorException extends RuntimeException {

    private static final long serialVersionUID = 2237417330087616965L;
    
    public OntologyDataInvalidAuthorException() {
        super("Missing or invalid author name.");
    }
    
    public OntologyDataInvalidAuthorException(String message) {
        super(message);
    }

}
