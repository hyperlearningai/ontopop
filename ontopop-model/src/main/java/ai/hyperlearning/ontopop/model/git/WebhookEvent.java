package ai.hyperlearning.ontopop.model.git;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	
	@NotNull
	private String latestRelevantCommitId;
	
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
    @JoinColumn(name="id", nullable=false)
    private Ontology ontology;
	
	public WebhookEvent() {
		
	}

	public WebhookEvent(long id, @NotNull String ref, @NotNull String repoUrl, 
			@NotNull long repoId, @NotNull String repoName, 
			@NotNull String repoOwner, @NotNull String repoResourcePath,
			@NotNull String repoBranch, @NotNull String pusherName, 
			@NotNull String pusherEmail, @NotNull String latestRelevantCommitId, 
			LocalDateTime latestRelevantCommitTimestamp, 
			@NotNull String latestRelevantCommitAuthorName,
			@NotNull String latestRelevantCommitAuthorEmail, 
			@NotNull String latestRelevantCommitAuthorUsername,
			@NotNull String latestRelevantCommitCommitterName,
			@NotNull String latestRelevantCommitCommitterEmail,
			@NotNull String latestRelevantCommitCommitterUsername,
			@NotNull String requestHeaderSignature, 
			Ontology ontology) {
		super();
		this.id = id;
		this.ref = ref;
		this.repoUrl = repoUrl;
		this.repoId = repoId;
		this.repoName = repoName;
		this.repoOwner = repoOwner;
		this.repoResourcePath = repoResourcePath;
		this.repoBranch = repoBranch;
		this.pusherName = pusherName;
		this.pusherEmail = pusherEmail;
		this.latestRelevantCommitId = latestRelevantCommitId;
		this.latestRelevantCommitTimestamp = latestRelevantCommitTimestamp;
		this.latestRelevantCommitAuthorName = latestRelevantCommitAuthorName;
		this.latestRelevantCommitAuthorEmail = latestRelevantCommitAuthorEmail;
		this.latestRelevantCommitAuthorUsername = latestRelevantCommitAuthorUsername;
		this.latestRelevantCommitCommitterName = latestRelevantCommitCommitterName;
		this.latestRelevantCommitCommitterEmail = latestRelevantCommitCommitterEmail;
		this.latestRelevantCommitCommitterUsername = latestRelevantCommitCommitterUsername;
		this.requestHeaderSignature = requestHeaderSignature;
		this.ontology = ontology;
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

	public void setPusherEmail(String pusherEmail) {
		this.pusherEmail = pusherEmail;
	}

	public String getLatestRelevantCommitId() {
		return latestRelevantCommitId;
	}

	public void setLatestRelevantCommitId(String latestRelevantCommitId) {
		this.latestRelevantCommitId = latestRelevantCommitId;
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
				+ "latestRelevantCommitId=" + latestRelevantCommitId + ", "
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
