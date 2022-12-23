package ai.hyperlearning.ontopop.model.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * OWL Edge Label Types
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum SimpleOntologyEdgeLabel {
    
    SUB_CLASS_OF("Subclass Of"), 
    INSTANCE_OF("Instance Of"), 
    LINKED_NAMED_INDIVIDUAL("Linked Named Individual");
    
    private final String label;
    private static final Map<String, SimpleOntologyEdgeLabel> LABEL_MAP =
            new HashMap<>();

    static {
        for (SimpleOntologyEdgeLabel e : values()) {
            LABEL_MAP.put(e.label, e);
        }
    }

    private SimpleOntologyEdgeLabel(final String label) {
        this.label = label;
    }

    public static SimpleOntologyEdgeLabel valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
