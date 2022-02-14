package ai.hyperlearning.ontopop.model.webprotege;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ai.hyperlearning.ontopop.model.ontology.Ontology;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Webprotege Webhook Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Entity
@Table(name = "wpwebhooks")
public class WebProtegeWebhook implements Serializable {

    private static final long serialVersionUID = 337501327996667335L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "wpwebhook_id")
    private long id;
    
    @NotNull
    @Schema(description = "WebProtege project ID of the project that has been updated.", 
            example = "c9589912-e17b-4156-a1ab-fa5b10862f54", 
            required = true)
    private String projectId;
    
    @NotNull
    @Schema(description = "WebProtege user ID of the user that has updated the WebProtege project.", 
            example = "me@tld.com", 
            required = true)
    private String userId;
    
    @NotNull
    @Schema(description = "WebProtege project revision number as a result of the latest updates.", 
            example = "2960", 
            required = true)
    private int revisionNumber;
    
    @NotNull
    @Schema(description = "Timestamp of the latest WebProtege project update.", 
            example = "1644414105240", 
            required = true)
    private long timestamp;
    
    @ManyToOne
    @JoinColumn(name = "ontology_id", nullable = false)
    private Ontology ontology;
    
    public WebProtegeWebhook() {
        
    }

    public WebProtegeWebhook(String projectId, String userId,
            int revisionNumber, long timestamp) {
        super();
        this.projectId = projectId;
        this.userId = userId;
        this.revisionNumber = revisionNumber;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        WebProtegeWebhook other = (WebProtegeWebhook) obj;
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
        return "WebProtegeWebhook ["
                + "id=" + id + ", "
                + "projectId=" + projectId + ", "
                + "userId=" + userId + ", "
                + "revisionNumber=" + revisionNumber + ", "
                + "timestamp=" + timestamp + ", "
                + "ontologyId=" + (ontology == null ? "" : ontology.getId())
                + "]";
    }

}
