package ai.hyperlearning.ontopop.data.ontology.modeller.graph.function;

import java.io.Serializable;

/**
 * Ontology Graph Modeller Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */


public class OntologyGraphModellerFunctionModel implements Serializable {

    private static final long serialVersionUID = 9004319793793335735L;
    private String payload;
    
    public OntologyGraphModellerFunctionModel() {
        
    }

    public OntologyGraphModellerFunctionModel(String payload) {
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
        return "OntologyGraphModellerFunctionModel ["
                + "payload=" + payload 
                + "]";
    }

}
