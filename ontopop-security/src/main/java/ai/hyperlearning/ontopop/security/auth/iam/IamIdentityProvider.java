package ai.hyperlearning.ontopop.security.auth.iam;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported IAM Identity Providers
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum IamIdentityProvider {
    
    AWS_COGNITO("AWS-COGNITO");

    private final String label;
    private static final Map<String, IamIdentityProvider> LABEL_MAP =
            new HashMap<>();

    static {
        for (IamIdentityProvider i : values()) {
            LABEL_MAP.put(i.label, i);
        }
    }

    private IamIdentityProvider(final String label) {
        this.label = label;
    }

    public static IamIdentityProvider valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
