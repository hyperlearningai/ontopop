package ai.hyperlearning.ontopop.search.azure.search;

import java.io.Serializable;

import com.azure.search.documents.indexes.SimpleField;

/**
 * Simple Vertex model for indexing into Azure Search
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class AzureSimpleIndexVertex implements Serializable {

    private static final long serialVersionUID = 5275340446327869288L;
    
    // Common Vertex Properties
    
    // Azure Search only supports string-based ID fields
    @SimpleField(isKey = true, isFilterable = true, isSortable = false)
    private String vertexId;
    
    @SimpleField(isFilterable = true, isSortable = true)
    private String label;
    
    @SimpleField(isFilterable = false, isSortable = false)
    private String rdfsLabel;
    
    @SimpleField(isFilterable = true, isSortable = false)
    private String iri;
    
    @SimpleField(isFilterable = false, isSortable = false)
    private int ontologyId;
    
    @SimpleField(isFilterable = false, isSortable = false)
    private String key;
    
    @SimpleField(isFilterable = false, isSortable = false)
    private long latestWebhookEventId;
    
    // Example domain-specific Vertex Properties
    
    @SimpleField(isFilterable = false, isSortable = false)
    private String definition;
    
    @SimpleField(isFilterable = true, isSortable = true)
    private String businessArea;
    
    @SimpleField(isFilterable = true, isSortable = true)
    private String subdomain;
    
    @SimpleField(isFilterable = true, isSortable = true)
    private String dataSource;
    
    @SimpleField(isFilterable = false, isSortable = false)
    private String synonym;
    
    @SimpleField(isFilterable = false, isSortable = false)
    private String example;
    
    public AzureSimpleIndexVertex() {
        
    }

    public String getVertexId() {
        return vertexId;
    }

    public void setVertexId(String vertexId) {
        this.vertexId = vertexId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRdfsLabel() {
        return rdfsLabel;
    }

    public void setRdfsLabel(String rdfsLabel) {
        this.rdfsLabel = rdfsLabel;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public int getOntologyId() {
        return ontologyId;
    }

    public void setOntologyId(int ontologyId) {
        this.ontologyId = ontologyId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLatestWebhookEventId() {
        return latestWebhookEventId;
    }

    public void setLatestWebhookEventId(long latestWebhookEventId) {
        this.latestWebhookEventId = latestWebhookEventId;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((vertexId == null) ? 0 : vertexId.hashCode());
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
        AzureSimpleIndexVertex other = (AzureSimpleIndexVertex) obj;
        if (vertexId == null) {
            if (other.vertexId != null)
                return false;
        } else if (!vertexId.equals(other.vertexId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AzureSimpleIndexVertex ["
                + "vertexId=" + vertexId + ", "
                + "label=" + label + ", "
                + "rdfsLabel=" + rdfsLabel + ", "
                + "iri=" + iri + ", "
                + "ontologyId=" + ontologyId + ", "
                + "key=" + key + ", "
                + "latestWebhookEventId=" + latestWebhookEventId + "]";
    }
    
}
