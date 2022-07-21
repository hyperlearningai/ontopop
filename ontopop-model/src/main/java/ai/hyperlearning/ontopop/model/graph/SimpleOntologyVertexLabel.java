package ai.hyperlearning.ontopop.model.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * OWL Vertex Label Types
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum SimpleOntologyVertexLabel {
    
    CLASS("Class"), 
    NAMED_INDIVIDUAL("Named Individual");
    
    private final String label;
    private static final Map<String, SimpleOntologyVertexLabel> LABEL_MAP =
            new HashMap<>();

    static {
        for (SimpleOntologyVertexLabel v : values()) {
            LABEL_MAP.put(v.label, v);
        }
    }

    private SimpleOntologyVertexLabel(final String label) {
        this.label = label;
    }

    public static SimpleOntologyVertexLabel valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
