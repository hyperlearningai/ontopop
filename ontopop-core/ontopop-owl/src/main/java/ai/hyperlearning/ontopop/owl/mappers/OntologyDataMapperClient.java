package ai.hyperlearning.ontopop.owl.mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Ontology Mapper Data Clients
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum OntologyDataMapperClient {
    
    ONTOKAI("ONTOKAI");
    
    private final String label;
    private static final Map<String, OntologyDataMapperClient> LABEL_MAP =
            new HashMap<>();

    static {
        for (OntologyDataMapperClient c : values()) {
            LABEL_MAP.put(c.label, c);
        }
    }

    private OntologyDataMapperClient(final String label) {
        this.label = label;
    }

    public static OntologyDataMapperClient valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
