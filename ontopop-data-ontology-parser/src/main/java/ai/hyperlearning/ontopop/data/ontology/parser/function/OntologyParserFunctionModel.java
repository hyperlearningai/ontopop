package ai.hyperlearning.ontopop.data.ontology.parser.function;

import java.io.Serializable;

/**
 * Ontology Parser Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyParserFunctionModel implements Serializable {

    private static final long serialVersionUID = -4064820438065491037L;
    private String payload;
    
    public OntologyParserFunctionModel() {
        
    }

    public OntologyParserFunctionModel(String payload) {
        super();
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "OntologyParserFunctionModel ["
                + "payload=" + payload 
                + "]";
    }

}
