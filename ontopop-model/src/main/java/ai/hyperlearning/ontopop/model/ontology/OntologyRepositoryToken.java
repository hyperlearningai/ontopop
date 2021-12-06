package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;

/**
 * Ontology Model - Repository Access Token
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyRepositoryToken implements Serializable {

	private static final long serialVersionUID = -7266346052340382363L;
	private int id;
	private String repoToken;
	
	public OntologyRepositoryToken() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRepoToken() {
		return repoToken;
	}

	public void setRepoToken(String repoToken) {
		this.repoToken = repoToken;
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
		OntologyRepositoryToken other = (OntologyRepositoryToken) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
