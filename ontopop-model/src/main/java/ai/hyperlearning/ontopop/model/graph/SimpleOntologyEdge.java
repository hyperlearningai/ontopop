package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Simple Property Graph model representation of an OWL Class relationship
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyEdge implements Serializable {

	private static final long serialVersionUID = 3796299067782280539L;
	public static final String LABEL = "subClassOf";
	public static final String RELATIONSHIP_TYPE_KEY = "relationship";
	private String sourceVertexKey;
	private long sourceVertexId;
	private String targetVertexKey;
	private long targetVertexId;
	private int ontologyId;
	private long latestGitWebhookId;
	private Map<String, Object> properties = new LinkedHashMap<>();
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateLastUpdated = LocalDateTime.now();
	
	public SimpleOntologyEdge() {
		
	}

	public SimpleOntologyEdge(
			String sourceVertexKey, 
			long sourceVertexId, 
			String targetVertexKey, 
			long targetVertexId, 
			int ontologyId,
			long latestGitWebhookId, 
			Map<String, Object> properties) {
		this.sourceVertexKey = sourceVertexKey;
		this.sourceVertexId = sourceVertexId;
		this.targetVertexKey = targetVertexKey;
		this.targetVertexId = targetVertexId;
		this.ontologyId = ontologyId;
		this.latestGitWebhookId = latestGitWebhookId;
		this.properties = properties;
	}

	public String getSourceVertexKey() {
		return sourceVertexKey;
	}

	public void setSourceVertexKey(String sourceVertexKey) {
		this.sourceVertexKey = sourceVertexKey;
	}

	public long getSourceVertexId() {
		return sourceVertexId;
	}

	public void setSourceVertexId(long sourceVertexId) {
		this.sourceVertexId = sourceVertexId;
	}

	public String getTargetVertexKey() {
		return targetVertexKey;
	}

	public void setTargetVertexKey(String targetVertexKey) {
		this.targetVertexKey = targetVertexKey;
	}

	public long getTargetVertexId() {
		return targetVertexId;
	}

	public void setTargetVertexId(long targetVertexId) {
		this.targetVertexId = targetVertexId;
	}

	public int getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(int ontologyId) {
		this.ontologyId = ontologyId;
	}

	public long getLatestGitWebhookId() {
		return latestGitWebhookId;
	}

	public void setLatestGitWebhookId(long latestGitWebhookId) {
		this.latestGitWebhookId = latestGitWebhookId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public void preparePropertiesForLoading() {
		this.properties.put("sourceVertexKey", this.sourceVertexKey);
		this.properties.put("sourceVertexId", this.sourceVertexId);
		this.properties.put("targetVertexKey", this.targetVertexKey);
		this.properties.put("targetVertexId", this.targetVertexId);
		this.properties.put("ontologyId", this.ontologyId);
		this.properties.put("latestGitWebhookId", this.latestGitWebhookId);
	}

	public LocalDateTime getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	@Override
	public String toString() {
		return "SimpleOntologyEdge ["
				+ "label=" + LABEL + ", "
				+ "sourceVertexKey=" + sourceVertexKey + ", "
				+ "sourceVertexId=" + sourceVertexId + ", "
				+ "targetVertexKey=" + targetVertexKey + ", "
				+ "targetVertexId=" + targetVertexId + ", "
				+ "ontologyId=" + ontologyId + ", "
				+ "latestGitWebhookId=" + latestGitWebhookId + ", "
				+ "properties=" + properties + ", "
				+ "dateLastUpdated=" + dateLastUpdated 
				+ "]";
	}

}
