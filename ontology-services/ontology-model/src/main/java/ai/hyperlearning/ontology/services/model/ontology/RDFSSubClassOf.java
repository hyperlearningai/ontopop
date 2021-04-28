package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Simple RDF OWL Subclass model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFSSubClassOf implements Serializable {

	private static final long serialVersionUID = 7470592283782453682L;
	private int edgeId;
	private String classRdfAbout;
	
	@JsonIgnoreProperties({ "classRdfAbout" })
	private RDFOwlRestriction owlRestriction;
	
	public RDFSSubClassOf() {
		
	}

	public RDFSSubClassOf(int edgeId, String classRdfAbout, 
			RDFOwlRestriction owlRestriction) {
		this.edgeId = edgeId;
		this.classRdfAbout = classRdfAbout;
		this.owlRestriction = owlRestriction;
	}

	public int getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(int edgeId) {
		this.edgeId = edgeId;
	}

	public String getClassRdfAbout() {
		return classRdfAbout;
	}

	public void setClassRdfAbout(String classRdfAbout) {
		this.classRdfAbout = classRdfAbout;
	}

	public RDFOwlRestriction getOwlRestriction() {
		return owlRestriction;
	}

	public void setOwlRestriction(RDFOwlRestriction owlRestriction) {
		this.owlRestriction = owlRestriction;
	}

	@Override
	public String toString() {
		return "RDFSSubClassOf ["
				+ "edgeId=" + edgeId + ", "
				+ "classRdfAbout=" + classRdfAbout + ", "
				+ "owlRestriction=" + owlRestriction + "]";
	}

}
