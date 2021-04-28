package ai.hyperlearning.ontology.services.owlapi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ai.hyperlearning.ontology.services.model.ontology.RDFOwlAnnotationProperty;

/**
 * OWL SKOS Utility Methods
 * https://www.w3.org/2009/08/skos-reference/skos.html
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class OWLSKOSUtils {
	
	private static final Set<String> SKOS_SCHEMA_CLASS_URIS = Stream.of(
			"http://www.w3.org/2004/02/skos/core#Collection", 
			"http://www.w3.org/2004/02/skos/core#Concept", 
			"http://www.w3.org/2004/02/skos/core#ConceptScheme", 
			"http://www.w3.org/2004/02/skos/core#OrderedCollection", 
			"http://www.w3.org/2004/02/skos/core#altLabel", 
			"http://www.w3.org/2004/02/skos/core#broadMatch", 
			"http://www.w3.org/2004/02/skos/core#broader", 
			"http://www.w3.org/2004/02/skos/core#broaderTransitive", 
			"http://www.w3.org/2004/02/skos/core#changeNote", 
			"http://www.w3.org/2004/02/skos/core#closeMatch", 
			"http://www.w3.org/2004/02/skos/core#comment",
			"http://www.w3.org/2004/02/skos/core#definition", 
			"http://www.w3.org/2004/02/skos/core#editorialNote", 
			"http://www.w3.org/2004/02/skos/core#exactMatch", 
			"http://www.w3.org/2004/02/skos/core#example", 
			"http://www.w3.org/2004/02/skos/core#hasTopConcept", 
			"http://www.w3.org/2004/02/skos/core#hiddenLabel", 
			"http://www.w3.org/2004/02/skos/core#historyNote", 
			"http://www.w3.org/2004/02/skos/core#inScheme", 
			"http://www.w3.org/2004/02/skos/core#mappingRelation", 
			"http://www.w3.org/2004/02/skos/core#member", 
			"http://www.w3.org/2004/02/skos/core#memberList", 
			"http://www.w3.org/2004/02/skos/core#narrowMatch", 
			"http://www.w3.org/2004/02/skos/core#narrower", 
			"http://www.w3.org/2004/02/skos/core#narrowerTransitive",
			"http://www.w3.org/2004/02/skos/core#notation",
			"http://www.w3.org/2004/02/skos/core#note",
			"http://www.w3.org/2004/02/skos/core#prefLabel",
			"http://www.w3.org/2004/02/skos/core#related",
			"http://www.w3.org/2004/02/skos/core#relatedMatch",
			"http://www.w3.org/2004/02/skos/core#scopeNote",
			"http://www.w3.org/2004/02/skos/core#semanticRelation",
			"http://www.w3.org/2004/02/skos/core#topConceptOf").collect(
					Collectors.toCollection(HashSet::new));
	
	/**
	 * Generate an annotation property map of SKOS classes
	 * @param startId
	 * @return
	 */
	
	public static Map<String, RDFOwlAnnotationProperty> generateSkosAnnotationPropertyMap(int startId) {
		
		// Instantiate an empty annotation property map
		Map<String, RDFOwlAnnotationProperty> owlSkosAnnotationPropertyMap = 
				new HashMap<String, RDFOwlAnnotationProperty>();
		
		// Iterate over the set of SKOS class URIs and create new
		// annotation property POJOs
		int id = startId;
		for (String skosClassURI : SKOS_SCHEMA_CLASS_URIS) {
			RDFOwlAnnotationProperty rdfOwlAnnotationProperty = 
					new RDFOwlAnnotationProperty();
			String[] skosClassURIComponents = skosClassURI.split("#");
			String skosClassLabel = 
					skosClassURIComponents[skosClassURIComponents.length - 1];
			rdfOwlAnnotationProperty.setId(id);
			rdfOwlAnnotationProperty.setRdfAboutManually(skosClassURI);
			rdfOwlAnnotationProperty.setRdfsLabelManually(
					"skos" + skosClassLabel.substring(0, 1).toUpperCase() + 
					skosClassLabel.substring(1));
			owlSkosAnnotationPropertyMap.put(
					skosClassURI, rdfOwlAnnotationProperty);
			id++;
		}
		
		return owlSkosAnnotationPropertyMap;
		
	}

}
