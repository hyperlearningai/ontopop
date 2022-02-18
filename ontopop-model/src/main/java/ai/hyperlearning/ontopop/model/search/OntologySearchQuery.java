package ai.hyperlearning.ontopop.model.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Search Query Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */
public class OntologySearchQuery implements Serializable {

    private static final long serialVersionUID = -6683211769320557901L;
    
    @Schema(description = "Free-text search query", 
            example = "network incident", 
            required = true)
    private String query;
    
    @Schema(description = "List of fields to search across. If this is not provided, then all fields will be searched across by default.", 
            example = "['label', 'properties.definition', 'properties.description']", 
            required = false)
    private List<String> fields = new ArrayList<>();
    
    @Schema(description = "Whether to use AND boolean logic. If this is set to false or not provided, then OR boolean logic will be used by default.", 
            example = "false", 
            required = false)
    private boolean and = false;
    
    @Schema(description = "Whether to use exact matching (enabled by default). If this is set to false, then fuzzy matching will be used.", 
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
