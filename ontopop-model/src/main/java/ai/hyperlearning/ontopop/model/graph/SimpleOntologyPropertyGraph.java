package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.CaseUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;

/**
 * Simple Directed Property Graph model representation of an OWL Ontology
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyPropertyGraph implements Serializable {
	
	private static final long serialVersionUID = 4551428047838876163L;
	
	// One-to-one mapping between Ontology ID and Property Graph ID
	private int id;
	
	// The latest webhook event ID so that it can be attached as a property
	// to vertices and edges when they are updated
	private long latestWebhookEventId;
	
	// Map of resolved Simple Ontology Vertex Key (IRI + Ontology ID) <> 
	// Simple Ontology Vertex objects which are a one-to-one mapping of OWL classes
	private Map<String, SimpleOntologyVertex> vertices = new LinkedHashMap<>();
	
	// List of resolved Simple Ontology Edge objects which are a one-to-one
	// mapping of OWL subclass relationships
	private List<SimpleOntologyEdge> edges = new ArrayList<>();
	
	public SimpleOntologyPropertyGraph() {
		
	}

	public SimpleOntologyPropertyGraph(
			int id, 
			long latestWebhookEventId, 
			Map<String, SimpleOntologyVertex> vertices, 
			List<SimpleOntologyEdge> edges) {
		this.id = id;
		this.latestWebhookEventId = latestWebhookEventId;
		this.vertices = vertices;
		this.edges = edges;
	}
	
	/**
	 * Transform a SimpleOntology object into a 
	 * SimpleOntologyPropertyGraph object
	 * @param id
	 * @param latestWebhookEventId
	 * @param simpleOntology
	 * @param standardSchemaAnnotationProperties
	 */
	
	public SimpleOntologyPropertyGraph(
			int id,
			long latestWebhookEventId, 
			SimpleOntology simpleOntology, 
			Map<String, SimpleAnnotationProperty> standardSchemaAnnotationProperties) {
		
		// Set the IDs
		this.id = id;
		this.latestWebhookEventId = latestWebhookEventId;
		
		// Resolve and set the simple ontology vertex objects
		resolveAndSetVertices(simpleOntology, 
				standardSchemaAnnotationProperties);
		
		// Resolve and set the simple ontology edge objects
		resolveAndSetEdges(simpleOntology);
		
	}
	
	/**
	 * Resolve and set the simple ontology vertex objects which are a 
	 * one-to-one mapping of OWL classes
	 * @param simpleOntology
	 * @param standardSchemaAnnotationProperties
	 */
	
	@JsonIgnore
	public void resolveAndSetVertices(
			SimpleOntology simpleOntology, 
			Map<String, SimpleAnnotationProperty> standardSchemaAnnotationProperties) {
		
		// Aggregate the simple annotation property objects into a single map
		Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap = 
				new LinkedHashMap<>(
						simpleOntology.getSimpleAnnotationPropertyMap());
		simpleAnnotationPropertyMap.putAll(standardSchemaAnnotationProperties);
		
		// Iterate over the simple class map from the simple ontology
		Map<String, SimpleClass> simpleClassMap = simpleOntology.getSimpleClassMap();
		for (var simpleClassMapEntry : simpleClassMap.entrySet()) {
			String owlClassIRI = simpleClassMapEntry.getKey();
			SimpleClass owlClass = simpleClassMapEntry.getValue();
			
			// Create a new simple ontology vertex object for each OWL class
			SimpleOntologyVertex vertex = new SimpleOntologyVertex();
			vertex.setIri(owlClassIRI);
			vertex.setOntologyId(this.id);
			vertex.setLatestWebhookEventId(this.latestWebhookEventId);
			Map<String, Object> vertexProperties = new LinkedHashMap<>();
			
			// Resolve annotations for this vertex
			Map<String, String> owlClassAnnotations = owlClass.getAnnotations();
			for (var owlClassAnnotationsEntry : owlClassAnnotations.entrySet()) {
				String annotationIRI = owlClassAnnotationsEntry.getKey();
				String annotationValue = owlClassAnnotationsEntry.getValue();
				String annotationLabel = 
						simpleAnnotationPropertyMap.containsKey(annotationIRI) ? 
								CaseUtils.toCamelCase(
										simpleAnnotationPropertyMap
											.get(annotationIRI).getLabel(), 
										false, ' ') :
								annotationIRI;
				vertexProperties.put(
						annotationLabel != null ? 
								annotationLabel : annotationIRI, 
						annotationValue);
			}
			
			// Add the new simple ontology vertex to the set of vertices
			vertex.setProperties(vertexProperties);
			this.vertices.put(vertex.getKey(), vertex);
			
		}
		
	}
	
	/**
	 * Resolve and set the simple ontology edge objects which are a
	 * one-to-one mapping of OWL sub class of relationships 
	 * @param simpleOntology
	 */
	
	@JsonIgnore
	public void resolveAndSetEdges(SimpleOntology simpleOntology) {
		
		// Iterate over the simple class map from the simple ontology
		Map<String, SimpleClass> simpleClassMap = simpleOntology.getSimpleClassMap();
		for (var simpleClassMapEntry : simpleClassMap.entrySet()) {
			String owlClassIRI = simpleClassMapEntry.getKey();
			SimpleClass owlClass = simpleClassMapEntry.getValue();
			
			// Get the previously resolved source vertex
			String sourceVertexKey = owlClassIRI 
					+ SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
					+ this.id;
			SimpleOntologyVertex sourceVertex = this.vertices.containsKey(sourceVertexKey) ? 
					vertices.get(sourceVertexKey) : null;
			if ( sourceVertex != null ) {
				
				// Resolve relationships for this vertex
				Map<String, String> owlClassParents = owlClass.getParentClasses();
				for (var owlClassParentsEntry : owlClassParents.entrySet()) {
					String parentClassIRI = owlClassParentsEntry.getKey();
					String objectPropertyIRI = owlClassParentsEntry.getValue();
					
					// Get the previously resolved target vertex
					String targetVertexKey = parentClassIRI 
							+ SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
							+ this.id;
					SimpleOntologyVertex targetVertex = this.vertices.containsKey(targetVertexKey) ? 
							vertices.get(targetVertexKey) : null;
					if ( targetVertex != null ) {
						
						// Create a new simple ontology edge object for each 
						// OWL class parent relationship
						SimpleOntologyEdge edge = new SimpleOntologyEdge();
						edge.setSourceVertexKey(sourceVertex.getKey());
						edge.setSourceVertexId(sourceVertex.getVertexId());
						edge.setTargetVertexKey(targetVertex.getKey());
						edge.setTargetVertexId(targetVertex.getVertexId());
						edge.setOntologyId(id);
						edge.setLatestWebhookEventId(latestWebhookEventId);
						Map<String, Object> edgeProperties = new LinkedHashMap<>();
						if ( objectPropertyIRI != null ) {
							
							String objectPropertyLabel = simpleOntology
									.getSimpleObjectPropertyMap()
									.containsKey(objectPropertyIRI) ?
											simpleOntology
												.getSimpleObjectPropertyMap()
												.get(objectPropertyIRI)
												.getLabel() : 
											objectPropertyIRI;
							edgeProperties.put(
									SimpleOntologyEdge.RELATIONSHIP_TYPE_KEY, 
									objectPropertyLabel);
							
						}
						
						// Add the new simple ontology edge to the set of edges
						edge.setProperties(edgeProperties);
						this.edges.add(edge);
						
					}
					
				}
				
			}
			
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getLatestWebhookEventId() {
		return latestWebhookEventId;
	}

	public void setLatestWebhookEventId(long latestWebhookEventId) {
		this.latestWebhookEventId = latestWebhookEventId;
	}

	public Map<String, SimpleOntologyVertex> getVertices() {
		return vertices;
	}

	public void setVertices(Map<String, SimpleOntologyVertex> vertices) {
		this.vertices = vertices;
	}
	
	public void addVertex(String key, SimpleOntologyVertex vertex) {
		this.vertices.put(key, vertex);
	}

	public List<SimpleOntologyEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<SimpleOntologyEdge> edges) {
		this.edges = edges;
	}
	
	public void addEdge(SimpleOntologyEdge edge) {
		this.edges.add(edge);
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
		SimpleOntologyPropertyGraph other = (SimpleOntologyPropertyGraph) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleOntologyPropertyGraph ["
				+ "id=" + id + ", "
				+ "latestWebhookEventId=" + latestWebhookEventId + ", "
				+ "vertices=" + vertices + ", "
				+ "edges=" + edges + 
				"]";
	}

}
