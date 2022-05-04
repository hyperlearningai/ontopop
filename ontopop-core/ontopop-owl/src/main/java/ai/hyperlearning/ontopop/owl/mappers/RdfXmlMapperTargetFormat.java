package ai.hyperlearning.ontopop.owl.mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * RDF/XML mapper supported target formats
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum RdfXmlMapperTargetFormat {
    
    GRAPHSON("GRAPHSON"),
    NATIVE("NATIVE");
    
    private final String label;
    private static final Map<String, RdfXmlMapperTargetFormat> LABEL_MAP =
            new HashMap<>();

    static {
        for (RdfXmlMapperTargetFormat f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private RdfXmlMapperTargetFormat(final String label) {
        this.label = label;
    }

    public static RdfXmlMapperTargetFormat valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
