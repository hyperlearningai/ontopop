package ai.hyperlearning.ontopop.model.git;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import ai.hyperlearning.ontopop.model.ontology.Ontology;

/**
 * Git Webhook Event Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Entity
@Table(name = "webhookevents")
public class WebhookEvent implements Serializable {

	private static final long serialVersionUID = 4064948544862677934L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "webhookevent_id")
	private long id;
	
	@NotNull
	private String ref;
	
	@NotNull
	private String repoUrl;
	
	@NotNull
	private long repoId;
	
	@NotNull
	private String repoName;
	
	@NotNull
	private String repoOwner;
	
	@NotNull
	private String repoResourcePath;
	
	@NotNull
	private String repoBranch;
	
	@NotNull
	private String pusherName;
	
	@NotNull
	private String pusherEmail;
	
	@JsonInclude()
	@Transient
	private Set<String> commitsModifiedResourcePaths;
	
	@NotNull
	private String latestRelevantCommitId;
	
	private String latestRelevantCommitMessage;
	
	@Basic
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime latestRelevantCommitTimestamp;
	
	@NotNull
	private String latestRelevantCommitAuthorName;
	
	@NotNull
	private String latestRelevantCommitAuthorEmail;
	
	@NotNull
	private String latestRelevantCommitAuthorUsername;
	
	@NotNull
	private String latestRelevantCommitCommitterName;
	
	@NotNull
	private String latestRelevantCommitCommitterEmail;
	
	@NotNull
	private String latestRelevantCommitCommitterUsername;
	
	@NotNull
	private String requestHeaderSignature;
	
	@ManyToOne
    @JoinColumn(name="ontology_id", nullable=false)
    private Ontology ontology;
	
	public WebhookEvent() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public long getRepoId() {
		return repoId;
	}

	public void setRepoId(long repoId) {
		this.repoId = repoId;
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

	public String getPusherName() {
		return pusherName;
	}

	public void setPusherName(String pusherName) {
		this.pusherName = pusherName;
	}

	public String getPusherEmail() {
		return pusherEmail;
	}

	public Set<String> getCommitsModifiedResourcePaths() {
		return commitsModifiedResourcePaths;
	}

	public void setCommitsModifiedResourcePaths(
			Set<String> commitsModifiedResourcePaths) {
		this.commitsModifiedResourcePaths = commitsModifiedResourcePaths;
	}

	public void setPusherEmail(String pusherEmail) {
		this.pusherEmail = pusherEmail;
	}

	public String getLatestRelevantCommitId() {
		return latestRelevantCommitId;
	}

	public void setLatestRelevantCommitId(
			String latestRelevantCommitId) {
		this.latestRelevantCommitId = latestRelevantCommitId;
	}

	public String getLatestRelevantCommitMessage() {
		return latestRelevantCommitMessage;
	}

	public void setLatestRelevantCommitMessage(
			String latestRelevantCommitMessage) {
		this.latestRelevantCommitMessage = latestRelevantCommitMessage;
	}

	public LocalDateTime getLatestRelevantCommitTimestamp() {
		return latestRelevantCommitTimestamp;
	}

	public void setLatestRelevantCommitTimestamp(
			LocalDateTime latestRelevantCommitTimestamp) {
		this.latestRelevantCommitTimestamp = latestRelevantCommitTimestamp;
	}

	public String getLatestRelevantCommitAuthorName() {
		return latestRelevantCommitAuthorName;
	}

	public void setLatestRelevantCommitAuthorName(
			String latestRelevantCommitAuthorName) {
		this.latestRelevantCommitAuthorName = latestRelevantCommitAuthorName;
	}

	public String getLatestRelevantCommitAuthorEmail() {
		return latestRelevantCommitAuthorEmail;
	}

	public void setLatestRelevantCommitAuthorEmail(
			String latestRelevantCommitAuthorEmail) {
		this.latestRelevantCommitAuthorEmail = latestRelevantCommitAuthorEmail;
	}

	public String getLatestRelevantCommitAuthorUsername() {
		return latestRelevantCommitAuthorUsername;
	}

	public void setLatestRelevantCommitAuthorUsername(
			String latestRelevantCommitAuthorUsername) {
		this.latestRelevantCommitAuthorUsername = 
				latestRelevantCommitAuthorUsername;
	}

	public String getLatestRelevantCommitCommitterName() {
		return latestRelevantCommitCommitterName;
	}

	public void setLatestRelevantCommitCommitterName(
			String latestRelevantCommitCommitterName) {
		this.latestRelevantCommitCommitterName = 
				latestRelevantCommitCommitterName;
	}

	public String getLatestRelevantCommitCommitterEmail() {
		return latestRelevantCommitCommitterEmail;
	}

	public void setLatestRelevantCommitCommitterEmail(
			String latestRelevantCommitCommitterEmail) {
		this.latestRelevantCommitCommitterEmail = 
				latestRelevantCommitCommitterEmail;
	}

	public String getLatestRelevantCommitCommitterUsername() {
		return latestRelevantCommitCommitterUsername;
	}

	public void setLatestRelevantCommitCommitterUsername(
			String latestRelevantCommitCommitterUsername) {
		this.latestRelevantCommitCommitterUsername = 
				latestRelevantCommitCommitterUsername;
	}

	public String getRequestHeaderSignature() {
		return requestHeaderSignature;
	}

	public void setRequestHeaderSignature(String requestHeaderSignature) {
		this.requestHeaderSignature = requestHeaderSignature;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		WebhookEvent other = (WebhookEvent) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WebhookEvent ["
				+ "id=" + id + ", "
				+ "ref=" + ref + ", "
				+ "repoUrl=" + repoUrl + ", "
				+ "repoId=" + repoId + ", "
				+ "repoName=" + repoName + ", "
				+ "repoOwner=" + repoOwner + ", "
				+ "repoResourcePath=" + repoResourcePath + ", "
				+ "repoBranch=" + repoBranch + ", "
				+ "pusherName=" + pusherName + ", "
				+ "pusherEmail=" + pusherEmail + ", "
				+ "commitsModifiedResourcePaths=" + commitsModifiedResourcePaths + ", "
				+ "latestRelevantCommitId=" + latestRelevantCommitId + ", "
				+ "latestRelevantCommitMessage=" + latestRelevantCommitMessage + ", "
				+ "latestRelevantCommitTimestamp=" + latestRelevantCommitTimestamp + ", "
				+ "latestRelevantCommitAuthorName=" + latestRelevantCommitAuthorName + ", "
				+ "latestRelevantCommitAuthorEmail=" + latestRelevantCommitAuthorEmail + ", "
				+ "latestRelevantCommitAuthorUsername=" + latestRelevantCommitAuthorUsername + ", "
				+ "latestRelevantCommitCommitterName=" + latestRelevantCommitCommitterName + ", "
				+ "latestRelevantCommitCommitterEmail=" + latestRelevantCommitCommitterEmail + ", "
				+ "latestRelevantCommitCommitterUsername=" + latestRelevantCommitCommitterUsername + ", "
				+ "requestHeaderSignature=" + requestHeaderSignature + ", "
				+ "ontology=" + ontology 
				+ "]";
	}
	
}
