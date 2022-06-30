package ai.hyperlearning.ontopop.model.owl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Simple OWL Model - Named Individual
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleNamedIndividual implements Serializable {

    private static final long serialVersionUID = 6737598935677119690L;
    
    // Named Individual IRI
    private String iri;
    
    // Named Individual Label
    private String label;
    
    // Named Individual Class Types (instance of)
    private Set<String> instanceOfClassIris = new LinkedHashSet<>();
    
    // Map between property IRI and property literal value
    private Map<String, String> annotations = new LinkedHashMap<>();
    
    // Map between linked Named Individual IRIs and object property IRIs
    private Map<String, String> linkedNamedIndividuals = new LinkedHashMap<>();
    
    public SimpleNamedIndividual() {
        
    }

    public SimpleNamedIndividual(String iri, 
            String label, 
            Set<String> instanceOfClassIris,
            Map<String, String> annotations, 
            Map<String, String> linkedNamedIndividuals) {
        this.iri = iri;
        this.label = label;
        this.instanceOfClassIris = instanceOfClassIris;
        this.annotations = annotations;
        this.linkedNamedIndividuals = linkedNamedIndividuals;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<String> getInstanceOfClassIris() {
        return instanceOfClassIris;
    }

    public void setInstanceOfClassIris(Set<String> instanceOfClassIris) {
        this.instanceOfClassIris = instanceOfClassIris;
    }

    public Map<String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    public Map<String, String> getLinkedNamedIndividuals() {
        return linkedNamedIndividuals;
    }

    public void setLinkedNamedIndividuals(
            Map<String, String> linkedNamedIndividuals) {
        this.linkedNamedIndividuals = linkedNamedIndividuals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(iri);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleNamedIndividual other = (SimpleNamedIndividual) obj;
        return Objects.equals(iri, other.iri);
    }

    @Override
    public String toString() {
        return "SimpleNamedIndividual ["
                + "iri=" + iri + ", "
                + "label=" + label + ", "
                + "instanceOfClassIris=" + instanceOfClassIris + ", "
                + "annotations=" + annotations + ", "
                + "linkedNamedIndividuals=" + linkedNamedIndividuals 
                + "]";
    }

}
