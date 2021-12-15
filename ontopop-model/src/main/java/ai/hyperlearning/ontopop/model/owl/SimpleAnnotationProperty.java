package ai.hyperlearning.ontopop.model.owl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple OWL Model - Annotation Property
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleAnnotationProperty implements Serializable {

	private static final long serialVersionUID = 997348305176979462L;
	
	// Annotation Property IRI
	private String iri;
	
	// Annotation Property Label (RDF schema)
	private String label;
	
	// Map between property IRI and property literal value
	private Map<String, String> annotations = new LinkedHashMap<>();
	
	public SimpleAnnotationProperty() {
		
	}

	public SimpleAnnotationProperty(
			String iri, 
			String label, 
			Map<String, String> annotations) {
		this.iri = iri;
		this.label = label;
		this.annotations = annotations;
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
	
	public void addAnnotation(
			String propertyIRI, String propertyLiteralValue) {
		this.annotations.put(propertyIRI, propertyLiteralValue);
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
		SimpleAnnotationProperty other = (SimpleAnnotationProperty) obj;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleAnnotationProperty ["
				+ "iri=" + iri + ", "
				+ "label=" + label + ", "
				+ "annotations=" + annotations + "]";
	}
	
}
