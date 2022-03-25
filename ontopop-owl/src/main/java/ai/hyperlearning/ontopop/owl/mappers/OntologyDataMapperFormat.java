package ai.hyperlearning.ontopop.owl.mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Ontology Mapper Data Formats
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum OntologyDataMapperFormat {
    
    RDF_XML("RDF-XML"),
    JSON("JSON");
    
    private final String label;
    private static final Map<String, OntologyDataMapperFormat> LABEL_MAP =
            new HashMap<>();

    static {
        for (OntologyDataMapperFormat f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private OntologyDataMapperFormat(final String label) {
        this.label = label;
    }

    public static OntologyDataMapperFormat valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
