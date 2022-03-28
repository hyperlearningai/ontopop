package ai.hyperlearning.ontopop.exceptions.ontology;

/**
 * Custom Exception - Ontology Data Pipeline Processing Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyDataPipelineProcessingException extends RuntimeException {

    private static final long serialVersionUID = -1251907528203807267L;
    
    public OntologyDataPipelineProcessingException(String message) {
        super(message);
    }

}
