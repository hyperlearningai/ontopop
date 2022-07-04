package ai.hyperlearning.ontopop.security.auth.api.apikey;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported API Key Authentication Engines
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum ApiKeyAuthenticationEngine {
    
    SECRETS("SECRETS");

    private final String label;
    private static final Map<String, ApiKeyAuthenticationEngine> LABEL_MAP =
            new HashMap<>();

    static {
        for (ApiKeyAuthenticationEngine e : values()) {
            LABEL_MAP.put(e.label, e);
        }
    }

    private ApiKeyAuthenticationEngine(final String label) {
        this.label = label;
    }

    public static ApiKeyAuthenticationEngine valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
