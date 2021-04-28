package ai.hyperlearning.ontology.services.model.datamanagement;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Simple Physical Dataset Model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class Dataset implements Serializable {

	private static final long serialVersionUID = -7508253867943586586L;
	private String name;
	private Set<String> entities;
	private Map<String, Object> metadata;
	
	public Dataset() {
		
	}

	public Dataset(
			String name, Set<String> entities, Map<String, Object> metadata) {
		super();
		this.name = name;
		this.entities = entities;
		this.metadata = metadata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getEntities() {
		return entities;
	}

	public void setEntities(Set<String> entities) {
		this.entities = entities;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Dataset other = (Dataset) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Dataset ["
				+ "name=" + name + ", "
				+ "entities=" + entities + ", "
				+ "metadata=" + metadata + "]";
	}

}
