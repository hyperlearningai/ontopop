package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RDF Class for JAXB Unmarhsalling
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */
 
@XmlRootElement(
		namespace="http://www.w3.org/1999/02/22-rdf-syntax-ns#",
		name="RDF")
@XmlAccessorType(XmlAccessType.FIELD)
public class RDF implements Serializable {
	
	private static final long serialVersionUID = -2910609822087324659L;
	
	@XmlElement(
			namespace="http://www.w3.org/2002/07/owl#",
			name="AnnotationProperty")
	private List<RDFOwlAnnotationProperty> owlAnnotationProperties;
	
	@XmlElement(
			namespace="http://www.w3.org/2000/01/rdf-schema#",
			name="Datatype")
	private List<RDFSDatatype> rdfsDatatypes;

	public List<RDFOwlAnnotationProperty> getOwlAnnotationProperties() {
		return owlAnnotationProperties;
	}

	public List<RDFSDatatype> getRdfsDatatypes() {
		return rdfsDatatypes;
	}

	@Override
	public String toString() {
		return "RDF [owlAnnotationProperties=" + owlAnnotationProperties + ", "
				+ "rdfsDatatypes=" + rdfsDatatypes + "]";
	}

}