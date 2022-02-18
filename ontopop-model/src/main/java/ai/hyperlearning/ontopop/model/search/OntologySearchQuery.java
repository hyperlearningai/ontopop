package ai.hyperlearning.ontopop.model.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Search Query Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */
public class OntologySearchQuery implements Serializable {

    private static final long serialVersionUID = -6683211769320557901L;
    private static final String FIELD_PREFIX = "properties.";
    
    @Schema(description = "Free-text search query", 
            example = "network incident", 
            required = true)
    private String query;
    
    @Schema(description = "List of fields to search. If this is not "
            + "provided, then all fields will be searched across by default.", 
            example = "['label', 'properties.definition', 'properties.description']", 
            required = false)
    private List<String> fields = new ArrayList<>();
    
    @Schema(description = "Whether to use AND boolean logic when a list of "
            + "fields to search has been provided. If this is set to "
            + "false or not provided, then OR boolean logic will be used by "
            + "default. If no list of fields is provided, then this property "
            + "is ignored completely.", 
            example = "false", 
            required = false)
    private boolean and = false;
    
    @Schema(description = "Whether to use exact matching (enabled by default). "
            + "If this is set to false, then fuzzy matching will be used. "
            + "Note that currently fuzzy matching is only used when exactly "
            + "one field has been provided to search. If no list of fields is "
            + "provided, or more than one field is provided, then currently "
            + "exact matching will be used regardless of this property.", 
            example = "true", 
            required = false)
    private boolean exact = true;
    
    public OntologySearchQuery() {
        
    }

    public OntologySearchQuery(String query, List<String> fields, boolean and,
            boolean exact) {
        super();
        this.query = query;
        this.fields = fields;
        this.and = and;
        this.exact = exact;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
    
    /**
     * The only top-level properties in the SearchIndexVertex.class model are
     * vertexId and label (graph label as opposed to RDF label). All other
     * properties are stored in the "properties" hash map. If Elasticsearch
     * is used as the search service, then the "properties" hash map is
     * indexed as a nested object i.e. "properties.x", for example
     * "properties.iri" and "properties.label" etc. Thus if a list of fields 
     * to search is provided, then we need to prefix each field key with 
     * "properties" if it has not already been prefixed.
     */
    
    @JsonIgnore
    public void prefixFields() {
        for (int i = 0; i < this.fields.size(); i++) {
            String currentFieldKey = this.fields.get(i);
            if ( !currentFieldKey.startsWith(FIELD_PREFIX) )
                this.fields.set(i, FIELD_PREFIX + currentFieldKey);
        }
    }

    public boolean isAnd() {
        return and;
    }

    public void setAnd(boolean and) {
        this.and = and;
    }

    public boolean isExact() {
        return exact;
    }

    public void setExact(boolean exact) {
        this.exact = exact;
    }

    @Override
    public String toString() {
        return "OntologySearchQuery ["
                + "query=" + query + ", "
                + "fields=" + fields + ", "
                + "and=" + and + ", "
                + "exact=" + exact 
                + "]";
    }
    
}
