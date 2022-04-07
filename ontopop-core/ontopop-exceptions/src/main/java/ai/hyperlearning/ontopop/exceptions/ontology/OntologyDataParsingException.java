package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Ontology Data Parsing Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataParsingException extends RuntimeException {

    private static final long serialVersionUID = 2883984532509572567L;
    
    public OntologyDataParsingException() {
        super("An error was encountered when attempting to parse the "
                + "RDF/XML OWL file.");
    }
    
    public OntologyDataParsingException(String message) {
        super(message);
    }

}
