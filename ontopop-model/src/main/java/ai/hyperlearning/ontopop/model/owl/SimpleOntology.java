package ai.hyperlearning.ontopop.model.owl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple OWL Model - Ontology
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntology implements Serializable {

	private static final long serialVersionUID = -2164606179650762875L;
	private int id;
	private long latestGitWebhookId;
	
	// Map between annotation property IRI and Simple Annotation Property objects
	private Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap = new LinkedHashMap<>();
	
	// Map between object property IRI and Simple Object Property objects
	private Map<String, SimpleObjectProperty> simpleObjectPropertyMap = new LinkedHashMap<>();
	
	// Map between class IRI and Simple Class objects
	private Map<String, SimpleClass> simpleClassMap = new LinkedHashMap<>();
	
	public SimpleOntology() {
		
	}
	
	public SimpleOntology(
			int id, 
			long latestGitWebhookId, 
			Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap,
			Map<String, SimpleObjectProperty> simpleObjectPropertyMap, 
			Map<String, SimpleClass> simpleClassMap) {
		this.id = id;
		this.latestGitWebhookId = latestGitWebhookId;
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

	public long getLatestGitWebhookId() {
		return latestGitWebhookId;
	}

	public void setLatestGitWebhookId(long latestGitWebhookId) {
		this.latestGitWebhookId = latestGitWebhookId;
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
	
	@JsonIgnore
	public Set<String> getUniqueSimpleAnnotationPropertyLabels() {
	    Set<String> uniqueSimpleAnnotationPropertyLabels = new HashSet<>();
	    for (SimpleAnnotationProperty simpleAnnotationProperty : 
	        this.simpleAnnotationPropertyMap.values()) {
	        if ( simpleAnnotationProperty.getLabel() != null )
    	        uniqueSimpleAnnotationPropertyLabels.add(
    	                simpleAnnotationProperty.getLabel()
    	                    .strip().toUpperCase());
	    }
	    return uniqueSimpleAnnotationPropertyLabels;
	}
	
	@JsonIgnore
	public Set<String> getUniqueSimpleObjectPropertyLabels() {
	    Set<String> uniqueSimpleObjectPropertyLabels = new HashSet<>();
	    for (SimpleObjectProperty simpleObjectProperty : 
            this.simpleObjectPropertyMap.values()) {
	        if ( simpleObjectProperty.getLabel() != null ) {
	            uniqueSimpleObjectPropertyLabels.add(
	                    simpleObjectProperty.getLabel().strip().toUpperCase()
	                    .replaceAll(" +", " "));
	        }
	    }
	    return uniqueSimpleObjectPropertyLabels;
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
				+ "latestGitWebhookId=" + latestGitWebhookId + ", "
				+ "simpleAnnotationPropertyMap=" + simpleAnnotationPropertyMap + ", "
				+ "simpleObjectPropertyMap=" + simpleObjectPropertyMap + ", "
				+ "simpleClassMap=" + simpleClassMap 
				+ "]";
	}
	
}
