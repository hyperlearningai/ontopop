package ai.hyperlearning.ontopop.security.auth.api.guest;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported API Guest Authentication Modes
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum ApiGuestAuthenticationServiceMode {
    
    SESSION("SESSION");

    private final String label;
    private static final Map<String, ApiGuestAuthenticationServiceMode> LABEL_MAP =
            new HashMap<>();

    static {
        for (ApiGuestAuthenticationServiceMode m : values()) {
            LABEL_MAP.put(m.label, m);
        }
    }

    private ApiGuestAuthenticationServiceMode(final String label) {
        this.label = label;
    }

    public static ApiGuestAuthenticationServiceMode valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
