package ai.hyperlearning.ontopop.model.git.github;

import java.io.Serializable;

import ai.hyperlearning.ontopop.model.git.GitAuthor;
import ai.hyperlearning.ontopop.model.git.GitCommitter;

/**
 * GitHub PUT file request body model
 * Reference: https://docs.github.com/en/rest/reference/repos#create-or-update-file-contents
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GitHubPutFileBody implements Serializable {

    private static final long serialVersionUID = 3588683459820680454L;
    private String message;
    private String content;
    private String sha;
    private String branch;
    private GitCommitter committer;
    private GitAuthor author;
    
    public GitHubPutFileBody() {
        
    }

    public GitHubPutFileBody(String message, String content, String sha,
            String branch, GitCommitter committer, GitAuthor author) {
        super();
        this.message = message;
        this.content = content;
        this.sha = sha;
        this.branch = branch;
        this.committer = committer;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public GitCommitter getCommitter() {
        return committer;
    }

    public void setCommitter(GitCommitter committer) {
        this.committer = committer;
    }

    public GitAuthor getAuthor() {
        return author;
    }

    public void setAuthor(GitAuthor author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "GitHubPutFileBody ["
                + "message=" + message + ", "
                + "content=" + content + ", "
                + "sha=" + sha + ", "
                + "branch=" + branch + ", "
                + "committer=" + committer + ", "
                + "author=" + author + 
                "]";
    }

}
