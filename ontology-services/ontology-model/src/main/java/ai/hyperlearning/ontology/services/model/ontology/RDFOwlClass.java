package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple RDF OWL Class model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class RDFOwlClass implements Serializable {

	private static final long serialVersionUID = -5620325482654809093L;
	private static final Set<String> UPPER_ONTOLOGY_CLASSES = Stream.of(
			"IMMATERIAL CONTINUANT", 
			"MATERIAL CONTINUANT", 
			"OCCURRENT").collect(Collectors.toCollection(HashSet::new));
	private int nodeId;
	private String rdfAbout;
	private String rdfsLabel;
	private boolean upperOntology = false;
	private Map<String, String> owlAnnotationProperties;
	private Set<RDFSSubClassOf> rdfsSubClassOf;
	
	public RDFOwlClass() {
		
	}

	public RDFOwlClass(int nodeId, String rdfAbout, String rdfsLabel, 
			Map<String, String> owlAnnotationProperties, 
			Set<RDFSSubClassOf> rdfsSubClassOf) {
		this.nodeId = nodeId;
		this.rdfAbout = rdfAbout;
		this.rdfsLabel = rdfsLabel;
		this.owlAnnotationProperties = owlAnnotationProperties;
		this.rdfsSubClassOf = rdfsSubClassOf;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
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
		if ( rdfsLabel != null )
			if ( UPPER_ONTOLOGY_CLASSES.contains(
					rdfsLabel.toUpperCase().trim()) )
				this.upperOntology = true;
	}

	public boolean isUpperOntology() {
		return upperOntology;
	}

	public void setUpperOntology(boolean upperOntology) {
		this.upperOntology = upperOntology;
	}

	public Map<String, String> getOwlAnnotationProperties() {
		return owlAnnotationProperties;
	}

	public void setOwlAnnotationProperties(
			Map<String, String> owlAnnotationProperties) {
		this.owlAnnotationProperties = owlAnnotationProperties;
	}

	public Set<RDFSSubClassOf> getRdfsSubClassOf() {
		return rdfsSubClassOf;
	}

	public void setRdfsSubClassOf(Set<RDFSSubClassOf> rdfsSubClassOf) {
		this.rdfsSubClassOf = rdfsSubClassOf;
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
		RDFOwlClass other = (RDFOwlClass) obj;
		if (rdfAbout == null) {
			if (other.rdfAbout != null)
				return false;
		} else if (!rdfAbout.equals(other.rdfAbout))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RDFOwlClass ["
				+ "nodeId=" + nodeId + ", "
				+ "rdfAbout=" + rdfAbout + ", "
				+ "rdfsLabel=" + rdfsLabel + ", "
				+ "upperOntology=" + upperOntology + ", "
				+ "owlAnnotationProperties=" + owlAnnotationProperties + ", "
				+ "rdfsSubClassOf" + rdfsSubClassOf + "]";
	}
	
	/**
	 * Generate a property map for the OWL class
	 * @return
	 */
	
	@JsonIgnore
	public Map<String, Object> getPropertyMap() {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("nodeId", getNodeId());
		propertyMap.put("rdfAbout", getRdfAbout());
		propertyMap.put("rdfsLabel", getRdfsLabel());
		propertyMap.put("upperOntology", isUpperOntology());
		for (Map.Entry<String, String> annotationPropertyEntry 
				: getOwlAnnotationProperties().entrySet()) {
			propertyMap.put(annotationPropertyEntry.getKey(),
					annotationPropertyEntry.getValue());
		}
		return propertyMap;
	}
	
	/**
	 * Generate a property map for the OWL class, where the property key
	 * is the annotation property RDFS label if is it not-null
	 * @param owlAnnotationPropertyMap
	 * @return
	 */
	
	@JsonIgnore
	public Map<String, Object> getPropertyMap(
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap) {
		
		// Instantiate a property map for property key RDFS label <> value
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("nodeId", getNodeId());
		propertyMap.put("rdfAbout", getRdfAbout());
		propertyMap.put("rdfsLabel", getRdfsLabel());
		propertyMap.put("upperOntology", isUpperOntology());
		propertyMap.put("name", getRdfsLabel());
		
		// Iterate over the annotation property map
		for (Map.Entry<String, String> annotationPropertyEntry 
				: getOwlAnnotationProperties().entrySet()) {
			
			// Get the annotation property RDFS label
			if (owlAnnotationPropertyMap.containsKey(
					annotationPropertyEntry.getKey())) {
				
				// Get the annotation property RDFS label
				String annotationPropertyRdfsLabel = 
						owlAnnotationPropertyMap.get(
								annotationPropertyEntry.getKey())
						.getRdfsLabel();
				
				// Create a mapping between the annotation property RDFS
				// label and the property value for this OWL class
				if (annotationPropertyRdfsLabel != null)
					propertyMap.put(annotationPropertyRdfsLabel,
							annotationPropertyEntry.getValue());
				else
					propertyMap.put(annotationPropertyEntry.getKey(),
							annotationPropertyEntry.getValue());
				
				// Populate the list of synonyms with any synonyms created
				// in webprotege or similar (i.e. in the origina ontology)
				if (annotationPropertyRdfsLabel.equalsIgnoreCase("SYNONYM")) {
					
					// Create an initial list from these synonyms
					List<String> synonyms =  Arrays.asList(
							annotationPropertyEntry.getValue().split(",", -1));
					propertyMap.put("synonyms", synonyms.toString());
					propertyMap.remove(annotationPropertyRdfsLabel);
					
				}
				
			} else
				propertyMap.put(annotationPropertyEntry.getKey(),
						annotationPropertyEntry.getValue());
		}
		return propertyMap;
	}

}
