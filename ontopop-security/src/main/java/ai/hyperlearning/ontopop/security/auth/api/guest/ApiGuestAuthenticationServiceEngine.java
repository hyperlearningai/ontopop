package ai.hyperlearning.ontopop.security.auth.api.guest;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported API Guest Authentication Engines
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum ApiGuestAuthenticationServiceEngine {
    
    SECRETS("SECRETS");

    private final String label;
    private static final Map<String, ApiGuestAuthenticationServiceEngine> LABEL_MAP =
            new HashMap<>();

    static {
        for (ApiGuestAuthenticationServiceEngine s : values()) {
            LABEL_MAP.put(s.label, s);
        }
    }

    private ApiGuestAuthenticationServiceEngine(final String label) {
        this.label = label;
    }

    public static ApiGuestAuthenticationServiceEngine valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
