package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;

/**
 * Ontology Message model for shared messaging system
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyMessage implements Serializable {

	private static final long serialVersionUID = 2424638485939464791L;
	private int ontologyId;
	private long webhookEventId;
	private String processedFilename;
	private boolean semanticallyValid = false;
	
	public OntologyMessage() {
		
	}

	public OntologyMessage(
			int ontologyId, 
			long webhookEventId, 
			String processedFilename) {
		super();
		this.ontologyId = ontologyId;
		this.webhookEventId = webhookEventId;
		this.processedFilename = processedFilename;
	}

	public int getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
	}

	public long getWebhookEventId() {
		return webhookEventId;
	}

	public void setWebhookEventId(long webhookEventId) {
		this.webhookEventId = webhookEventId;
	}

	public String getProcessedFilename() {
		return processedFilename;
	}
	
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
				+ (int) (webhookEventId ^ (webhookEventId >>> 32));
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
		if (webhookEventId != other.webhookEventId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OntologyMessage ["
				+ "ontologyId=" + ontologyId + ", "
				+ "webhookEventId=" + webhookEventId + ", "
				+ "processedFilename=" + processedFilename  + ", "
				+ "semanticallyValid=" + semanticallyValid
				+ "]";
	}
	
}
