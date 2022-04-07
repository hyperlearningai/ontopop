package ai.hyperlearning.ontopop.owl.modellers;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported modelling formats
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum RdfXmlModellerFormat {
    
    GRAPHSON("GRAPHSON"),
    NATIVE("NATIVE");
    
    private final String label;
    private static final Map<String, RdfXmlModellerFormat> LABEL_MAP =
            new HashMap<>();

    static {
        for (RdfXmlModellerFormat f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private RdfXmlModellerFormat(final String label) {
        this.label = label;
    }

    public static RdfXmlModellerFormat valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
