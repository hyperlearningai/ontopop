package ai.hyperlearning.ontopop.search.azure.search;

import java.io.Serializable;
import com.azure.search.documents.indexes.SearchableField;
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
    private String vertexId;
    private String label;
    private String rdfsLabel;
    private String iri;
    private int ontologyId;
    private String key;
    private long latestWebhookEventId;

    // Example domain-specific Vertex Properties
    private String definition;
    private String businessArea;
    private String subdomain;
    private String dataSource;
    private String synonym;
    private String example;

    public AzureSimpleIndexVertex() {

    }

    public AzureSimpleIndexVertex(String vertexId) {
        this.vertexId = vertexId;
    }

    // Azure Search only supports string-based ID fields
    @SimpleField(isKey = true, isFilterable = true, isSortable = false)
    public String getVertexId() {
        return vertexId;
    }

    public void setVertexId(String vertexId) {
        this.vertexId = vertexId;
    }

    @SearchableField(isFilterable = true, isSortable = true)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @SearchableField(isFilterable = false, isSortable = false)
    public String getRdfsLabel() {
        return rdfsLabel;
    }

    public void setRdfsLabel(String rdfsLabel) {
        this.rdfsLabel = rdfsLabel;
    }

    @SearchableField(isFilterable = true, isSortable = false)
    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    @SimpleField(isFilterable = true, isSortable = true)
    public int getOntologyId() {
        return ontologyId;
    }

    public void setOntologyId(int ontologyId) {
        this.ontologyId = ontologyId;
    }

    @SearchableField(isFilterable = false, isSortable = false)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @SimpleField(isFilterable = true, isSortable = true)
    public long getLatestWebhookEventId() {
        return latestWebhookEventId;
    }

    public void setLatestWebhookEventId(long latestWebhookEventId) {
        this.latestWebhookEventId = latestWebhookEventId;
    }

    @SearchableField(isFilterable = false, isSortable = false)
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @SearchableField(isFilterable = true, isSortable = true)
    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    @SearchableField(isFilterable = true, isSortable = true)
    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    @SearchableField(isFilterable = true, isSortable = true)
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @SearchableField(isFilterable = false, isSortable = false)
    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    @SearchableField(isFilterable = false, isSortable = false)
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
                + "latestWebhookEventId=" + latestWebhookEventId 
                + "]";
    }

}
