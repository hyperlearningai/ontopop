package ai.hyperlearning.ontopop.graph.gremlin.server.http;

import java.io.Serializable;

/**
 * Gremlin Server HTTP Web Client Request Body Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GremlinServerHttpWebClientRequestBodyModel implements Serializable {
    
    private static final long serialVersionUID = -4415636234863408178L;
    
    private String gremlin;
    
    public GremlinServerHttpWebClientRequestBodyModel() {
        
    }

    public GremlinServerHttpWebClientRequestBodyModel(String gremlin) {
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
        return "GremlinServerHttpWebClientRequestBodyModel ["
                + "gremlin=" + gremlin
                + "]";
    }

}
