package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;

/**
 * Simple OWL Model - Ontology Diff relative to given left and right
 * Git webhooks IDs
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyLeftRightDiff implements Serializable {

    private static final long serialVersionUID = -1502836164586493568L;
    
    // Ontology ID
    private int id;
    
    private long leftGitWebhookId;
    
    private long rightGitWebhookId;
    
    // Diff
    private SimpleOntologyDiff simpleOntologyDiff;
    
    public SimpleOntologyLeftRightDiff() {
        
    }

    public SimpleOntologyLeftRightDiff(int id, long leftGitWebhookId,
            long rightGitWebhookId, SimpleOntologyDiff simpleOntologyDiff) {
        this.id = id;
        this.leftGitWebhookId = leftGitWebhookId;
        this.rightGitWebhookId = rightGitWebhookId;
        this.simpleOntologyDiff = simpleOntologyDiff;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLeftGitWebhookId() {
        return leftGitWebhookId;
    }

    public void setLeftGitWebhookId(long leftGitWebhookId) {
        this.leftGitWebhookId = leftGitWebhookId;
    }

    public long getRightGitWebhookId() {
        return rightGitWebhookId;
    }

    public void setRightGitWebhookId(long rightGitWebhookId) {
        this.rightGitWebhookId = rightGitWebhookId;
    }

    public SimpleOntologyDiff getSimpleOntologyDiff() {
        return simpleOntologyDiff;
    }

    public void setSimpleOntologyDiff(SimpleOntologyDiff simpleOntologyDiff) {
        this.simpleOntologyDiff = simpleOntologyDiff;
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
        SimpleOntologyLeftRightDiff other = (SimpleOntologyLeftRightDiff) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleOntologyGitWebhookDiff ["
                + "id=" + id + ", "
                + "leftGitWebhookId=" + leftGitWebhookId + ", "
                + "rightGitWebhookId=" + rightGitWebhookId + ", "
                + "simpleOntologyDiff=" + simpleOntologyDiff 
                + "]";
    }

}
