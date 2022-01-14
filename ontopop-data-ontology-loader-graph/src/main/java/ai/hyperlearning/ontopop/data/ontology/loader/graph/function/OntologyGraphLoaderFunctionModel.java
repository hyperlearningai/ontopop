package ai.hyperlearning.ontopop.data.ontology.loader.graph.function;

import java.io.Serializable;

/**
 * Ontology Graph Loader Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyGraphLoaderFunctionModel implements Serializable {

    private static final long serialVersionUID = -5063740699426606255L;
    private String payload;
    
    public OntologyGraphLoaderFunctionModel() {
        
    }

    public OntologyGraphLoaderFunctionModel(String payload) {
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
        return "OntologyGraphLoaderFunctionModel ["
                + "payload=" + payload 
                + "]";
    }

}
