package ai.hyperlearning.ontopop.model.ontology;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import ai.hyperlearning.ontopop.model.git.WebhookEvent;

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
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@NotNull
	private String repoUrl;
	
	@NotNull
	private String repoOwner;
	
	@NotNull
	private String repoResourcePath;
	
	@NotNull
	private String repoBranch;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateCreated;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateLastUpdated;
	
	@OneToMany(mappedBy="ontology")
    private Set<WebhookEvent> webhooksEvents;
	
	public Ontology() {
		
	}

	public Ontology(int id, @NotNull String repoUrl, @NotNull String repoOwner, 
			@NotNull String repoResourcePath, @NotNull String repoBranch, 
			LocalDateTime dateCreated, LocalDateTime dateLastUpdated) {
		super();
		this.id = id;
		this.repoUrl = repoUrl;
		this.repoOwner = repoOwner;
		this.repoResourcePath = repoResourcePath;
		this.repoBranch = repoBranch;
		this.dateCreated = dateCreated;
		this.dateLastUpdated = dateLastUpdated;
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

	public String getRepoOwner() {
		return repoOwner;
	}

	public void setRepoOwner(String repoOwner) {
		this.repoOwner = repoOwner;
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
				+ "repoUrl=" + repoUrl + ", "
				+ "repoOwner=" + repoOwner + ", "
				+ "repoResourcePath=" + repoResourcePath + ", "
				+ "repoBranch=" + repoBranch + ", "
				+ "dateCreated=" + dateCreated + ", "
				+ "dateLastUpdated=" + dateLastUpdated 
				+ "]";
	}	
	
}
