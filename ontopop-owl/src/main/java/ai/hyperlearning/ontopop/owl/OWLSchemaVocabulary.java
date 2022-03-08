package ai.hyperlearning.ontopop.owl;

import java.util.HashMap;
import java.util.Map;

/**
 * OWL 2 Schema Vocabulary
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum OWLSchemaVocabulary {
    
    ANNOTATION_PROPERTY("AnnotationProperty"), 
    OBJECT_PROPERTY("ObjectProperty"), 
    CLASS("Class");
    
    private final String label;
    private static final Map<String, OWLSchemaVocabulary> LABEL_MAP =
            new HashMap<>();

    static {
        for (OWLSchemaVocabulary s : values()) {
            LABEL_MAP.put(s.label, s);
        }
    }

    private OWLSchemaVocabulary(final String label) {
        this.label = label;
    }

    public static OWLSchemaVocabulary valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
    
}
