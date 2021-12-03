package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;

/**
 * Ontology Model - Secret Data
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologySecretData implements Serializable {

	private static final long serialVersionUID = -3107174777469504852L;
	private String repoToken;
	private String repoWebhookSecret;
	
	public OntologySecretData() {
		
	}

	public String getRepoToken() {
		return repoToken;
	}

	public void setRepoToken(String repoToken) {
		this.repoToken = repoToken;
	}

	public String getRepoWebhookSecret() {
		return repoWebhookSecret;
	}

	public void setRepoWebhookSecret(String repoWebhookSecret) {
		this.repoWebhookSecret = repoWebhookSecret;
	}
	
}
