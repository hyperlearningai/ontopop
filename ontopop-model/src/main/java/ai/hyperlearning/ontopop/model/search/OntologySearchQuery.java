package ai.hyperlearning.ontopop.model.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ontology Search Query Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */
public class OntologySearchQuery implements Serializable {

    private static final long serialVersionUID = -6683211769320557901L;
    
    private String query;
    private List<String> fields = new ArrayList<>();
    private boolean and = false;
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
