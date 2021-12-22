package ai.hyperlearning.ontopop.graph.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Simple Vertex model for bulk loading
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleBulkVertex implements Serializable {

	private static final long serialVersionUID = 9091136164674497432L;
	private long vertexId;
	private String label;
	private Map<String, Object> properties;
	
	public SimpleBulkVertex() {
		
	}

	public SimpleBulkVertex(long vertexId, 
			String label, 
			Map<String, Object> properties) {
		this.vertexId = vertexId;
		this.label = label;
		this.properties = properties;
	}

	public long getVertexId() {
		return vertexId;
	}

	public void setVertexId(long vertexId) {
		this.vertexId = vertexId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (vertexId ^ (vertexId >>> 32));
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
		SimpleBulkVertex other = (SimpleBulkVertex) obj;
		if (vertexId != other.vertexId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleBulkVertex ["
				+ "vertexId=" + vertexId + ", "
				+ "label=" + label + ", "
				+ "properties=" + properties 
				+ "]";
	}

}
