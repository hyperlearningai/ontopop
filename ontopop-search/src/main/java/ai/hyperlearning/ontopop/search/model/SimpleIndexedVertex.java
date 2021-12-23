package ai.hyperlearning.ontopop.search.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Simple Vertex model for indexing
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Document(indexName = "#{@environment.getProperty('storage.search.elasticsearch.indexNamePrefix')}", createIndex = false)
public class SimpleIndexedVertex implements Serializable {

	private static final long serialVersionUID = -692044707286693545L;
	
	@org.springframework.data.annotation.Id
	@Field(type = FieldType.Long)
	private long vertexId;
	
	@Field(type = FieldType.Text)
	private String label;
	
	private Map<String, Object> properties;
	
	public SimpleIndexedVertex() {
		
	}

	public SimpleIndexedVertex(long vertexId, 
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
	public String toString() {
		return "SimpleIndexedVertex ["
				+ "vertexId=" + vertexId + ", "
				+ "label=" + label + ", "
				+ "properties=" + properties + "]";
	}

}
