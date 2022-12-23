package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ai.hyperlearning.ontopop.model.owl.SimpleAnnotationProperty;
import ai.hyperlearning.ontopop.model.owl.SimpleClass;
import ai.hyperlearning.ontopop.model.owl.SimpleNamedIndividual;
import ai.hyperlearning.ontopop.model.owl.SimpleOntology;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Simple Directed Property Graph model representation of an OWL Ontology
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class SimpleOntologyPropertyGraph implements Serializable {
	
	private static final long serialVersionUID = 4551428047838876163L;
	private static final String PARENT_CLASS_OBJECT_PROPERTY_DELIMITER = "|";
	
	// One-to-one mapping between Ontology ID and Property Graph ID
	@Schema(description = "Ontology ID", example = "13")
	private int id;
	
	// The latest Git webhook ID so that it can be attached as a property
	// to vertices and edges when they are updated
	@Schema(description = "Latest Git webhook ID for this ontology.", example = "108")
	private long latestGitWebhookId;
	
	// Map of resolved Simple Ontology Vertex Key (IRI + Ontology ID) <> 
	// Simple Ontology Vertex objects which are a one-to-one mapping of OWL classes
	@Schema(description = "Map of OWL classes modelled as property graph vertices.")
	private Map<String, SimpleOntologyVertex> vertices = new LinkedHashMap<>();
	
	// List of resolved Simple Ontology Edge objects which are a one-to-one
	// mapping of OWL subclass relationships
	@Schema(description = "Map of OWL subclass relationships modelled as directed property graph edges.")
	private List<SimpleOntologyEdge> edges = new ArrayList<>();
	
	public SimpleOntologyPropertyGraph() {
		
	}

	public SimpleOntologyPropertyGraph(
			int id, 
			long latestGitWebhookId, 
			Map<String, SimpleOntologyVertex> vertices, 
			List<SimpleOntologyEdge> edges) {
		this.id = id;
		this.latestGitWebhookId = latestGitWebhookId;
		this.vertices = vertices;
		this.edges = edges;
	}
	
	/**
	 * Transform a SimpleOntology object into a 
	 * SimpleOntologyPropertyGraph object
	 * @param id
	 * @param latestGitWebhookId
	 * @param simpleOntology
	 * @param standardSchemaAnnotationProperties
	 */
	
	public SimpleOntologyPropertyGraph(
			int id,
			long latestGitWebhookId, 
			SimpleOntology simpleOntology, 
			Map<String, SimpleAnnotationProperty> standardSchemaAnnotationProperties) {
		
		// Set the IDs
		this.id = id;
		this.latestGitWebhookId = latestGitWebhookId;
		
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
		
		// Resolve class vertices
		resolveAndSetClassVertices(
		        simpleOntology, simpleAnnotationPropertyMap);
		
		// Resolve named individual vertices
		resolveAndSetNamedIndividualVertices(
		        simpleOntology, simpleAnnotationPropertyMap);
		
	}
	
	@JsonIgnore
    private void resolveAndSetClassVertices(SimpleOntology simpleOntology, 
            Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap) {
	    
	    // Iterate over the simple class map from the simple ontology
        Map<String, SimpleClass> simpleClassMap = 
                simpleOntology.getSimpleClassMap();
        for (var simpleClassMapEntry : simpleClassMap.entrySet()) {
            String owlClassIRI = simpleClassMapEntry.getKey();
            SimpleClass owlClass = simpleClassMapEntry.getValue();
            
            // Create a new simple ontology vertex object for each OWL class
            SimpleOntologyVertex vertex = new SimpleOntologyVertex();
            vertex.setIri(owlClassIRI);
            vertex.setLabel(SimpleOntologyVertexLabel.CLASS.toString());
            vertex.setOntologyId(this.id);
            vertex.setLatestGitWebhookId(this.latestGitWebhookId);
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
            this.vertices.put(vertex.getVertexKey(), vertex);
            
        }
        
	}
	
	@JsonIgnore
    private void resolveAndSetNamedIndividualVertices(
            SimpleOntology simpleOntology, 
            Map<String, SimpleAnnotationProperty> simpleAnnotationPropertyMap) {
	    
	    // Iterate over the simple named individual map from the simple ontology
        Map<String, SimpleNamedIndividual> simpleNamedIndividualMap = 
                simpleOntology.getSimpleNamedIndividualMap();
        for (var simpleNamedIndividualMapEntry : 
            simpleNamedIndividualMap.entrySet()) {
            String namedIndividualIRI = 
                    simpleNamedIndividualMapEntry.getKey();
            SimpleNamedIndividual namedIndividual = 
                    simpleNamedIndividualMapEntry.getValue();
            
            // Create a new simple ontology vertex object for each named individual
            SimpleOntologyVertex vertex = new SimpleOntologyVertex();
            vertex.setIri(namedIndividualIRI);
            vertex.setLabel(SimpleOntologyVertexLabel.NAMED_INDIVIDUAL.toString());
            vertex.setOntologyId(this.id);
            vertex.setLatestGitWebhookId(this.latestGitWebhookId);
            Map<String, Object> vertexProperties = new LinkedHashMap<>();
            
            // Resolve annotations for this named individual
            Map<String, String> namedIndividualAnnotations = 
                    namedIndividual.getAnnotations();
            for (var namedIndividualAnnotationsEntry : 
                namedIndividualAnnotations.entrySet()) {
                String annotationIRI = namedIndividualAnnotationsEntry.getKey();
                String annotationValue = namedIndividualAnnotationsEntry.getValue();
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
            this.vertices.put(vertex.getVertexKey(), vertex);
            
        }
	    
	}
	
	/**
	 * Resolve and set the simple ontology edge objects which are a
	 * one-to-one mapping of OWL sub class of relationships 
	 * @param simpleOntology
	 */
	
	@JsonIgnore
	public void resolveAndSetEdges(SimpleOntology simpleOntology) {
		
		// Resolve class edges
	    resolveAndSetClassEdges(simpleOntology);
	    
	    // Resolve named individual edges
	    resolveAndSetNamedIndividualEdges(simpleOntology);
		
	}
	
	@JsonIgnore
    private void resolveAndSetClassEdges(SimpleOntology simpleOntology) {
	    
	    // Iterate over the simple class map from the simple ontology
        Map<String, SimpleClass> simpleClassMap = simpleOntology
                .getSimpleClassMap();
        for (var simpleClassMapEntry : simpleClassMap.entrySet()) {
            String owlClassIRI = simpleClassMapEntry.getKey();
            SimpleClass owlClass = simpleClassMapEntry.getValue();
            
            // Get the previously resolved source vertex
            String sourceVertexKey = owlClassIRI 
                    + SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
                    + this.id;
            SimpleOntologyVertex sourceVertex = this.vertices
                    .containsKey(sourceVertexKey) ? 
                    vertices.get(sourceVertexKey) : null;
            if ( sourceVertex != null ) {
                
                // Resolve relationships for this vertex
                Map<String, String> owlClassParents = owlClass
                        .getParentClasses();
                for (var owlClassParentsEntry : owlClassParents.entrySet()) {
                    String parentClassIRI = owlClassParentsEntry.getKey();
                    String objectPropertyIRI = owlClassParentsEntry.getValue();
                    
                    // Get the previously resolved target vertex
                    String targetVertexKey = parentClassIRI 
                            + SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
                            + this.id;
                    SimpleOntologyVertex targetVertex = this.vertices
                            .containsKey(targetVertexKey) ? 
                            vertices.get(targetVertexKey) : null;
                    if ( targetVertex != null ) {
                        
                        // Create a new simple ontology edge object for each 
                        // OWL class parent relationship
                        SimpleOntologyEdge edge = new SimpleOntologyEdge();
                        edge.setLabel(SimpleOntologyEdgeLabel.SUB_CLASS_OF.toString());
                        edge.setSourceVertexKey(sourceVertex.getVertexKey());
                        edge.setSourceVertexId(sourceVertex.getVertexId());
                        edge.setTargetVertexKey(targetVertex.getVertexKey());
                        edge.setTargetVertexId(targetVertex.getVertexId());
                        edge.setOntologyId(id);
                        edge.setLatestGitWebhookId(latestGitWebhookId);
                        
                        // Set a default relationship description
                        Map<String, Object> edgeProperties = new LinkedHashMap<>();
                        edgeProperties.put(
                                SimpleOntologyEdge.RELATIONSHIP_TYPE_KEY, 
                                SimpleOntologyEdgeLabel.SUB_CLASS_OF.toString());
                        
                        // Resolve the relationship description
                        if ( objectPropertyIRI != null ) {
                            
                            // There may be many object property IRIs
                            // delimited by the | symbol
                            List<String> objectPropertyIRIs = 
                                    Arrays.asList(objectPropertyIRI
                                            .replace(" " + PARENT_CLASS_OBJECT_PROPERTY_DELIMITER + " ", 
                                                    PARENT_CLASS_OBJECT_PROPERTY_DELIMITER)
                                            .split("\\" + PARENT_CLASS_OBJECT_PROPERTY_DELIMITER));
                            StringJoiner objectPropertyLabel = new StringJoiner(
                                    " " + PARENT_CLASS_OBJECT_PROPERTY_DELIMITER + " ");
                            for (String currentObjectPropertyIRI : 
                                objectPropertyIRIs) {
                                objectPropertyLabel.add(simpleOntology
                                        .getSimpleObjectPropertyMap()
                                        .containsKey(currentObjectPropertyIRI) ? 
                                                simpleOntology
                                                .getSimpleObjectPropertyMap()
                                                .get(currentObjectPropertyIRI)
                                                .getLabel() : 
                                                    currentObjectPropertyIRI);
                            }
                            edgeProperties.put(
                                    SimpleOntologyEdge.RELATIONSHIP_TYPE_KEY, 
                                    objectPropertyLabel.toString());
                            
                        }
                        
                        // Add the new simple ontology edge to the set of edges
                        edge.setProperties(edgeProperties);
                        this.edges.add(edge);
                        
                    }
                    
                }
                
            }
            
        }
	    
	}
	
	@JsonIgnore
    private void resolveAndSetNamedIndividualEdges(
            SimpleOntology simpleOntology) {
	    
	    // Iterate over the simple named individual map from the simple ontology
        Map<String, SimpleNamedIndividual> simpleNamedIndividualMap = 
                simpleOntology.getSimpleNamedIndividualMap();
        for (var simpleNamedIndividualMapEntry : 
            simpleNamedIndividualMap.entrySet()) {
            String namedIndividualIRI = 
                    simpleNamedIndividualMapEntry.getKey();
            SimpleNamedIndividual namedIndividual = 
                    simpleNamedIndividualMapEntry.getValue();
            
            // Get the previously resolved source vertex
            String sourceVertexKey = namedIndividualIRI 
                    + SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
                    + this.id;
            SimpleOntologyVertex sourceVertex = this.vertices
                    .containsKey(sourceVertexKey) ? 
                    vertices.get(sourceVertexKey) : null;
            if ( sourceVertex != null ) {
                
                // Resolve named individual > instance of > class relationships
                Set<String> instanceOfClassIris = namedIndividual
                        .getInstanceOfClassIris();
                for (String instanceOfClassIri : instanceOfClassIris) {
                    
                    // Get the previously resolved target vertex
                    String targetVertexKey = instanceOfClassIri
                            + SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
                            + this.id;
                    SimpleOntologyVertex targetVertex = 
                            this.vertices.containsKey(targetVertexKey) ? 
                                    vertices.get(targetVertexKey) : null;
                    if ( targetVertex != null ) {
                        
                        // Create a new simple ontology edge object for each 
                        // instance of class relationship
                        SimpleOntologyEdge edge = new SimpleOntologyEdge();
                        edge.setLabel(SimpleOntologyEdgeLabel.INSTANCE_OF.toString());
                        edge.setSourceVertexKey(sourceVertex.getVertexKey());
                        edge.setSourceVertexId(sourceVertex.getVertexId());
                        edge.setTargetVertexKey(targetVertex.getVertexKey());
                        edge.setTargetVertexId(targetVertex.getVertexId());
                        edge.setOntologyId(id);
                        edge.setLatestGitWebhookId(latestGitWebhookId);
                        
                        // Set a default relationship description
                        Map<String, Object> edgeProperties = new LinkedHashMap<>();
                        edgeProperties.put(
                                SimpleOntologyEdge.RELATIONSHIP_TYPE_KEY, 
                                SimpleOntologyEdgeLabel.INSTANCE_OF.toString());
                        
                        // Add the new simple ontology edge to the set of edges
                        edge.setProperties(edgeProperties);
                        this.edges.add(edge);
                    
                    }
                    
                }
                
                // Resolve linked named individual relationships
                Map<String, String> linkedNamedIndividuals = namedIndividual
                        .getLinkedNamedIndividuals();
                for (var linkedNamedIndividualsEntry : linkedNamedIndividuals
                        .entrySet()) {
                    String linkedTargetIndividualIri = 
                            linkedNamedIndividualsEntry.getKey();
                    String linkedTargetObjectPropertyIri = 
                            linkedNamedIndividualsEntry.getValue();
                    
                    // Get the previously resolved target vertex
                    String targetVertexKey = linkedTargetIndividualIri
                            + SimpleOntologyVertex.VERTEX_KEY_DELIMITER 
                            + this.id;
                    SimpleOntologyVertex targetVertex = 
                            this.vertices.containsKey(targetVertexKey) ? 
                                    vertices.get(targetVertexKey) : null;
                    if ( targetVertex != null ) {
                        
                        // Create a new simple ontology edge object for each 
                        // linked individual relationship
                        SimpleOntologyEdge edge = new SimpleOntologyEdge();
                        edge.setLabel(SimpleOntologyEdgeLabel.LINKED_NAMED_INDIVIDUAL.toString());
                        edge.setSourceVertexKey(sourceVertex.getVertexKey());
                        edge.setSourceVertexId(sourceVertex.getVertexId());
                        edge.setTargetVertexKey(targetVertex.getVertexKey());
                        edge.setTargetVertexId(targetVertex.getVertexId());
                        edge.setOntologyId(id);
                        edge.setLatestGitWebhookId(latestGitWebhookId);
                        
                        // Set a default relationship description
                        Map<String, Object> edgeProperties = new LinkedHashMap<>();
                        edgeProperties.put(
                                SimpleOntologyEdge.RELATIONSHIP_TYPE_KEY, 
                                SimpleOntologyEdgeLabel.LINKED_NAMED_INDIVIDUAL.toString());
                        
                        // Resolve the relationship description
                        if ( linkedTargetObjectPropertyIri != null ) {
                            
                            String linkedTargetObjectPropertyLabel = null;
                            if ( simpleOntology.getSimpleObjectPropertyMap()
                                    .containsKey(linkedTargetObjectPropertyIri) )
                                linkedTargetObjectPropertyLabel = 
                                    simpleOntology
                                        .getSimpleObjectPropertyMap()
                                        .get(linkedTargetObjectPropertyIri)
                                        .getLabel();
                            else {
                                
                                // Try to extract the relationship description
                                // from the object property IRI
                                int indexOfHash = linkedTargetObjectPropertyIri
                                        .lastIndexOf("#");
                                if ( indexOfHash > -1 )
                                    linkedTargetObjectPropertyLabel = 
                                                linkedTargetObjectPropertyIri
                                                    .substring(indexOfHash + 1);
                                
                            }
                            
                            // Add the relationship to the edge properties
                            if ( !StringUtils.isBlank(
                                    linkedTargetObjectPropertyLabel) )
                                edgeProperties.put(
                                        SimpleOntologyEdge.RELATIONSHIP_TYPE_KEY, 
                                        linkedTargetObjectPropertyLabel);
                            
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

	public long getLatestGitWebhookId() {
		return latestGitWebhookId;
	}

	public void setLatestGitWebhookId(long latestGitWebhookId) {
		this.latestGitWebhookId = latestGitWebhookId;
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
				+ "latestGitWebhookId=" + latestGitWebhookId + ", "
				+ "vertices=" + vertices + ", "
				+ "edges=" + edges + 
				"]";
	}

}
