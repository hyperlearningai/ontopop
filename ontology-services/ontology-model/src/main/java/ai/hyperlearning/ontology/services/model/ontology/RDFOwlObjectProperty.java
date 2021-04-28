package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Simple RDF OWL ObjectProperty model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFOwlObjectProperty implements Serializable {

	private static final long serialVersionUID = 4931581433953604113L;
	private int id;
	private String rdfAbout;
	private String rdfsLabel;
	private String skosDefinition;
	private String skosComment;
	private Map<String, String> owlAnnotationProperties;
	private Set<String> rdfsSubPropertyOf;
	
	public RDFOwlObjectProperty() {
		
	}

	public RDFOwlObjectProperty(int id, String rdfAbout, String rdfsLabel, 
			String skosDefinition, String skosComment, 
			Map<String, String> owlAnnotationProperties, 
			Set<String> rdfsSubPropertyOf) {
		this.id = id;
		this.rdfAbout = rdfAbout;
		this.rdfsLabel = rdfsLabel;
		this.skosDefinition = skosDefinition;
		this.skosComment = skosComment;
		this.owlAnnotationProperties = owlAnnotationProperties;
		this.rdfsSubPropertyOf = rdfsSubPropertyOf;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRdfAbout() {
		return rdfAbout;
	}

	public void setRdfAbout(String rdfAbout) {
		this.rdfAbout = rdfAbout;
	}

	public String getRdfsLabel() {
		return rdfsLabel;
	}

	public void setRdfsLabel(String rdfsLabel) {
		this.rdfsLabel = rdfsLabel;
	}

	public String getSkosDefinition() {
		return skosDefinition;
	}

	public void setSkosDefinition(String skosDefinition) {
		this.skosDefinition = skosDefinition;
	}

	public String getSkosComment() {
		return skosComment;
	}

	public void setSkosComment(String skosComment) {
		this.skosComment = skosComment;
	}

	public Map<String, String> getOwlAnnotationProperties() {
		return owlAnnotationProperties;
	}

	public void setOwlAnnotationProperties(
			Map<String, String> owlAnnotationProperties) {
		this.owlAnnotationProperties = owlAnnotationProperties;
	}

	public Set<String> getRdfsSubPropertyOf() {
		return rdfsSubPropertyOf;
	}

	public void setRdfsSubPropertyOf(Set<String> rdfsSubPropertyOf) {
		this.rdfsSubPropertyOf = rdfsSubPropertyOf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rdfAbout == null) ? 0 : 
			rdfAbout.hashCode());
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
		RDFOwlObjectProperty other = (RDFOwlObjectProperty) obj;
		if (rdfAbout == null) {
			if (other.rdfAbout != null)
				return false;
		} else if (!rdfAbout.equals(other.rdfAbout))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RDFOwlObjectProperty ["
				+ "id=" + id + ", "
				+ "rdfAbout=" + rdfAbout + ", "
				+ "rdfsLabel=" + rdfsLabel + ", "
				+ "skosDefinition=" + skosDefinition + ", "
				+ "skosComment=" + skosComment + ", "
				+ "owlAnnotationProperties=" + owlAnnotationProperties + ", "
				+ "rdfsSubPropertyOf=" + rdfsSubPropertyOf + "]";
	}
	
}
