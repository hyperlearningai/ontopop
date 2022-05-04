package ai.hyperlearning.ontopop.owl.mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported mapping target formats
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum RdfXmlMapperFormat {
    
    GRAPHSON("GRAPHSON"),
    NATIVE("NATIVE");
    
    private final String label;
    private static final Map<String, RdfXmlMapperFormat> LABEL_MAP =
            new HashMap<>();

    static {
        for (RdfXmlMapperFormat f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private RdfXmlMapperFormat(final String label) {
        this.label = label;
    }

    public static RdfXmlMapperFormat valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
