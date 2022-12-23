package ai.hyperlearning.ontopop.model.graph.formats;

import java.io.Serializable;

/**
 * Property Graph Format Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class PropertyGraph implements Serializable {

    private static final long serialVersionUID = -8626022964303478063L;
    
    private Object graph;
    
    public PropertyGraph() {
        
    }

    public PropertyGraph(Object graph) {
        super();
        this.graph = graph;
    }

    public Object getGraph() {
        return graph;
    }

    public void setGraph(Object graph) {
        this.graph = graph;
    }

    @Override
    public String toString() {
        return "PropertyGraphFormat ["
                + "graph=" + graph + 
                "]";
    }

}
