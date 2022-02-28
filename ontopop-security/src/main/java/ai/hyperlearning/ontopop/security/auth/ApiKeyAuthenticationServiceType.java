package ai.hyperlearning.ontopop.security.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported API Key Authentication Engines
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum ApiKeyAuthenticationServiceType {
    
    SECRETS("SECRETS");

    private final String label;
    private static final Map<String, ApiKeyAuthenticationServiceType> LABEL_MAP =
            new HashMap<>();

    static {
        for (ApiKeyAuthenticationServiceType s : values()) {
            LABEL_MAP.put(s.label, s);
        }
    }

    private ApiKeyAuthenticationServiceType(final String label) {
        this.label = label;
    }

    public static ApiKeyAuthenticationServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
