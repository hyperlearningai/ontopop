package ai.hyperlearning.ontopop.model.owl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple OWL Model - Class
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleClass implements Serializable {

	private static final long serialVersionUID = 6207704698447128772L;

	// Class IRI
	private String iri;
	
	// Class Label (RDF schema)
	private String label;
	
	// Map between property IRI and property literal value
	private Map<String, String> annotations = new LinkedHashMap<>();
	
	// Map between parent class IRI and object property IRI
	private Map<String, String> parentClasses = new LinkedHashMap<>();
	
	public SimpleClass() {
		
	}

	public SimpleClass(
			String iri, 
			String label, 
			Map<String, String> annotations, 
			Map<String, String> parentClasses) {
		this.iri = iri;
		this.label = label;
		this.annotations = annotations;
		this.parentClasses = parentClasses;
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

	public Map<String, String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Map<String, String> annotations) {
		this.annotations = annotations;
	}

	public Map<String, String> getParentClasses() {
		return parentClasses;
	}

	public void setParentClasses(Map<String, String> parentClasses) {
		this.parentClasses = parentClasses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
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
		SimpleClass other = (SimpleClass) obj;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleClass ["
				+ "iri=" + iri + ", "
				+ "label=" + label + ", "
				+ "annotations=" + annotations + ", "
				+ "parentClasses=" + parentClasses + "]";
	}
	
}
