package ai.hyperlearning.ontopop.security.auth.api.apikey;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported API Key Authentication Engines
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum ApiKeyAuthenticationServiceEngine {
    
    SECRETS("SECRETS");

    private final String label;
    private static final Map<String, ApiKeyAuthenticationServiceEngine> LABEL_MAP =
            new HashMap<>();

    static {
        for (ApiKeyAuthenticationServiceEngine s : values()) {
            LABEL_MAP.put(s.label, s);
        }
    }

    private ApiKeyAuthenticationServiceEngine(final String label) {
        this.label = label;
    }

    public static ApiKeyAuthenticationServiceEngine valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
