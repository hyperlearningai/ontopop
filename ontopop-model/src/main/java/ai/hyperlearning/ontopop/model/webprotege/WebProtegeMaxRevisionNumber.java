package ai.hyperlearning.ontopop.model.webprotege;

import java.io.Serializable;

/**
 * Webprotege Maximum Revision Number per Project
 * Refer to ai.hyperlearning.ontopop.data.jpa.repositories.WebProtegeWebhookRepository
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeMaxRevisionNumber implements Serializable {
    
    private static final long serialVersionUID = -7167232772258912384L;
    
    private String projectId;
    private int revisionNumber;
    
    public WebProtegeMaxRevisionNumber() {
        
    }

    public WebProtegeMaxRevisionNumber(String projectId, int revisionNumber) {
        super();
        this.projectId = projectId;
        this.revisionNumber = revisionNumber;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    @Override
    public String toString() {
        return "WebProtegeMaxRevisionNumber ["
                + "projectId=" + projectId + ", "
                + "revisionNumber=" + revisionNumber + "]";
    }

}
