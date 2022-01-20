/**@
 * JanusGraph OntoPop Graph Database Deletion
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
 
 // Open the JanusGraph graph database - replace the .properties file with your own
 graph = JanusGraphFactory.open("conf/janusgraph-development-berkeleyje-es.properties")
 
 // Drop the graph database, irreversibly deleting all data in the storage and indexing backends
 JanusGraphFactory.drop(graph)
 