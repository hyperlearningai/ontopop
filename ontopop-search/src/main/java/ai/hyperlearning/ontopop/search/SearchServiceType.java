package ai.hyperlearning.ontopop.search;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Search Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum SearchServiceType {

    ELASTICSEARCH("ELASTICSEARCH"), 
    AZURE_SEARCH("AZURE-SEARCH");

    private final String label;
    private static final Map<String, SearchServiceType> LABEL_MAP =
            new HashMap<>();

    static {
        for (SearchServiceType s : values()) {
            LABEL_MAP.put(s.label, s);
        }
    }

    private SearchServiceType(final String label) {
        this.label = label;
    }

    public static SearchServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
