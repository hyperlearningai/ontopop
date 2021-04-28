package ai.hyperlearning.ontology.services.api.ontology.statics;

import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontology.services.model.ontology.ParsedRDFOwlOntology;
import ai.hyperlearning.ontology.services.owlapi.OWLAPIPipelines;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;

/**
 * Ontology API Application Component
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@Component
public class OntologyAPIStaticObjects {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyAPIStaticObjects.class);
	
	private static ParsedRDFOwlOntology parsedRDFOwlOntology;
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@PostConstruct
	public void init() throws ConfigurationException, URISyntaxException, 
	InconsistentOntologyException, OWLOntologyCreationException, JAXBException {
		
		/**********************************************************************
		 * PHASE 1 (ONLY!) - PARSE AND LOAD ONTOLOGY FROM LOCAL FILESYSTEM
		 *********************************************************************/
		
		String ontologyOwlFilename = 
				globalProperties.getGraphDbAppsLoaderOntologyOwlFilename();
		parse(ontologyOwlFilename);
		
	}
	
	public static ParsedRDFOwlOntology getParsedRDFOwlOntology() {
		return parsedRDFOwlOntology;
	}
	
	/**
	 * Parse and load the ontology into an in-memory object
	 * @param ontologyOwlFilename
	 */
	
	public void parse(String ontologyOwlFilename) {
		
		try {
			
			// Parse the ontology OWL file
			LOGGER.info("Parsing {}", ontologyOwlFilename);
			parsedRDFOwlOntology = OWLAPIPipelines
					.parseRdfOwlOntology(ontologyOwlFilename);
			
		} catch (Exception e) {
			
			LOGGER.error("An error was encountered parsing the OWL file", e);
			
		}
		
	}

}
