package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;

/**
 * Ontology Model - Repository Webhook Secret
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyRepositoryWebhookSecret implements Serializable {

	private static final long serialVersionUID = 8630018619884583056L;
	private int id;
	private String repoWebhookSecret;
	
	public OntologyRepositoryWebhookSecret() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRepoWebhookSecret() {
		return repoWebhookSecret;
	}

	public void setRepoWebhookSecret(String repoWebhookSecret) {
		this.repoWebhookSecret = repoWebhookSecret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OntologyRepositoryWebhookSecret other = (OntologyRepositoryWebhookSecret) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
