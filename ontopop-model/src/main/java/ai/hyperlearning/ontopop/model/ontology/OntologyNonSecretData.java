package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Model - Non Secret Data
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyNonSecretData implements Serializable {

	private static final long serialVersionUID = -2410562383720708200L;
	
	@Schema(description = "ID of the ontology to update.", 
	        example = "1", 
	        required = false)
	private int id;
	
	@Schema(description = "Updated short free-text name for the ontology.", 
	        example = "National Highways Ontology.", 
	        required = false)
	private String name;
	
	@Schema(description = "Updated free-text description of the ontology.", 
	        example = "National Highways ontology-based conceptual data model.", 
	        required = false)
	private String description;
	
	@Schema(description = "Updated uRL of the Git repository managing the OWL file for this ontology.", 
	        example = "https://github.com/myorg/myrepo", 
	        required = false)
	private String repoUrl;
	
	@Schema(description = "Updated name of the Git repository managing the OWL file for this ontology.", 
	        example = "myrepo", 
	        required = false)
	private String repoName;
	
	@Schema(description = "Updated owner of the Git repository managing the OWL file for this ontology.", 
	        example = "myorg", 
	        required = false)
	private String repoOwner;
	
	@Schema(description = "Whether the Git repository managing the OWL file for this ontology is a private repository.", 
	        example = "true", 
	        required = false)
	private Boolean repoPrivate;
	
	@Schema(description = "Updated path to the OWL file for this ontology inside the Git repository.", 
	        example = "data/ontology.owl", 
	        required = false)
	private String repoResourcePath;
	
	@Schema(description = "Updated name of the Git repository branch to monitor for changes to the OWL file.", 
	        example = "main", 
	        required = false)
	private String repoBranch;
	
	@Schema(description = "System generated ontology last updated date and time.", 
	        example = "2022-02-28 09:08:07", 
	        required = false)
	private LocalDateTime dateLastUpdated;
	
	@Schema(description = "WebProtege project ID.", 
	        example = "0b3be685-73bd-4d5a-b866-e70d0ac7169b", 
	        required = false)
	private String webProtegeProjectId;
	
	public OntologyNonSecretData() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Boolean isRepoPrivate() {
		return repoPrivate;
	}

	public void setRepoPrivate(Boolean repoPrivate) {
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
				+ "name=" + name + ", "
				+ "description=" + description + ", "
				+ "repoUrl=" + repoUrl + ", "
				+ "repoName=" + repoName + ", "
				+ "repoOwner=" + repoOwner + ", "
				+ "repoPrivate=" + repoPrivate + ", "
				+ "repoResourcePath=" + repoResourcePath + ", "
				+ "repoBranch=" + repoBranch + ", "
				+ "dateLastUpdated=" + dateLastUpdated 
				+ "]";
	}

}
