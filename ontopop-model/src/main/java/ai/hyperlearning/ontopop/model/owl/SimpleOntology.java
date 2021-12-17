package ai.hyperlearning.ontopop.model.owl;

import java.io.Serializable;
import java.util.Map;

/**
 * Simple OWL Model - Ontology
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntology implements Serializable {

	private static final long serialVersionUID = -2164606179650762875L;
	private int id;
	
	// Map between annotation property IRI and Simple Annotation Property objects
	private Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap;
	
	// Map between object property IRI and Simple Object Property objects
	private Map<String, SimpleObjectProperty> simpleObjectPropertyMap;
	
	// Map between class IRI and Simple Class objects
	private Map<String, SimpleClass> simpleClassMap;
	
	public SimpleOntology() {
		
	}
	
	public SimpleOntology(
			int id, 
			Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap,
			Map<String, SimpleObjectProperty> simpleObjectPropertyMap, 
			Map<String, SimpleClass> simpleClassMap) {
		this.id = id;
		this.simpleAnnotationPropertyMap = simpleAnnotationPropertyMap;
		this.simpleObjectPropertyMap = simpleObjectPropertyMap;
		this.simpleClassMap = simpleClassMap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Map<String, SimpleAnnotationProperty> getSimpleAnnotationPropertyMap() {
		return simpleAnnotationPropertyMap;
	}

	public void setSimpleAnnotationPropertyMap(
			Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap) {
		this.simpleAnnotationPropertyMap = simpleAnnotationPropertyMap;
	}

	public Map<String, SimpleObjectProperty> getSimpleObjectPropertyMap() {
		return simpleObjectPropertyMap;
	}

	public void setSimpleObjectPropertyMap(
			Map<String, SimpleObjectProperty> simpleObjectPropertyMap) {
		this.simpleObjectPropertyMap = simpleObjectPropertyMap;
	}

	public Map<String, SimpleClass> getSimpleClassMap() {
		return simpleClassMap;
	}

	public void setSimpleClassMap(Map<String, SimpleClass> simpleClassMap) {
		this.simpleClassMap = simpleClassMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		SimpleOntology other = (SimpleOntology) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleOntology ["
				+ "id=" + id + ", "
				+ "simpleAnnotationPropertyMap=" + simpleAnnotationPropertyMap + ", "
				+ "simpleObjectPropertyMap=" + simpleObjectPropertyMap + ", "
				+ "simpleClassMap=" + simpleClassMap 
				+ "]";
	}
	
}
