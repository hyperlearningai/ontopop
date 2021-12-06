package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Ontology Model - Non Secret Data
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyNonSecretData implements Serializable {

	private static final long serialVersionUID = -2410562383720708200L;
	private int id;
	private String repoUrl;
	private String repoName;
	private String repoOwner;
	private boolean repoPrivate;
	private String repoResourcePath;
	private String repoBranch;
	private String description;
	private LocalDateTime dateLastUpdated;
	
	public OntologyNonSecretData() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public String getRepoOwner() {
		return repoOwner;
	}

	public void setRepoOwner(String repoOwner) {
		this.repoOwner = repoOwner;
	}

	public boolean isRepoPrivate() {
		return repoPrivate;
	}

	public void setRepoPrivate(boolean repoPrivate) {
		this.repoPrivate = repoPrivate;
	}

	public String getRepoResourcePath() {
		return repoResourcePath;
	}

	public void setRepoResourcePath(String repoResourcePath) {
		this.repoResourcePath = repoResourcePath;
	}

	public String getRepoBranch() {
		return repoBranch;
	}

	public void setRepoBranch(String repoBranch) {
		this.repoBranch = repoBranch;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
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
		OntologyNonSecretData other = (OntologyNonSecretData) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OntologyNonSecretData ["
				+ "id=" + id + ", "
				+ "repoUrl=" + repoUrl + ", "
				+ "repoName=" + repoName + ", "
				+ "repoOwner=" + repoOwner + ", "
				+ "repoPrivate=" + repoPrivate + ", "
				+ "repoResourcePath=" + repoResourcePath + ", "
				+ "repoBranch=" + repoBranch + ", "
				+ "description=" + description + ", "
				+ "dateLastUpdated=" + dateLastUpdated 
				+ "]";
	}

}
