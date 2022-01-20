/**@
 * JanusGraph OntoPop Graph Database Schema Creator
 * 
 * This file should be executed from within the Gremlin console that comes 
 * bundled with JanusGraph, and on any machine that has network access to the 
 * relevant JanusGraph storage and index backends.
 * You should replace conf/janusgraph-development-berkeleyje-es.properties
 * with your own JanusGraph properties file (which is used to define the
 * storage and index backends used by JanusGraph and associated connectivity
 * attributes).
 * 
 * NOTE: You must ensure that JanusGraph server is NOT running prior to
 * executing this script, otherwise the JanusGraph Management API cannot
 * be instantiated, and an error will be returned.  
 */

// Open the JanusGraph management API - replace the .properties file with your own
graph = JanusGraphFactory.open("conf/janusgraph-development-berkeleyje-es.properties")
management = graph.openManagement()

// Create property keys - common vertex property keys
management.makePropertyKey("iri").dataType(String.class).make()
management.makePropertyKey("ontologyId").dataType(Integer.class).make()
management.makePropertyKey("latestWebhookEventId").dataType(Long.class).make()
management.makePropertyKey("vertexKey").dataType(String.class).make()
management.makePropertyKey("vertexId").dataType(Long.class).make()

// Create property keys - common edge property keys
management.makePropertyKey("sourceVertexKey").dataType(String.class).make()
management.makePropertyKey("sourceVertexId").dataType(Long.class).make()
management.makePropertyKey("targetVertexKey").dataType(String.class).make()
management.makePropertyKey("targetVertexId").dataType(Long.class).make()

// Create labels - vertex labels
management.makeVertexLabel("class").make()

// Create labels - edge labels
management.makeEdgeLabel("subClassOf").make()

// Create composite indexes - vertex-centric indexes
management.buildIndex("iriIndex", Vertex.class).addKey(management.getPropertyKey("iri")).buildCompositeIndex()
management.buildIndex("ontologyIdIndex", Vertex.class).addKey(management.getPropertyKey("ontologyId")).buildCompositeIndex()
management.buildIndex("vertexKeyIndex", Vertex.class).addKey(management.getPropertyKey("vertexKey")).buildCompositeIndex()
management.buildIndex("vertexIdIndex", Vertex.class).addKey(management.getPropertyKey("vertexId")).buildCompositeIndex()

// Create composite indexes - edge-centric indexes
management.buildIndex("sourceVertexKeyIndex", Edge.class).addKey(management.getPropertyKey("sourceVertexKey")).buildCompositeIndex()
management.buildIndex("sourceVertexIdIndex", Edge.class).addKey(management.getPropertyKey("sourceVertexId")).buildCompositeIndex()
management.buildIndex("targetVertexKeyIndex", Edge.class).addKey(management.getPropertyKey("targetVertexKey")).buildCompositeIndex()
management.buildIndex("targetVertexIdIndex", Edge.class).addKey(management.getPropertyKey("targetVertexId")).buildCompositeIndex()

// Commit the schema
management.commit()

// Close the graph
graph.close()
