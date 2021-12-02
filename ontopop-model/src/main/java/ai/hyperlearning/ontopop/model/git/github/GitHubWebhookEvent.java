package ai.hyperlearning.ontopop.model.git.github;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai.hyperlearning.ontopop.model.git.WebhookEvent;

/**
 * GitHub Webhook Event Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GitHubWebhookEvent implements Serializable {
	
	private static final long serialVersionUID = 7283618078343604424L;
	private String ref;
	private String before;
	private String after;
	private Map<String, Object> repository;
	private Map<String, Object> pusher;
	private Map<String, Object> organization;
	private Map<String, Object> sender;
	private boolean created;
	private boolean deleted;
	private boolean forced;
	private String baseRef;
	private String compare;
	private List<Map<String, Object>> commits;
	private Map<String, Object> headCommit;
	
	public GitHubWebhookEvent() {
		
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public Map<String, Object> getRepository() {
		return repository;
	}

	public void setRepository(Map<String, Object> repository) {
		this.repository = repository;
	}

	public Map<String, Object> getPusher() {
		return pusher;
	}

	public void setPusher(Map<String, Object> pusher) {
		this.pusher = pusher;
	}

	public Map<String, Object> getOrganization() {
		return organization;
	}

	public void setOrganization(Map<String, Object> organization) {
		this.organization = organization;
	}

	public Map<String, Object> getSender() {
		return sender;
	}

	public void setSender(Map<String, Object> sender) {
		this.sender = sender;
	}

	public boolean isCreated() {
		return created;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isForced() {
		return forced;
	}

	public void setForced(boolean forced) {
		this.forced = forced;
	}

	public String getBaseRef() {
		return baseRef;
	}

	public void setBaseRef(String baseRef) {
		this.baseRef = baseRef;
	}

	public String getCompare() {
		return compare;
	}

	public void setCompare(String compare) {
		this.compare = compare;
	}

	public List<Map<String, Object>> getCommits() {
		return commits;
	}

	public void setCommits(List<Map<String, Object>> commits) {
		this.commits = commits;
	}

	public Map<String, Object> getHeadCommit() {
		return headCommit;
	}

	public void setHeadCommit(Map<String, Object> headCommit) {
		this.headCommit = headCommit;
	}
	
	@SuppressWarnings("unchecked")
	public WebhookEvent toWebhookEvent(
			String resourcePath, String requestSignaure) {
		
		WebhookEvent webhookEvent = new WebhookEvent();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
				"yyyy-MM-dd'T'HH:mm:ss'Z'");
		
		// Set reference
		webhookEvent.setRef( this.ref );
		
		// Set repository attributes
		webhookEvent.setRepoUrl( this.repository.get("html_url").toString() );
		webhookEvent.setRepoId( Long.valueOf(repository.get("id").toString()) );
		webhookEvent.setRepoName( this.repository.get("name").toString() );
		Map<String, Object> repositoryOwner = (Map<String, Object>) 
				this.repository.get("owner");
		webhookEvent.setRepoOwner( repositoryOwner.get("name").toString() );
		webhookEvent.setRepoResourcePath( resourcePath );
		webhookEvent.setRepoBranch( this.ref.substring(
				this.ref.lastIndexOf("/") + 1) );
		
		// Set pusher attributes
		webhookEvent.setPusherName( this.pusher.get("name").toString() );
		webhookEvent.setPusherEmail( this.pusher.get("email").toString() );
		
		// Set commits modified resource paths
		Set<String> commitsModifiedResourcePaths = new HashSet<>();
		for( Map<String, Object> commit : this.commits ) {
			List<String> modified = (List<String>) commit.get("modified");
			commitsModifiedResourcePaths.addAll(modified);
		}
		webhookEvent.setCommitsModifiedResourcePaths(
				commitsModifiedResourcePaths);
		
		// Set latest relevant commit attributes
		if (resourcePath != null) {
			
			commits:
			for( Map<String, Object> commit : this.commits ) {
				List<String> modified = (List<String>) commit.get("modified");
				for (String modifiedResource: modified) {
					if ( modifiedResource.equals(resourcePath) ) {
						
						// Set the commit ID and timestamp attributes
						webhookEvent.setLatestRelevantCommitId( 
								commit.get("id").toString() );
						webhookEvent.setLatestRelevantCommitMessage(
								commit.get("message").toString() );
						String timestamp = commit.get("timestamp").toString();
						webhookEvent.setLatestRelevantCommitTimestamp( 
								LocalDateTime.parse(timestamp, formatter) );
						
						// Set the commit author attributes
						Map<String, Object> author = 
								(Map<String, Object>) commit.get("author");
						webhookEvent.setLatestRelevantCommitAuthorName( 
								author.get("name").toString() );
						webhookEvent.setLatestRelevantCommitAuthorEmail( 
								author.get("email").toString() );
						webhookEvent.setLatestRelevantCommitAuthorUsername( 
								author.get("username").toString() );
						
						// Set the commit committer attributes
						Map<String, Object> committer = 
								(Map<String, Object>) commit.get("committer");
						webhookEvent.setLatestRelevantCommitCommitterName( 
								committer.get("name").toString() );
						webhookEvent.setLatestRelevantCommitCommitterEmail( 
								committer.get("email").toString() );
						webhookEvent.setLatestRelevantCommitCommitterUsername( 
								committer.get("username").toString() );
						
						// Stop iterating over the commits
						break commits;
						
					}
				}
				
			}
			
		}
		
		// Set the request signature attribute
		webhookEvent.setRequestHeaderSignature( requestSignaure );
		
		return webhookEvent;
		
	}

	@Override
	public String toString() {
		return "GitHubWebhookEvent ["
				+ "ref=" + ref + ", "
				+ "before=" + before + ", "
				+ "after=" + after + ", "
				+ "repository=" + repository + ", "
				+ "pusher=" + pusher + ", "
				+ "organization=" + organization + ", "
				+ "sender=" + sender + ", "
				+ "created=" + created + ", "
				+ "deleted=" + deleted + ", "
				+ "forced=" + forced + ", "
				+ "baseRef=" + baseRef + ", "
				+ "compare=" + compare + ", "
				+ "commits=" + commits + ", "
				+ "headCommit=" + headCommit 
				+ "]";
	}

}
