package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * Simple OWL Model - Ontology Diff
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyDiff implements Serializable {

    private static final long serialVersionUID = 2992288149293570148L;
    
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
    
    // Annotation property diffs
    private List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationProperties = new ArrayList<>();
    private List<SimpleAnnotationPropertyDiff> updatedSimpleAnnotationProperties = new ArrayList<>();
    private List<SimpleAnnotationPropertyDiff> deletedSimpleAnnotationProperties = new ArrayList<>();
    
    // Object property diffs
    private List<SimpleObjectPropertyDiff> createdSimpleObjectProperties = new ArrayList<>();
    private List<SimpleObjectPropertyDiff> updatedSimpleObjectProperties = new ArrayList<>();
    private List<SimpleObjectPropertyDiff> deletedSimpleObjectProperties = new ArrayList<>();
    
    // Class diffs
    private List<SimpleClassDiff> createdSimpleClasses = new ArrayList<>();
    private List<SimpleClassDiff> updatedSimpleClasses = new ArrayList<>();
    private List<SimpleClassDiff> deletedSimpleClasses = new ArrayList<>();
    
    public SimpleOntologyDiff() {
        
    }

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

    public List<SimpleAnnotationPropertyDiff> getCreatedSimpleAnnotationProperties() {
        return createdSimpleAnnotationProperties;
    }

    public void setCreatedSimpleAnnotationProperties(
            List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationProperties) {
        this.createdSimpleAnnotationProperties = createdSimpleAnnotationProperties;
    }

    public List<SimpleAnnotationPropertyDiff> getUpdatedSimpleAnnotationProperties() {
        return updatedSimpleAnnotationProperties;
    }

    public void setUpdatedSimpleAnnotationProperties(
            List<SimpleAnnotationPropertyDiff> updatedSimpleAnnotationProperties) {
        this.updatedSimpleAnnotationProperties = updatedSimpleAnnotationProperties;
    }

    public List<SimpleAnnotationPropertyDiff> getDeletedSimpleAnnotationProperties() {
        return deletedSimpleAnnotationProperties;
    }

    public void setDeletedSimpleAnnotationProperties(
            List<SimpleAnnotationPropertyDiff> deletedSimpleAnnotationProperties) {
        this.deletedSimpleAnnotationProperties = deletedSimpleAnnotationProperties;
    }

    public List<SimpleObjectPropertyDiff> getCreatedSimpleObjectProperties() {
        return createdSimpleObjectProperties;
    }

    public void setCreatedSimpleObjectProperties(
            List<SimpleObjectPropertyDiff> createdSimpleObjectProperties) {
        this.createdSimpleObjectProperties = createdSimpleObjectProperties;
    }

    public List<SimpleObjectPropertyDiff> getUpdatedSimpleObjectProperties() {
        return updatedSimpleObjectProperties;
    }

    public void setUpdatedSimpleObjectProperties(
            List<SimpleObjectPropertyDiff> updatedSimpleObjectProperties) {
        this.updatedSimpleObjectProperties = updatedSimpleObjectProperties;
    }

    public List<SimpleObjectPropertyDiff> getDeletedSimpleObjectProperties() {
        return deletedSimpleObjectProperties;
    }

    public void setDeletedSimpleObjectProperties(
            List<SimpleObjectPropertyDiff> deletedSimpleObjectProperties) {
        this.deletedSimpleObjectProperties = deletedSimpleObjectProperties;
    }

    public List<SimpleClassDiff> getCreatedSimpleClasses() {
        return createdSimpleClasses;
    }

    public void setCreatedSimpleClasses(
            List<SimpleClassDiff> createdSimpleClasses) {
        this.createdSimpleClasses = createdSimpleClasses;
    }

    public List<SimpleClassDiff> getUpdatedSimpleClasses() {
        return updatedSimpleClasses;
    }

    public void setUpdatedSimpleClasses(
            List<SimpleClassDiff> updatedSimpleClasses) {
        this.updatedSimpleClasses = updatedSimpleClasses;
    }

    public List<SimpleClassDiff> getDeletedSimpleClasses() {
        return deletedSimpleClasses;
    }

    public void setDeletedSimpleClasses(
            List<SimpleClassDiff> deletedSimpleClasses) {
        this.deletedSimpleClasses = deletedSimpleClasses;
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
        SimpleOntologyDiff other = (SimpleOntologyDiff) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SimpleOntologyDiff ["
                + "id=" + id + ", "
                + "requestedTimestamp=" + requestedTimestamp + ", "
                + "latestGitWebhookIdBeforeRequestedTimestamp=" + latestGitWebhookIdBeforeRequestedTimestamp + ", "
                + "latestGitWebhookTimestampBeforeRequestedTimestamp=" + latestGitWebhookTimestampBeforeRequestedTimestamp + ", "
                + "latestGitWebhookIdAfterRequestedTimestamp=" + latestGitWebhookIdAfterRequestedTimestamp + ", "
                + "latestGitWebhookTimestampAfterRequestedTimestamp=" + latestGitWebhookTimestampAfterRequestedTimestamp + ", "
                + "updatesExist=" + updatesExist + ", "
                + "createdSimpleAnnotationProperties=" + createdSimpleAnnotationProperties + ", "
                + "updatedSimpleAnnotationProperties=" + updatedSimpleAnnotationProperties + ", "
                + "deletedSimpleAnnotationProperties=" + deletedSimpleAnnotationProperties + ", "
                + "createdSimpleObjectProperties=" + createdSimpleObjectProperties + ", "
                + "updatedSimpleObjectProperties=" + updatedSimpleObjectProperties + ", "
                + "deletedSimpleObjectProperties=" + deletedSimpleObjectProperties + ", "
                + "createdSimpleClasses=" + createdSimpleClasses + ", "
                + "updatedSimpleClasses=" + updatedSimpleClasses + ", "
                + "deletedSimpleClasses=" + deletedSimpleClasses 
                + "]";
    }

}
