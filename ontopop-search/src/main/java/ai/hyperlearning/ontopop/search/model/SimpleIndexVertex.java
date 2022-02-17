package ai.hyperlearning.ontopop.search.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple Vertex model for indexing
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Document(
        indexName = "#{@environment.getProperty('storage.search.indexNamePrefix')}",
        createIndex = false)
public class SimpleIndexVertex implements Serializable {

    private static final long serialVersionUID = -692044707286693545L;

    @org.springframework.data.annotation.Id
    @Field(type = FieldType.Long)
    private long vertexId;

    @Field(type = FieldType.Text)
    private String label;
    
    @Field(type = FieldType.Text)
    private String iri;

    private Map<String, Object> properties;

    public SimpleIndexVertex() {

    }

    public SimpleIndexVertex(long vertexId, String label,
            Map<String, Object> properties) {
        this.vertexId = vertexId;
        this.label = label;
        this.properties = properties;
        this.setIri();
    }

    public long getVertexId() {
        return vertexId;
    }

    public void setVertexId(long vertexId) {
        this.vertexId = vertexId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }
    
    @JsonIgnore
    public void setIri() {
        if ( this.properties.containsKey("iri") )
            this.iri = this.properties.get("iri").toString();
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
        this.setIri();
    }

    @Override
    public String toString() {
        return "SimpleIndexVertex [" 
                + "vertexId=" + vertexId + ", " 
                + "label=" + label + ", " 
                + "properties=" + properties 
                + "]";
    }

}
