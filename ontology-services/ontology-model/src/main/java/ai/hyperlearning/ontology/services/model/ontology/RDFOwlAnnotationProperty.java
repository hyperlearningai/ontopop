package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Simple RDF OWL AnnotationProperty model for JAXB Unmarhsalling
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFOwlAnnotationProperty implements Serializable {

	private static final long serialVersionUID = 2013819416482110098L;

	private int id;
	
	@XmlAttribute(
			namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#",
			name="about")
	private String rdfAbout;
	
	@XmlElement(
			namespace="http://www.w3.org/2000/01/rdf-schema#",
			name="label")
	private String rdfsLabel;
	
	@XmlElement(
			namespace="http://www.w3.org/2004/02/skos/core#",
			name="comment")
	private String skosComment;
	
	@XmlElement(
			namespace="http://www.w3.org/2004/02/skos/core#",
			name="definition")
	private String skosDefinition;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRdfAbout() {
		return rdfAbout;
	}

	public void setRdfAboutManually(String rdfAbout) {
		this.rdfAbout = rdfAbout;
	}

	public String getRdfsLabel() {
		return rdfsLabel;
	}
	
	public void setRdfsLabelManually(String rdfsLabel) {
		this.rdfsLabel = rdfsLabel;
	}

	public String getSkosComment() {
		return skosComment;
	}
	
	public void setSkosCommentManually(String skosComment) {
		this.skosComment = skosComment;
	}

	public String getSkosDefinition() {
		return skosDefinition;
	}
	
	public void setSkosDefinitionManually(String skosDefinition) {
		this.skosDefinition = skosDefinition;
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
		RDFOwlAnnotationProperty other = (RDFOwlAnnotationProperty) obj;
		if (rdfAbout == null) {
			if (other.rdfAbout != null)
				return false;
		} else if (!rdfAbout.equals(other.rdfAbout))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RDFOwlAnnotationProperty ["
				+ "id=" + id + ", "
				+ "rdfAbout=" + rdfAbout + ", "
				+ "rdfsLabel=" + rdfsLabel + ", "
				+ "skosComment=" + skosComment + ", "
				+ "skosDefinition=" + skosDefinition + "]";
	}
	
}
