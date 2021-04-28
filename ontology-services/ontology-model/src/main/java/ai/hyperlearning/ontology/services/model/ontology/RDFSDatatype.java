package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * RDFS Datatype Class for JAXB Unmarhsalling
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFSDatatype implements Serializable {

	private static final long serialVersionUID = -3571549150606758100L;
	
	@XmlAttribute(
			namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#",
			name="about")
	private String rdfAbout;
	
	@XmlElement(
			namespace="http://www.w3.org/2000/01/rdf-schema#",
			name="label")
	private String rdfsLabel;

	public String getRdfAbout() {
		return rdfAbout;
	}

	public String getRdfsLabel() {
		return rdfsLabel;
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
		RDFSDatatype other = (RDFSDatatype) obj;
		if (rdfAbout == null) {
			if (other.rdfAbout != null)
				return false;
		} else if (!rdfAbout.equals(other.rdfAbout))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RDFSDatatype ["
				+ "rdfAbout=" + rdfAbout + ", "
				+ "rdfsLabel=" + rdfsLabel + "]";
	}

}
