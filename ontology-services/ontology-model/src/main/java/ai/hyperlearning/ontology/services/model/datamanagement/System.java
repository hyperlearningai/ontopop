package ai.hyperlearning.ontology.services.model.datamanagement;

import java.io.Serializable;
import java.util.Map;

/**
 * Simple Physical Data System Model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class System implements Serializable {

	private static final long serialVersionUID = 7472255981395364446L;
	private String name;
	private Map<String, Object> metadata;
	
	public System() {
		
	}

	public System(String name, Map<String, Object> metadata) {
		super();
		this.name = name;
		this.metadata = metadata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		System other = (System) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "System ["
				+ "name=" + name + ", "
				+ "metadata=" + metadata + "]";
	}

}
