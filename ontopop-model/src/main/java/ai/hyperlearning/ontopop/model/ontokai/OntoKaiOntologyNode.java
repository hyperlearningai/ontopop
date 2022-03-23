package ai.hyperlearning.ontopop.model.ontokai;

import java.io.Serializable;
import java.util.List;

/**
 * OntoKai Ontology Node Schema
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntoKaiOntologyNode implements Serializable {

    private static final long serialVersionUID = 4119766509076827851L;
    
    private int id;
    private String category;
    private String label;
    private String url;
    private List<OntoKaiOntologyNodeAttribute> attributes;
    private List<OntoKaiOntologyNodeRelationship> relationships;
    
    public OntoKaiOntologyNode() {
        
    }

    public OntoKaiOntologyNode(int id, String category, String label,
            String url, List<OntoKaiOntologyNodeAttribute> attributes,
            List<OntoKaiOntologyNodeRelationship> relationships) {
        super();
        this.id = id;
        this.category = category;
        this.label = label;
        this.url = url;
        this.attributes = attributes;
        this.relationships = relationships;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<OntoKaiOntologyNodeAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OntoKaiOntologyNodeAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<OntoKaiOntologyNodeRelationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(
            List<OntoKaiOntologyNodeRelationship> relationships) {
        this.relationships = relationships;
    }

    @Override
    public String toString() {
        return "OntoKaiOntologyNode ["
                + "id=" + id + ", "
                + "category=" + category + ", "
                + "label=" + label + ", "
                + "url=" + url + ", "
                + "attributes=" + attributes + ", "
                + "relationships=" + relationships 
                + "]";
    }

}
