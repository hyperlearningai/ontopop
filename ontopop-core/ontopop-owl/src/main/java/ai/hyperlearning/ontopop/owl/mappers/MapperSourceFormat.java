package ai.hyperlearning.ontopop.owl.mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Ontology data mapper supported source formats
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum MapperSourceFormat {

    N_QUADS("N-QUADS"), 
    N_TRIPLES("N-TRIPLES"), 
    OWL_XML("OWL-XML"), 
    RDF_XML("RDF-XML"), 
    TRIG("TRIG"), 
    TURTLE("TURTLE");
    
    private final String label;
    private static final Map<String, MapperSourceFormat> LABEL_MAP =
            new HashMap<>();

    static {
        for (MapperSourceFormat f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private MapperSourceFormat(final String label) {
        this.label = label;
    }

    public static MapperSourceFormat valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }
    
}
