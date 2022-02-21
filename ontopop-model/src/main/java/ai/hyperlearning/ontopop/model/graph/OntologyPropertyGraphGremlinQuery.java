package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Property Graph Gremlin Query Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyPropertyGraphGremlinQuery implements Serializable {

    private static final long serialVersionUID = 8147496203552859038L;
    
    @Schema(description = "Gremlin query", 
            example = "g.V().has('iri', 'http://webprotege.stanford.edu/RxMK8BflSk74kqDWT4eHTy').valueMap()", 
            required = true)
    private String gremlin;
    
    public OntologyPropertyGraphGremlinQuery() {
        
    }

    public OntologyPropertyGraphGremlinQuery(String gremlin) {
        super();
        this.gremlin = gremlin;
    }

    public String getGremlin() {
        return gremlin;
    }

    public void setGremlin(String gremlin) {
        this.gremlin = gremlin;
    }

    @Override
    public String toString() {
        return "OntologyPropertyGraphGremlinQuery ["
                + "gremlin=" + gremlin 
                + "]";
    }

}
