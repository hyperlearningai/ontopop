package ai.hyperlearning.ontopop.security.secrets;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Secrets Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum SecretsServiceType {
	
	HASHICORP_VAULT("HASHICORP-VAULT"), 
	AWS_SECRETS_MANAGER("AWS-SECRETS-MANAGER"), 
	AZURE_KEY_VAULT("AZURE-KEY-VAULT");
	
	private final String label;
	private static final Map<String, SecretsServiceType> LABEL_MAP = 
			new HashMap<>();
	
	static {
        for (SecretsServiceType s: values()) {
        		LABEL_MAP.put(s.label, s);
        }
    }
	
	private SecretsServiceType(final String label) {
		this.label = label;
	}
	
	public static SecretsServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }
	
	@Override
	public String toString() {
		return label;
	}

}
