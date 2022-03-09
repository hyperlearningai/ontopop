package ai.hyperlearning.ontopop.model.owl.diff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple OWL Model - Ontology Diff
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyDiff implements Serializable {

    private static final long serialVersionUID = 2992288149293570148L;
    
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
    
    public SimpleOntologyDiff(
            List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationProperties, 
            List<SimpleAnnotationPropertyDiff> updatedSimpleAnnotationProperties, 
            List<SimpleAnnotationPropertyDiff> deletedSimpleAnnotationProperties, 
            List<SimpleObjectPropertyDiff> createdSimpleObjectProperties, 
            List<SimpleObjectPropertyDiff> updatedSimpleObjectProperties, 
            List<SimpleObjectPropertyDiff> deletedSimpleObjectProperties, 
            List<SimpleClassDiff> createdSimpleClasses, 
            List<SimpleClassDiff> updatedSimpleClasses, 
            List<SimpleClassDiff> deletedSimpleClasses) {
        
        this.createdSimpleAnnotationProperties = createdSimpleAnnotationProperties;
        this.updatedSimpleAnnotationProperties = updatedSimpleAnnotationProperties;
        this.deletedSimpleAnnotationProperties = deletedSimpleAnnotationProperties;
        this.createdSimpleObjectProperties = createdSimpleObjectProperties;
        this.updatedSimpleObjectProperties = updatedSimpleObjectProperties;
        this.deletedSimpleObjectProperties = deletedSimpleObjectProperties;
        this.createdSimpleClasses = createdSimpleClasses;
        this.updatedSimpleClasses = updatedSimpleClasses;
        this.deletedSimpleClasses = deletedSimpleClasses;
        
    }

    @JsonProperty("createdAnnotationProperties")
    public List<SimpleAnnotationPropertyDiff> getCreatedSimpleAnnotationProperties() {
        return createdSimpleAnnotationProperties;
    }

    public void setCreatedSimpleAnnotationProperties(
            List<SimpleAnnotationPropertyDiff> createdSimpleAnnotationProperties) {
        this.createdSimpleAnnotationProperties = createdSimpleAnnotationProperties;
    }

    @JsonProperty("updatedAnnotationProperties")
    public List<SimpleAnnotationPropertyDiff> getUpdatedSimpleAnnotationProperties() {
        return updatedSimpleAnnotationProperties;
    }

    public void setUpdatedSimpleAnnotationProperties(
            List<SimpleAnnotationPropertyDiff> updatedSimpleAnnotationProperties) {
        this.updatedSimpleAnnotationProperties = updatedSimpleAnnotationProperties;
    }

    @JsonProperty("deletedAnnotationProperties")
    public List<SimpleAnnotationPropertyDiff> getDeletedSimpleAnnotationProperties() {
        return deletedSimpleAnnotationProperties;
    }

    public void setDeletedSimpleAnnotationProperties(
            List<SimpleAnnotationPropertyDiff> deletedSimpleAnnotationProperties) {
        this.deletedSimpleAnnotationProperties = deletedSimpleAnnotationProperties;
    }

    @JsonProperty("createdObjectProperties")
    public List<SimpleObjectPropertyDiff> getCreatedSimpleObjectProperties() {
        return createdSimpleObjectProperties;
    }

    public void setCreatedSimpleObjectProperties(
            List<SimpleObjectPropertyDiff> createdSimpleObjectProperties) {
        this.createdSimpleObjectProperties = createdSimpleObjectProperties;
    }

    @JsonProperty("updatedObjectProperties")
    public List<SimpleObjectPropertyDiff> getUpdatedSimpleObjectProperties() {
        return updatedSimpleObjectProperties;
    }

    public void setUpdatedSimpleObjectProperties(
            List<SimpleObjectPropertyDiff> updatedSimpleObjectProperties) {
        this.updatedSimpleObjectProperties = updatedSimpleObjectProperties;
    }

    @JsonProperty("deletedObjectProperties")
    public List<SimpleObjectPropertyDiff> getDeletedSimpleObjectProperties() {
        return deletedSimpleObjectProperties;
    }

    public void setDeletedSimpleObjectProperties(
            List<SimpleObjectPropertyDiff> deletedSimpleObjectProperties) {
        this.deletedSimpleObjectProperties = deletedSimpleObjectProperties;
    }

    @JsonProperty("createdClasses")
    public List<SimpleClassDiff> getCreatedSimpleClasses() {
        return createdSimpleClasses;
    }

    public void setCreatedSimpleClasses(
            List<SimpleClassDiff> createdSimpleClasses) {
        this.createdSimpleClasses = createdSimpleClasses;
    }

    @JsonProperty("updatedClasses")
    public List<SimpleClassDiff> getUpdatedSimpleClasses() {
        return updatedSimpleClasses;
    }

    public void setUpdatedSimpleClasses(
            List<SimpleClassDiff> updatedSimpleClasses) {
        this.updatedSimpleClasses = updatedSimpleClasses;
    }

    @JsonProperty("deletedClasses")
    public List<SimpleClassDiff> getDeletedSimpleClasses() {
        return deletedSimpleClasses;
    }

    public void setDeletedSimpleClasses(
            List<SimpleClassDiff> deletedSimpleClasses) {
        this.deletedSimpleClasses = deletedSimpleClasses;
    }
    
    @JsonIgnore
    public boolean doUpdatesExist() {
        return this.createdSimpleAnnotationProperties.isEmpty() && 
                this.updatedSimpleAnnotationProperties.isEmpty() && 
                this.deletedSimpleAnnotationProperties.isEmpty() && 
                this.createdSimpleObjectProperties.isEmpty() && 
                this.updatedSimpleObjectProperties.isEmpty() && 
                this.deletedSimpleObjectProperties.isEmpty() && 
                this.createdSimpleClasses.isEmpty() && 
                this.updatedSimpleClasses.isEmpty() && 
                this.deletedSimpleClasses.isEmpty() ? false : true;
    }

    @Override
    public String toString() {
        return "SimpleOntologyDiff ["
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
