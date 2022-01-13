package ai.hyperlearning.ontopop.data.ontology.validator.function;

import java.io.Serializable;

/**
 * Ontology Validator Cloud Function - Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyValidatorFunctionModel implements Serializable {

    private static final long serialVersionUID = 2192738870709009478L;
    private String payload;
    
    public OntologyValidatorFunctionModel() {
        
    }

    public OntologyValidatorFunctionModel(String payload) {
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
        return "OntologyValidatorFunctionModel ["
                + "payload=" + payload 
                + "]";
    }
    
}
