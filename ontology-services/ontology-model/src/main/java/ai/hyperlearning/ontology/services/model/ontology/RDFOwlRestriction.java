package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;

/**
 * Simple RDF OWL Subclass Restriction model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFOwlRestriction implements Serializable  {

	private static final long serialVersionUID = 6180440375615762801L;
	private String objectPropertyRdfAbout;
	private String objectPropertyRdfsLabel;
	private String classRdfAbout;
	
	public RDFOwlRestriction() {
		
	}

	public RDFOwlRestriction(String objectPropertyRdfAbout, 
			String objectPropertyRdfsLabel, String classRdfAbout) {
		this.objectPropertyRdfAbout = objectPropertyRdfAbout;
		this.objectPropertyRdfsLabel = objectPropertyRdfsLabel;
		this.classRdfAbout = classRdfAbout;
	}

	public String getObjectPropertyRdfAbout() {
		return objectPropertyRdfAbout;
	}

	public void setObjectPropertyRdfAbout(String objectPropertyRdfAbout) {
		this.objectPropertyRdfAbout = objectPropertyRdfAbout;
	}

	public String getObjectPropertyRdfsLabel() {
		return objectPropertyRdfsLabel;
	}

	public void setObjectPropertyRdfsLabel(String objectPropertyRdfsLabel) {
		this.objectPropertyRdfsLabel = objectPropertyRdfsLabel;
	}

	public String getClassRdfAbout() {
		return classRdfAbout;
	}

	public void setClassRdfAbout(String classRdfAbout) {
		this.classRdfAbout = classRdfAbout;
	}

	@Override
	public String toString() {
		return "RDFOwlRestriction ["
				+ "objectPropertyRdfAbout=" + objectPropertyRdfAbout + ", "
				+ "objectPropertyRdfsLabel=" + objectPropertyRdfsLabel + ", "
				+ "classRdfAbout=" + classRdfAbout + "]";
	}
	
}
