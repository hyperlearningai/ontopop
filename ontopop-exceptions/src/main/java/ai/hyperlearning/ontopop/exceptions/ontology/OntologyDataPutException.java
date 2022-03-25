package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Data PUT Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataPutException extends RuntimeException {

    private static final long serialVersionUID = -7242776517129809581L;
    
    public OntologyDataPutException() {
        super("An error was encountered when attempting to create or overwrite "
                    + "the Git-based data file for an ontology.");
    }
    
    public OntologyDataPutException(String message) {
        super(message);
    }

}
