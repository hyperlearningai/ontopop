package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Simple OWL Model - Ontology Diff relative to given timestamp
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyTimestampDiff implements Serializable {

    private static final long serialVersionUID = -7916728335248570822L;
    
    // Ontology ID
    private int id;
    
    // Requested timestamp
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedTimestamp;
    
    // The closest Git Webhook ID before the requested timestamp
    private long latestGitWebhookIdBeforeRequestedTimestamp;
    
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestGitWebhookTimestampBeforeRequestedTimestamp;
    
    // The latest Git Webhook ID after the requested timestamp
    private long latestGitWebhookIdAfterRequestedTimestamp;
    
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestGitWebhookTimestampAfterRequestedTimestamp;
    
    // Whether updates to this ontology exist
    private boolean updatesExist = false;
    
    // Diff
    private SimpleOntologyDiff simpleOntologyDiff;
    
    public SimpleOntologyTimestampDiff() {
        
    }

    @JsonProperty("ontologyId")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getRequestedTimestamp() {
        return requestedTimestamp;
    }

    public void setRequestedTimestamp(LocalDateTime requestedTimestamp) {
        this.requestedTimestamp = requestedTimestamp;
    }

    public long getLatestGitWebhookIdBeforeRequestedTimestamp() {
        return latestGitWebhookIdBeforeRequestedTimestamp;
    }

    public void setLatestGitWebhookIdBeforeRequestedTimestamp(
            long latestGitWebhookIdBeforeRequestedTimestamp) {
        this.latestGitWebhookIdBeforeRequestedTimestamp =
                latestGitWebhookIdBeforeRequestedTimestamp;
    }

    public LocalDateTime getLatestGitWebhookTimestampBeforeRequestedTimestamp() {
        return latestGitWebhookTimestampBeforeRequestedTimestamp;
    }

    public void setLatestGitWebhookTimestampBeforeRequestedTimestamp(
            LocalDateTime latestGitWebhookTimestampBeforeRequestedTimestamp) {
        this.latestGitWebhookTimestampBeforeRequestedTimestamp =
                latestGitWebhookTimestampBeforeRequestedTimestamp;
    }

    public long getLatestGitWebhookIdAfterRequestedTimestamp() {
        return latestGitWebhookIdAfterRequestedTimestamp;
    }

    public void setLatestGitWebhookIdAfterRequestedTimestamp(
            long latestGitWebhookIdAfterRequestedTimestamp) {
        this.latestGitWebhookIdAfterRequestedTimestamp =
                latestGitWebhookIdAfterRequestedTimestamp;
    }

    public LocalDateTime getLatestGitWebhookTimestampAfterRequestedTimestamp() {
        return latestGitWebhookTimestampAfterRequestedTimestamp;
    }

    public void setLatestGitWebhookTimestampAfterRequestedTimestamp(
            LocalDateTime latestGitWebhookTimestampAfterRequestedTimestamp) {
        this.latestGitWebhookTimestampAfterRequestedTimestamp =
                latestGitWebhookTimestampAfterRequestedTimestamp;
    }

    public boolean isUpdatesExist() {
        return updatesExist;
    }
    
    public boolean doUpdatesExist() {
        return updatesExist;
    }

    public void setUpdatesExist(boolean updatesExist) {
        this.updatesExist = updatesExist;
    }

    @JsonProperty("diff")
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
        SimpleOntologyTimestampDiff other = (SimpleOntologyTimestampDiff) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleOntologyTimestampDiff ["
                + "id=" + id + ", "
                + "requestedTimestamp=" + requestedTimestamp + ", "
                + "latestGitWebhookIdBeforeRequestedTimestamp=" + latestGitWebhookIdBeforeRequestedTimestamp + ", "
                + "latestGitWebhookTimestampBeforeRequestedTimestamp=" + latestGitWebhookTimestampBeforeRequestedTimestamp + ", "
                + "latestGitWebhookIdAfterRequestedTimestamp=" + latestGitWebhookIdAfterRequestedTimestamp + ", "
                + "latestGitWebhookTimestampAfterRequestedTimestamp=" + latestGitWebhookTimestampAfterRequestedTimestamp + ", "
                + "updatesExist=" + updatesExist + ", "
                + "simpleOntologyDiff=" + simpleOntologyDiff 
                + "]";
    }

}
