package ai.hyperlearning.ontopop.model.webprotege;

import java.io.Serializable;

/**
 * Webprotege Webhook Payload Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeWebhookPayload implements Serializable {

    private static final long serialVersionUID = 337501327996667335L;
    private String projectId;
    private String userId;
    private int revisionNumber;
    private long timestamp;
    
    public WebProtegeWebhookPayload() {
        
    }

    public WebProtegeWebhookPayload(String projectId, String userId,
            int revisionNumber, long timestamp) {
        super();
        this.projectId = projectId;
        this.userId = userId;
        this.revisionNumber = revisionNumber;
        this.timestamp = timestamp;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((projectId == null) ? 0 : projectId.hashCode());
        result = prime * result + revisionNumber;
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
        WebProtegeWebhookPayload other =
                (WebProtegeWebhookPayload) obj;
        if (projectId == null) {
            if (other.projectId != null)
                return false;
        } else if (!projectId.equals(other.projectId))
            return false;
        if (revisionNumber != other.revisionNumber)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WebProtegeProjectNotification ["
                + "projectId=" + projectId + ", "
                + "userId=" + userId + ", "
                + "revisionNumber=" + revisionNumber + ", "
                + "timestamp=" + timestamp + "]";
    }

}
