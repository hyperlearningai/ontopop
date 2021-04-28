package ai.hyperlearning.ontology.services.graphdb.apps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseManager;
import ai.hyperlearning.ontology.services.model.datamanagement.Dataset;
import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlClass;
import ai.hyperlearning.ontology.services.model.ontology.RDFOwlRestriction;
import ai.hyperlearning.ontology.services.model.ontology.RDFSSubClassOf;

/**
 * Graph Database Loader Utility Methods
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

public class GraphDatabaseLoader {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(GraphDatabaseLoader.class);
	public static final String DEFAULT_VERTEX_LABEL = "class";
	public static final String DEFAULT_EDGE_LABEL = "subclass";
	public static final String DEFAULT_EDGE_LABEL_CAPTION = "Subclass of";
	public static final String DATASET_VERTEX_LABEL = "dataset";
	public static final String DATASOURCE_EDGE_LABEL = "datasource";
	public static final String DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION = 
			"Found in";
	public static final String OWL_ANNOTATION_PROPERTY_DATASOURCE_RDFS_LABEL = 
			"Data Source";
	private ParsedRDFOwlOntology parsedRDFOwlOntology;
	private Map<String, Vertex> loadedVertexMap = 
			new HashMap<String, Vertex>();
	private Map<String, Vertex> loadedVertexLabelMap = 
			new HashMap<String, Vertex>();
	private Map<String, Vertex> loadedDatasetMap = 
			new HashMap<String, Vertex>();
	
	public GraphDatabaseLoader(ParsedRDFOwlOntology parsedRDFOwlOntology) {
		this.parsedRDFOwlOntology = parsedRDFOwlOntology;
	}
	
	/**
	 * Load parsed ontological classes as graph vertices
	 * @param graphDatabaseManager
	 */
	
	public void loadParsedOntologyClassesAsVertices(
			GraphDatabaseManager graphDatabaseManager) {
		
		// Get the parsed OWL classes
		Map<String, RDFOwlClass> owlClassMap = 
				this.parsedRDFOwlOntology.getOwlClassMap();
		
		// Iterate over the class map (class <-> vertex)
		for (Map.Entry<String, RDFOwlClass> classEntry 
				: owlClassMap.entrySet()) {
			
			// Get the current class object (which will be modelled as a vertex)
			String rdfOwlClassRdfAbout = classEntry.getKey();
			RDFOwlClass rdfOwlClass = classEntry.getValue();
			
			// Model the class attributes as a map of vertex properties
			Map<String, Object> rdfOwlClassPropertyMap = 
					rdfOwlClass.getPropertyMap(this.parsedRDFOwlOntology
							.getOwlAnnotationPropertyMap());
			
			// Create a vertex in the graph database
			Vertex vertex = graphDatabaseManager.addVertex(
					DEFAULT_VERTEX_LABEL, rdfOwlClassPropertyMap);
			loadedVertexMap.put(rdfOwlClassRdfAbout, vertex);
			String rdfsLabel = rdfOwlClass.getRdfsLabel();
			if ( rdfsLabel != null )
				loadedVertexLabelMap.put(
						rdfsLabel.toLowerCase().trim(), vertex);
			LOGGER.debug("Added vertex[class[rdfAbout={}]]", 
					rdfOwlClassRdfAbout);
			
		}
		
		// Commit if the graph database supports transactions
		graphDatabaseManager.commit();
		
	}
	
	
	/**
	 * Load parsed ontological subclass relationships and
	 * restrictions as graph edges
	 * @param graphDatabaseManager
	 */
	
	public void loadParsedOntologyClassesAsEdges(
			GraphDatabaseManager graphDatabaseManager) {
		
		// Get the parsed objects
		Map<String, RDFOwlClass> owlClassMap = 
				this.parsedRDFOwlOntology.getOwlClassMap();
		
		// Iterate over the class map (class <-> vertex)
		for (Map.Entry<String, RDFOwlClass> classEntry 
				: owlClassMap.entrySet()) {
			
			// Get the current class object which has already been 
			// modelled and loaded as a vertex
			String rdfOwlClassRdfAbout = classEntry.getKey();
			RDFOwlClass rdfOwlClass = classEntry.getValue();
			Vertex sourceVertex = loadedVertexMap.get(rdfOwlClassRdfAbout);
			if (sourceVertex != null) {
				
				// Get and iterate the subclass relationships for this class
				Set<RDFSSubClassOf> rdfsSubClassOf = 
						rdfOwlClass.getRdfsSubClassOf();
				for (RDFSSubClassOf subclassOf : rdfsSubClassOf) {
					
					// Get the superclass and hence the target vertex
					Vertex targetVertex = loadedVertexMap.get(
							subclassOf.getClassRdfAbout());
					if (targetVertex != null) {
					
						// Create an edge property map containing the 
						// edge ID (generated) and owl restriction
						Map<String, Object> rdfsSubClassOfPropertyMap = 
								new HashMap<String, Object>();
						rdfsSubClassOfPropertyMap.put(
								"edgeId", subclassOf.getEdgeId());
						RDFOwlRestriction rdfOwlRestriction = 
								subclassOf.getOwlRestriction();
						if (subclassOf.getOwlRestriction() != null) {
							rdfsSubClassOfPropertyMap.put(
									"objectPropertyRdfAbout", 
									rdfOwlRestriction
									.getObjectPropertyRdfAbout());
							rdfsSubClassOfPropertyMap.put(
									"objectPropertyRdfsLabel", 
									rdfOwlRestriction
									.getObjectPropertyRdfsLabel());
						} else
							rdfsSubClassOfPropertyMap.put(
									"objectPropertyRdfsLabel", 
									DEFAULT_EDGE_LABEL_CAPTION);
						
						// Create an edge in the graph database
						graphDatabaseManager.addEdge(
								sourceVertex, targetVertex, DEFAULT_EDGE_LABEL, 
								rdfsSubClassOfPropertyMap);
						LOGGER.debug("Added edge[class[rdfAbout={}], "
								+ "subclassof[rdfAbout={}]]", 
								rdfOwlClassRdfAbout, 
								subclassOf.getClassRdfAbout());
						
					}
					
				}
				
			}
			
		}
		
		// Commit if the graph database supports transactions
		graphDatabaseManager.commit();
		
	}
	
	/**
	 * Load a parsed data catalogue into the graph database
	 * @param datasets
	 * @param graphDatabaseManager
	 */
	
	public void loadDataCatalogue(Set<Dataset> datasets, 
			GraphDatabaseManager graphDatabaseManager) {
		
		/**********************************************************************
		 * LOAD DATASET VERTICES AND CLASS > DATASET EDGES THAT HAVE 
		 * BEEN DEFINED IN THE DATA CATALOGUE
		 *********************************************************************/
		
		// Iterate over the parsed data catalogue
		for (Dataset dataset : datasets) {
			
			// Create a vertex in the graph database representing the dataset
			Vertex targetVertex = graphDatabaseManager.addVertex(
					DATASET_VERTEX_LABEL, dataset.getMetadata());
			loadedDatasetMap.put(dataset.getName().toLowerCase().trim(), 
					targetVertex);
			LOGGER.debug("Added vertex[dataset[name={}]]", 
					dataset.getName());
			
			// Iterate over the entities mapped to this dataset
			for ( String entity : dataset.getEntities() ) {
				
				// Check whether an OWL class with this RDFS label exists
				String cleanEntity = entity.toLowerCase().trim();
				if ( loadedVertexLabelMap.containsKey(cleanEntity) ) {
					
					// Create a datasource edge between the source OWL class
					// and the target dataset vertex
					Vertex sourceVertex = loadedVertexLabelMap.get(cleanEntity);
					Map<String, Object> edgePropertyMap = 
							new HashMap<String, Object>();
					edgePropertyMap.put(
							"objectPropertyRdfsLabel", 
							DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION);
					edgePropertyMap.put(
							"role", 
							DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION);
					graphDatabaseManager.addEdge(
							sourceVertex, targetVertex, DATASOURCE_EDGE_LABEL, 
							edgePropertyMap);
					
				}
				
			}
			
		}
		
		/**********************************************************************
		 * LOAD CLASS > DATASET EDGES THAT HAVE BEEN DEFINED IN THE 
		 * OWL ONTOLOGY IN PROTEGE
		 *********************************************************************/
		
		// Iterate over the map of loaded class vertices
		for (Map.Entry<String, Vertex> entry : 
			loadedVertexLabelMap.entrySet()) {
			
			// Get the "Data Source" of the current class vertex
			Vertex classVertex = entry.getValue();
			if ( classVertex.property(
					OWL_ANNOTATION_PROPERTY_DATASOURCE_RDFS_LABEL)
					.isPresent() ) {
				String dataSourcePropertyValue = classVertex.property(
						OWL_ANNOTATION_PROPERTY_DATASOURCE_RDFS_LABEL)
						.value().toString();
				if ( dataSourcePropertyValue != null 
						&& dataSourcePropertyValue.length() > 0 ) {
					
					// Get the individual data sources for this class vertex
					String[] dataSources = dataSourcePropertyValue.split(" ");
					for ( String dataSource : dataSources ) {
						
						// Check whether a dataset vertex with this 
						// datasource name has been loaded
						if ( loadedDatasetMap.containsKey(
								dataSource.toLowerCase().trim()) ) {
							
							// Get the dataset vertex
							Vertex datasetVertex = loadedDatasetMap.get(
									dataSource.toLowerCase().trim());
							
							// Create a datasource edge between the 
							// source OWL class and the target dataset vertex
							Map<String, Object> edgePropertyMap = 
									new HashMap<String, Object>();
							edgePropertyMap.put(
									"objectPropertyRdfsLabel", 
									DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION);
							edgePropertyMap.put(
									"role", 
									DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION);
							graphDatabaseManager.addEdge(
									classVertex, datasetVertex, 
									DATASOURCE_EDGE_LABEL, 
									edgePropertyMap);
							
						}
						
						// Otherwise create this dataset vertex
						else {
							
							// Create a vertex in the graph database 
							// representing the dataset
							Dataset dataset = new Dataset();
							dataset.setName(dataSource);
							dataset.setMetadata(Collections.singletonMap(
									"name", dataSource));
							Vertex datasetVertex = graphDatabaseManager
									.addVertex(
											DATASET_VERTEX_LABEL, 
											dataset.getMetadata());
							loadedDatasetMap.put(
									dataSource.toLowerCase().trim(), 
									datasetVertex);
							
							// Create a datasource edge between the 
							// source OWL class and the target dataset vertex
							Map<String, Object> edgePropertyMap = 
									new HashMap<String, Object>();
							edgePropertyMap.put(
									"objectPropertyRdfsLabel", 
									DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION);
							edgePropertyMap.put(
									"role", 
									DATASOURCE_EDGE_LABEL_DEFAULT_CAPTION);
							graphDatabaseManager.addEdge(
									classVertex, datasetVertex, 
									DATASOURCE_EDGE_LABEL, 
									edgePropertyMap);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		// Commit if the graph database supports transactions
		graphDatabaseManager.commit();
		
		// Clear the map of loaded dataset vertices
		loadedDatasetMap.clear();
		
	}
	
	/**
	 * Empty the map of loaded class vertices
	 */
	
	public void clearLoadedVertexMap() {
		loadedVertexMap.clear();
	}
	
	/**
	 * Empty the map of loaded class vertices
	 */
	
	public void clearLoadedVertexLabelMap() {
		loadedVertexLabelMap.clear();
	}

}
