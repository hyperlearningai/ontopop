package ai.hyperlearning.ontopop.model.graph;

import java.io.Serializable;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

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
	
	// Set of OWL classes and associated annotations and annotation properties
	private Set<Vertex> vertices;
	
	// Set of OWL subclass relationships and associated object properties
	private Set<Edge> edges;
	
	public SimpleOntologyPropertyGraph() {
		
	}

	public SimpleOntologyPropertyGraph(
			int id, 
			long latestWebhookEventId, 
			Set<Vertex> vertices, 
			Set<Edge> edges) {
		this.id = id;
		this.latestWebhookEventId = latestWebhookEventId;
		this.vertices = vertices;
		this.edges = edges;
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

	public Set<Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(Set<Vertex> vertices) {
		this.vertices = vertices;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
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
