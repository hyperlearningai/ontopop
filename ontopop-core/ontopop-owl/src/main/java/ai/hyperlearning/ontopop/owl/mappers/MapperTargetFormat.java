package ai.hyperlearning.ontopop.owl.mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper supported target formats
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum MapperTargetFormat {
    
    GRAPHSON("GRAPHSON"),
    JSON_LD("JSON-LD"), 
    NATIVE("NATIVE"), 
    N_QUADS("N-QUADS"), 
    N_TRIPLES("N-TRIPLES"), 
    OWL_XML("OWL-XML"), 
    RDF_XML("RDF-XML"), 
    TRIG("TRIG"), 
    TURTLE("TURTLE"), 
    VIS("VIS");
    
    private final String label;
    private static final Map<String, MapperTargetFormat> LABEL_MAP =
            new HashMap<>();

    static {
        for (MapperTargetFormat f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private MapperTargetFormat(final String label) {
        this.label = label;
    }

    public static MapperTargetFormat valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
