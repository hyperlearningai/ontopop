package ai.hyperlearning.ontopop.data.ontology.indexer.function;

import java.io.Serializable;

/**
 * Ontology Graph Indexer Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyGraphIndexerFunctionModel implements Serializable {

    private static final long serialVersionUID = 325620876444868802L;
    private String payload;
    
    public OntologyGraphIndexerFunctionModel() {
        
    }

    public OntologyGraphIndexerFunctionModel(String payload) {
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
        return "OntologyGraphIndexerFunctionModel ["
                + "payload=" + payload 
                + "]";
    }

}
