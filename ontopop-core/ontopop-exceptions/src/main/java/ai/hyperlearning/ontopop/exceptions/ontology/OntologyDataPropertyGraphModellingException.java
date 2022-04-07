package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Data Property Graph Modelling Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataPropertyGraphModellingException extends RuntimeException {

    private static final long serialVersionUID = -1177993727845804863L;
    
    public OntologyDataPropertyGraphModellingException() {
        super("An error was encountered when attempting to model the "
                + "parsed RDF/XML OWL file as a simple property graph object.");
    }
    
    public OntologyDataPropertyGraphModellingException(String message) {
        super(message);
    }

}
