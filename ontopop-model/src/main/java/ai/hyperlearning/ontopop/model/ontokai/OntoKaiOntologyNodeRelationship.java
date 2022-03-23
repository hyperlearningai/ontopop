package ai.hyperlearning.ontopop.model.ontokai;

import java.io.Serializable;

/**
 * OntoKai Ontology Node Relationship Schema
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyNodeRelationship implements Serializable {

    private static final long serialVersionUID = 2993824496772491694L;
    
    private int childId;
    private int parentId;
    private String type;
    private String bundleType;
    private String bundleValue;
    
    public OntoKaiOntologyNodeRelationship() {
        
    }

    public OntoKaiOntologyNodeRelationship(int childId, int parentId,
            String type, String bundleType, String bundleValue) {
        super();
        this.childId = childId;
        this.parentId = parentId;
        this.type = type;
        this.bundleType = bundleType;
        this.bundleValue = bundleValue;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBundleType() {
        return bundleType;
    }

    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }

    public String getBundleValue() {
        return bundleValue;
    }

    public void setBundleValue(String bundleValue) {
        this.bundleValue = bundleValue;
    }

    @Override
    public String toString() {
        return "OntoKaiOntologyNodeRelationship ["
                + "childId=" + childId + ", "
                + "parentId=" + parentId + ", "
                + "type=" + type + ", "
                + "bundleType=" + bundleType + ", "
                + "bundleValue=" + bundleValue 
                + "]";
    }

}
