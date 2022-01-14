package ai.hyperlearning.ontopop.data.ontology.loader.triplestore.function;

import java.io.Serializable;

/**
 * Ontology Triplestore Function Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyTriplestoreFunctionModel implements Serializable {

    private static final long serialVersionUID = 4586489147365113174L;
    private String payload;
    
    public OntologyTriplestoreFunctionModel() {
        
    }

    public OntologyTriplestoreFunctionModel(String payload) {
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
        return "OntologyTriplestoreFunctionModel ["
                + "payload=" + payload 
                + "]";
    }

}
