package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Ontology Message model for shared messaging system
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMessage implements Serializable {

	private static final long serialVersionUID = 2424638485939464791L;
	private int ontologyId;
	private long gitWebhookId;
	private String processedFilename;
	private boolean semanticallyValid = false;
	
	public OntologyMessage() {
		
	}

	public OntologyMessage(
			int ontologyId, 
			long gitWebhookId, 
			String processedFilename) {
		super();
		this.ontologyId = ontologyId;
		this.gitWebhookId = gitWebhookId;
		this.processedFilename = processedFilename;
	}

	public int getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
	}

	public long getGitWebhookId() {
		return gitWebhookId;
	}

	public void setGitWebhookId(long gitWebhookId) {
		this.gitWebhookId = gitWebhookId;
	}

	public String getProcessedFilename() {
		return processedFilename;
	}
	
	@JsonIgnore
	public String getJsonProcessedFilename() {
		return processedFilename + ".json";
	}

	public void setProcessedFilename(String processedFilename) {
		this.processedFilename = processedFilename;
	}

	public boolean isSemanticallyValid() {
		return semanticallyValid;
	}

	public void setSemanticallyValid(boolean semanticallyValid) {
		this.semanticallyValid = semanticallyValid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ontologyId;
		result = prime * result 
				+ (int) (gitWebhookId ^ (gitWebhookId >>> 32));
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
		OntologyMessage other = (OntologyMessage) obj;
		if (ontologyId != other.ontologyId)
			return false;
		if (gitWebhookId != other.gitWebhookId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OntologyMessage ["
				+ "ontologyId=" + ontologyId + ", "
				+ "gitWebhookId=" + gitWebhookId + ", "
				+ "processedFilename=" + processedFilename  + ", "
				+ "semanticallyValid=" + semanticallyValid
				+ "]";
	}
	
}
