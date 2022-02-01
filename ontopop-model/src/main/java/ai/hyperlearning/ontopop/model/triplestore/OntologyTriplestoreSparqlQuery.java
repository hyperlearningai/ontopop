package ai.hyperlearning.ontopop.model.triplestore;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ontology Triplestore SPARQL Query Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyTriplestoreSparqlQuery implements Serializable {

    private static final long serialVersionUID = -8537802100224127929L;
    
    @Schema(description = "SPARQL query", 
        example = "SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object} LIMIT 25", 
        required = true)
    private String query;
    
    public OntologyTriplestoreSparqlQuery() {
        
    }
    
    public OntologyTriplestoreSparqlQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "OntologyTriplestoreSparqlQuery ["
                + "query=" + query + "]";
    }

}
