package ai.hyperlearning.ontopop.model.ontokai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleObjectProperty;

/**
 * OntoKai Ontology Node Schema
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class OntoKaiOntologyNode implements Serializable {

    private static final long serialVersionUID = 4119766509076827851L;
    private static final String RDF_SCHEMA_LABEL_IRI = 
            "http://www.w3.org/2000/01/rdf-schema#label";
    private static final String SUBCLASS_OF_OBJECT_PROPERTY_RDFS_LABEL = 
            "SUBCLASS OF";
    private static final String ONTOKAI_RESTRICTION_BUNDLE_TYPE_VALUE = 
            "SOME";
    
    private int id;
    private String category;
    private String label;
    private String url;
    private List<OntoKaiOntologyNodeAttribute> attributes = new ArrayList<>();
    private List<OntoKaiOntologyNodeRelationship> relationships = new ArrayList<>();
    
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
    
    @JsonIgnore
    public Map<String, String> generateAttributeIriValueMap(
            Map<String, SimpleAnnotationProperty> allSimpleAnnotationProperties) {
        Map<String, String> attributeIriValueMap = new HashMap<>();
        for ( OntoKaiOntologyNodeAttribute attribute : this.attributes ) {
            String transformedAttributeName = attribute.getName()
                    .strip().toUpperCase();
            if ( allSimpleAnnotationProperties.containsKey(
                    transformedAttributeName) )
                attributeIriValueMap.put(
                        allSimpleAnnotationProperties
                            .get(transformedAttributeName).getIri(), 
                        attribute.getValue());
        }
        attributeIriValueMap.put(RDF_SCHEMA_LABEL_IRI, this.label.strip());
        return attributeIriValueMap;
    }
    
    @JsonIgnore
    public Map<String, String> generateParentClassIriPropertyRestrictionIriMap(
            Map<Integer, String> ontoKaiNodeIdIriMap, 
            Map<String, SimpleObjectProperty> allSimpleObjectProperties) {
        
        // Iterate over all relationships for this node
        Map<String, String> parentClassIriPropertyRestrictionIriMap = new HashMap<>();
        for ( OntoKaiOntologyNodeRelationship relationship : this.relationships ) {
            String typeTransformed = relationship.getType().strip()
                    .toUpperCase().replace("_", " ");
            
            // No object property restriction
            if ( typeTransformed.equalsIgnoreCase(
                    SUBCLASS_OF_OBJECT_PROPERTY_RDFS_LABEL) )
                parentClassIriPropertyRestrictionIriMap.put(
                        ontoKaiNodeIdIriMap.get(relationship.getParentId()), 
                        null);
            
            // Object property restriction
            else {
                
                if ( relationship.getBundleType() != null && 
                        relationship.getBundleType().equalsIgnoreCase(
                                ONTOKAI_RESTRICTION_BUNDLE_TYPE_VALUE) )
                    parentClassIriPropertyRestrictionIriMap.put(
                            ontoKaiNodeIdIriMap.get(relationship.getParentId()), 
                            allSimpleObjectProperties.containsKey(typeTransformed) ? 
                                    allSimpleObjectProperties.get(typeTransformed)
                                        .getIri() : null);
                
            } 
            
        }
        
        return parentClassIriPropertyRestrictionIriMap;
        
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
