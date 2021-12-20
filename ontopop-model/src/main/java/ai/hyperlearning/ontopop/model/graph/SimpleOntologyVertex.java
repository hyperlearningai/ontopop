package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Simple Property Graph model representation of an OWL Class
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyVertex implements Serializable {

	private static final long serialVersionUID = -9077006932418612066L;
	protected static final String VERTEX_KEY_DELIMITER = "_";
	
	private static final String LABEL = "class";
	private String iri;
	private Integer ontologyId;
	private String key;
	private long latestWebhookEventId;
	private Map<String, Object> properties = new LinkedHashMap<>();
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateLastUpdated = LocalDateTime.now();
	
	public SimpleOntologyVertex() {
		
	}

	public SimpleOntologyVertex(
			String iri, 
			int ontologyId, 
			long latestWebhookEventId, 
			Map<String, Object> properties) {
		this.iri = iri;
		this.ontologyId = ontologyId;
		this.key = iri + VERTEX_KEY_DELIMITER + ontologyId;
		this.latestWebhookEventId = latestWebhookEventId;
		this.properties = properties;
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
		if ( this.ontologyId != null )
			this.key = iri + VERTEX_KEY_DELIMITER + this.ontologyId;
	}

	public int getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
		if ( this.iri != null )
			this.key = this.iri + VERTEX_KEY_DELIMITER + ontologyId;
	}
	
	public String getKey() {
		return key;
	}

	public long getLatestWebhookEventId() {
		return latestWebhookEventId;
	}

	public void setLatestWebhookEventId(long latestWebhookEventId) {
		this.latestWebhookEventId = latestWebhookEventId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public LocalDateTime getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	public static String getLabel() {
		return LABEL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
		result = prime * result + ontologyId;
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
		SimpleOntologyVertex other = (SimpleOntologyVertex) obj;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		if (ontologyId != other.ontologyId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleOntologyVertex ["
				+ "label=" + LABEL + ", "
				+ "iri=" + iri + ", "
				+ "ontologyId=" + ontologyId + ", "
				+ "key=" + key + ", "
				+ "latestWebhookEventId=" + latestWebhookEventId + ", "
				+ "properties=" + properties + ", "
				+ "dateLastUpdated=" + dateLastUpdated 
				+ "]";
	}

}
