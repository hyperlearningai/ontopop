package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Simple Property Graph model representation of an OWL Class relationship
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyEdge implements Serializable {

	private static final long serialVersionUID = 3796299067782280539L;
	private static final String LABEL = "subClassOf";
	protected static final String RELATIONSHIP_TYPE_KEY = "relationshipType";
	private SimpleOntologyVertex sourceVertex;
	private SimpleOntologyVertex targetVertex;
	private int ontologyId;
	private long latestWebhookEventId;
	private Map<String, Object> properties = new LinkedHashMap<>();;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateLastUpdated = LocalDateTime.now();
	
	public SimpleOntologyEdge() {
		
	}

	public SimpleOntologyEdge(
			SimpleOntologyVertex sourceVertex, 
			SimpleOntologyVertex targetVertex, 
			int ontologyId,
			long latestWebhookEventId, 
			Map<String, Object> properties) {
		this.sourceVertex = sourceVertex;
		this.targetVertex = targetVertex;
		this.ontologyId = ontologyId;
		this.latestWebhookEventId = latestWebhookEventId;
		this.properties = properties;
	}

	public SimpleOntologyVertex getSourceVertex() {
		return sourceVertex;
	}

	public void setSourceVertex(SimpleOntologyVertex sourceVertex) {
		this.sourceVertex = sourceVertex;
	}

	public SimpleOntologyVertex getTargetVertex() {
		return targetVertex;
	}

	public void setTargetVertex(SimpleOntologyVertex targetVertex) {
		this.targetVertex = targetVertex;
	}

	public int getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
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
		result = prime * result + ontologyId;
		result = prime * result + ((sourceVertex == null) ? 0 : sourceVertex.hashCode());
		result = prime * result + ((targetVertex == null) ? 0 : targetVertex.hashCode());
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
		SimpleOntologyEdge other = (SimpleOntologyEdge) obj;
		if (ontologyId != other.ontologyId)
			return false;
		if (sourceVertex == null) {
			if (other.sourceVertex != null)
				return false;
		} else if (!sourceVertex.equals(other.sourceVertex))
			return false;
		if (targetVertex == null) {
			if (other.targetVertex != null)
				return false;
		} else if (!targetVertex.equals(other.targetVertex))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleOntologyEdge ["
				+ "label=" + LABEL + ", "
				+ "sourceVertexKey=" + sourceVertex.getKey() + ", "
				+ "targetVertexKey=" + targetVertex.getKey() + ", "
				+ "ontologyId=" + ontologyId + ", "
				+ "latestWebhookEventId=" + latestWebhookEventId + ", "
				+ "properties=" + properties + ", "
				+ "dateLastUpdated=" + dateLastUpdated 
				+ "]";
	}

}
