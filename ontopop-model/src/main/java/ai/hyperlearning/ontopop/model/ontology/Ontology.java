package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Entity
@Table(name = "ontologies")
public class Ontology implements Serializable {

	private static final long serialVersionUID = 897119992423827281L;
	private static final String WINDOWS_FILE_SEPARATOR = "\\";
	private static final String UNIX_FILE_SEPARATOR = "/";
    private static final String FILE_EXTENSION = ".";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ontology_id")
	@Schema(description = "Automatically generated unique identifier for the ontology.", 
	    example = "1")
	private int id;
	
	@NotNull
	@Column(length = 100)
	@Schema(description = "Short free-text name for the ontology.", 
	    example = "National Highways Ontology.", 
	    required = true)
	private String name;
	
	@Column(length = 250)
	@Schema(description = "Free-text description of the ontology.", 
	    example = "National Highways ontology-based conceptual data model.", 
	    required = false)
	private String description;
	
	@NotNull
	@Schema(description = "URL of the Git repository managing the OWL file for this ontology.", 
        example = "https://github.com/myorg/myrepo", 
        required = true)
	private String repoUrl;
	
	@NotNull
	@Schema(description = "Name of the Git repository managing the OWL file for this ontology.", 
        example = "myrepo", 
        required = true)
	private String repoName;
	
	@NotNull
	@Schema(description = "Owner of the Git repository managing the OWL file for this ontology.", 
        example = "myorg", 
        required = true)
	private String repoOwner;
	
	@NotNull
	@Schema(description = "Whether the Git repository managing the OWL file for this ontology is a private repository.", 
        example = "true", 
        required = true)
	private boolean repoPrivate;
	
	@NotNull
    @Schema(description = "The path to the OWL file for this ontology inside the Git repository.", 
        example = "data/ontology.owl", 
        required = true)
	private String repoResourcePath;
	
	@NotNull
	@Schema(description = "The name of the Git repository branch to monitor for changes to the OWL file.", 
        example = "main", 
        required = true)
	private String repoBranch;
	
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	@Schema(description = "Access token required to access the OWL file managed in private Git repositories.", 
        example = "ghp_123456789abcdefghi")
	private String repoToken;
	
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	@Schema(description = "User-defined webhook secret used to validate Git webhooks from this Git repository.", 
        example = "mysecret123")
	private String repoWebhookSecret;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "System generated ontology creation date and time.", 
        example = "2022-01-31 11:59:59")
	private LocalDateTime dateCreated;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "System generated ontology last updated date and time.", 
        example = "2022-02-28 09:08:07")
	private LocalDateTime dateLastUpdated;
	
	@Column(name = "wp_project_id", length = 100, nullable = true)
	@Schema(description = "WebProtege project ID.", 
        example = "0b3be685-73bd-4d5a-b866-e70d0ac7169b", 
        required = false)
	private String webProtegeProjectId;
	
	@OneToMany(mappedBy="ontology", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
    private Set<GitWebhook> gitWebhooks;
	
	@OneToMany(mappedBy="ontology", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private Set<WebProtegeWebhook> webProtegeWebhooks;
	
	public Ontology() {
		
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

	public String getRepoToken() {
		return repoToken;
	}

	public void setRepoToken(String repoToken) {
		this.repoToken = repoToken;
	}

	public String getRepoWebhookSecret() {
		return repoWebhookSecret;
	}

	public void setRepoWebhookSecret(String repoWebhookSecret) {
		this.repoWebhookSecret = repoWebhookSecret;
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public LocalDateTime getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(LocalDateTime dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	public Set<GitWebhook> getGitWebhooks() {
		return gitWebhooks;
	}

	public void setGitWebhooks(Set<GitWebhook> gitWebhooks) {
		this.gitWebhooks = gitWebhooks;
	}
	
	public String getWebProtegeProjectId() {
        return webProtegeProjectId;
    }

    public void setWebProtegeProjectId(String webProtegeProjectId) {
        this.webProtegeProjectId = webProtegeProjectId;
    }

    public Set<WebProtegeWebhook> getWebProtegeWebhooks() {
        return webProtegeWebhooks;
    }

    public void setWebProtegeWebhooks(
            Set<WebProtegeWebhook> webProtegeWebhooks) {
        this.webProtegeWebhooks = webProtegeWebhooks;
    }

    public void clearSecretData() {
		this.repoToken = null;
		this.repoWebhookSecret = null;
	}
	
	public String standardiseRepoResourcePath() {
		return Paths.get(repoResourcePath)
				.getFileName()
				.toString()
				.replace(" ", "-")
				.trim();
	}
	
	@JsonIgnore
	public String getRepoResourcePathFileExtension() {
	    int indexOfLastExtension = repoResourcePath
	            .lastIndexOf(FILE_EXTENSION);
	    int indexOfLastSeparator = Math.max(
	            repoResourcePath.lastIndexOf(WINDOWS_FILE_SEPARATOR), 
	            repoResourcePath.lastIndexOf(UNIX_FILE_SEPARATOR));
	    return (indexOfLastExtension > indexOfLastSeparator) ? 
	            "." + repoResourcePath.substring(indexOfLastExtension  + 1)
	                .toLowerCase()
	                .trim() : "";
	}
	
	public String generateFilenameForPersistence(long gitWebhookId) {
		return id + "_" + gitWebhookId + getRepoResourcePathFileExtension();
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
		Ontology other = (Ontology) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Ontology ["
				+ "id=" + id + ", "
				+ "name=" + name + ", "
				+ "description=" + description + ", "
				+ "repoUrl=" + repoUrl + ", "
				+ "repoName=" + repoName + ", "
				+ "repoOwner=" + repoOwner + ", "
				+ "repoPrivate=" + repoPrivate + ", "
				+ "repoResourcePath=" + repoResourcePath + ", "
				+ "repoBranch=" + repoBranch + ", "
				+ "dateCreated=" + dateCreated + ", "
				+ "dateLastUpdated=" + dateLastUpdated + ", "
				+ "webProtegeProjectId=" + webProtegeProjectId
				+ "]";
	}	
	
}
