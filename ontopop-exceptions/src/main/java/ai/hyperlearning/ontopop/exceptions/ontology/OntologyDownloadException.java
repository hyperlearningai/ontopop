package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Download Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */


public class OntologyDownloadException extends RuntimeException {

    private static final long serialVersionUID = -1446897825165441187L;
    
    public OntologyDownloadException() {
        super("An error was encountered when attempting to "
                    + "retrieve the OWL file for an ontology.");
    }
    
    public OntologyDownloadException(String message) {
        super(message);
    }

}
