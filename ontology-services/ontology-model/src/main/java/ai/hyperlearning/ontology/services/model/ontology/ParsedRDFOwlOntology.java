package ai.hyperlearning.ontology.services.model.ontology;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple parsed RDF OWL ontology class model
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class ParsedRDFOwlOntology implements Serializable {

	private static final long serialVersionUID = 4070098165937428545L;
	private Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap;
	private Map<String, RDFOwlObjectProperty> owlObjectPropertyMap;
	private Map<String, RDFOwlClass> owlClassMap;
	
	@JsonIgnore
	private Map<Integer, String> owlAnnotationPropertyIdMap;
	
	@JsonIgnore
	private Map<Integer, String> owlObjectPropertyIdMap;
	
	@JsonIgnore
	private Map<Integer, String> owlClassIdMap;
	
	@JsonIgnore
	private Map<Integer, RDFTriple> owlTripleMap;
	
	public ParsedRDFOwlOntology() {
		
	}

	public ParsedRDFOwlOntology(
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap,
			Map<String, RDFOwlObjectProperty> owlObjectPropertyMap, 
			Map<String, RDFOwlClass> owlClassMap) {
		this.owlAnnotationPropertyMap = owlAnnotationPropertyMap;
		generateOwlAnnotationPropertyIdMap();
		this.owlObjectPropertyMap = owlObjectPropertyMap;
		generateOwlObjectPropertyIdMap();
		this.owlClassMap = owlClassMap;
		generateOwlClassIdMap();
		generateOwlTripleMap();
	}

	public Map<String, RDFOwlAnnotationProperty> getOwlAnnotationPropertyMap() {
		return owlAnnotationPropertyMap;
	}

	public void setOwlAnnotationPropertyMap(
			Map<String, RDFOwlAnnotationProperty> owlAnnotationPropertyMap) {
		this.owlAnnotationPropertyMap = owlAnnotationPropertyMap;
		generateOwlAnnotationPropertyIdMap();
	}

	public Map<String, RDFOwlObjectProperty> getOwlObjectPropertyMap() {
		return owlObjectPropertyMap;
	}

	public void setOwlObjectPropertyMap(
			Map<String, RDFOwlObjectProperty> owlObjectPropertyMap) {
		this.owlObjectPropertyMap = owlObjectPropertyMap;
		generateOwlObjectPropertyIdMap();
	}

	public Map<String, RDFOwlClass> getOwlClassMap() {
		return owlClassMap;
	}

	public void setOwlClassMap(Map<String, RDFOwlClass> owlClassMap) {
		this.owlClassMap = owlClassMap;
		generateOwlClassIdMap();
		generateOwlTripleMap();
	}

	public Map<Integer, String> getOwlAnnotationPropertyIdMap() {
		return owlAnnotationPropertyIdMap;
	}

	public void setOwlAnnotationPropertyIdMap(
			Map<Integer, String> owlAnnotationPropertyIdMap) {
		this.owlAnnotationPropertyIdMap = owlAnnotationPropertyIdMap;
	}

	public Map<Integer, String> getOwlObjectPropertyIdMap() {
		return owlObjectPropertyIdMap;
	}

	public void setOwlObjectPropertyIdMap(
			Map<Integer, String> owlObjectPropertyIdMap) {
		this.owlObjectPropertyIdMap = owlObjectPropertyIdMap;
	}

	public Map<Integer, String> getOwlClassIdMap() {
		return owlClassIdMap;
	}

	public void setOwlClassIdMap(Map<Integer, String> owlClassIdMap) {
		this.owlClassIdMap = owlClassIdMap;
	}

	public Map<Integer, RDFTriple> getOwlTripleMap() {
		return owlTripleMap;
	}

	public void setOwlTripleMap(Map<Integer, RDFTriple> owlTripleMap) {
		this.owlTripleMap = owlTripleMap;
	}

	@Override
	public String toString() {
		return "ParsedRDFOwlOntology ["
				+ "owlAnnotationPropertyMap=" + owlAnnotationPropertyMap + ", "
				+ "owlObjectPropertyMap=" + owlObjectPropertyMap + ", "
				+ "owlClassMap=" + owlClassMap + "]";
	}
	
	/**
	 * Generate a map between OWL annotation property ID (generated) 
	 * <> OWL annotation property IRI
	 */
	
	@JsonIgnore
	private void generateOwlAnnotationPropertyIdMap() {
		this.owlAnnotationPropertyIdMap = new HashMap<Integer, String>();
		for (Map.Entry<String, RDFOwlAnnotationProperty> entry : 
			this.owlAnnotationPropertyMap.entrySet()) {
			this.owlAnnotationPropertyIdMap.put(
					entry.getValue().getId(), entry.getKey());
		}
	}
	
	/**
	 * Generate a map between OWL object property ID (generated) 
	 * <> OWL object property IRI
	 */
	
	@JsonIgnore
	private void generateOwlObjectPropertyIdMap() {
		this.owlObjectPropertyIdMap = new HashMap<Integer, String>();
		for (Map.Entry<String, RDFOwlObjectProperty> entry : 
			this.owlObjectPropertyMap.entrySet()) {
			this.owlObjectPropertyIdMap.put(
					entry.getValue().getId(), entry.getKey());
		}
	}
	
	/**
	 * Generate a map between OWL class node ID (generated) <> OWL class IRI
	 */
	
	@JsonIgnore
	private void generateOwlClassIdMap() {
		this.owlClassIdMap = new HashMap<Integer, String>();
		for (Map.Entry<String, RDFOwlClass> entry : 
			this.owlClassMap.entrySet()) {
			this.owlClassIdMap.put(
					entry.getValue().getNodeId(), entry.getKey());
		}
	}
	
	/**
	 * Generate a map of RDF Triples
	 */
	
	@JsonIgnore
	private void generateOwlTripleMap() {
		
		this.owlTripleMap = new HashMap<Integer, RDFTriple>();
		for (Map.Entry<String, RDFOwlClass> entry : 
			this.owlClassMap.entrySet()) {
			
			// Get this current subclass
			RDFOwlClass currentSubClass = entry.getValue();
			
			// Get and iterate the set of superclasses for this
			// current subclass
			Set<RDFSSubClassOf> rdfsSubClassOf = 
					currentSubClass.getRdfsSubClassOf();
			for (RDFSSubClassOf subClassOf : rdfsSubClassOf) {
				
				// Create a new RDFTriple POJO
				RDFTriple rdfTriple = new RDFTriple();
				rdfTriple.setId( subClassOf.getEdgeId() );
				rdfTriple.setSubjectRdfOwlClass(currentSubClass);
				rdfTriple.setPredicateRdfsSubClassOf(subClassOf);
				rdfTriple.setObjectRdfOwlClass(
						this.owlClassMap.get(subClassOf.getClassRdfAbout()));
				
				// Add the new RDFTriple to the map of Triples
				this.owlTripleMap.put(subClassOf.getEdgeId(), rdfTriple);
				
			}
			
		}
		
	}

}
