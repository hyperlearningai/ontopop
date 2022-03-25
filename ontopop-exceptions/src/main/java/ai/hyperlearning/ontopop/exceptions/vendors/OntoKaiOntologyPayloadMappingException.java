package ai.hyperlearning.ontopop.exceptions.vendors;

/**
 * OntoKai Ontology Payload Mapping Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyPayloadMappingException extends RuntimeException {

    private static final long serialVersionUID = -4661079657140803812L;
    
    public OntoKaiOntologyPayloadMappingException() {
        super("An error was encountered when attempting to mapp the OntoKai "
                + "ontology payload.");
    }
    
    public OntoKaiOntologyPayloadMappingException(String message) {
        super(message);
    }

}
