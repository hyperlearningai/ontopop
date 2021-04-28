package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Simple RDF Triple model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFTriple implements Serializable {

	private static final long serialVersionUID = 8623095768821272496L;
	private int id;
	
	@JsonIgnoreProperties({ "rdfAbout", "owlAnnotationProperties", "rdfsSubClassOf" })
	private RDFOwlClass subjectRdfOwlClass;
	
	@JsonIgnoreProperties({ "classRdfAbout" })
	private RDFSSubClassOf predicateRdfsSubClassOf;
	
	@JsonIgnoreProperties({ "rdfAbout", "owlAnnotationProperties", "rdfsSubClassOf" })
	private RDFOwlClass objectRdfOwlClass;
	
	public RDFTriple() {
		
	}

	public RDFTriple(int id, RDFOwlClass subjectRdfOwlClass, 
			RDFSSubClassOf predicateRdfsSubClassOf,
			RDFOwlClass objectRdfOwlClass) {
		this.id = id;
		this.subjectRdfOwlClass = subjectRdfOwlClass;
		this.predicateRdfsSubClassOf = predicateRdfsSubClassOf;
		this.objectRdfOwlClass = objectRdfOwlClass;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RDFOwlClass getSubjectRdfOwlClass() {
		return subjectRdfOwlClass;
	}

	public void setSubjectRdfOwlClass(RDFOwlClass subjectRdfOwlClass) {
		this.subjectRdfOwlClass = subjectRdfOwlClass;
	}

	public RDFSSubClassOf getPredicateRdfsSubClassOf() {
		return predicateRdfsSubClassOf;
	}

	public void setPredicateRdfsSubClassOf(
			RDFSSubClassOf predicateRdfsSubClassOf) {
		this.predicateRdfsSubClassOf = predicateRdfsSubClassOf;
	}

	public RDFOwlClass getObjectRdfOwlClass() {
		return objectRdfOwlClass;
	}

	public void setObjectRdfOwlClass(RDFOwlClass objectRdfOwlClass) {
		this.objectRdfOwlClass = objectRdfOwlClass;
	}

	@Override
	public String toString() {
		return "RDFTriple ["
				+ "id=" + id + ", "
				+ "subjectRdfOwlClass=" + subjectRdfOwlClass + ", "
				+ "predicateRdfsSubClassOf=" + predicateRdfsSubClassOf + ", "
				+ "objectRdfOwlClass=" + objectRdfOwlClass + "]";
	}
	
}
