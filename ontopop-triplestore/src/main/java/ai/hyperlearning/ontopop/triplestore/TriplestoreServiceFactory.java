package ai.hyperlearning.ontopop.triplestore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.triplestore.jena.JenaTriplestoreService;
import ai.hyperlearning.ontopop.triplestore.ontotext.OntotextGraphDBTriplestoreService;

/**
 * Triplestore Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class TriplestoreServiceFactory {
	
	@Autowired(required=false)
	private JenaTriplestoreService jenaTriplestoreService;
	
	@Autowired(required=false)
	private OntotextGraphDBTriplestoreService ontotextGraphDBTriplestoreService;
	
	/**
	 * Select the relevant triplestore service
	 * @param type
	 * @return
	 */
	
	public TriplestoreService getTriplestoreService(String type) {
		
		TriplestoreServiceType triplestoreServiceType = 
				TriplestoreServiceType.valueOfLabel(type.toUpperCase());
		switch ( triplestoreServiceType ) {
			case APACHE_JENA:
				return jenaTriplestoreService;
			case ONTOTEXT_GRAPHDB:
				return ontotextGraphDBTriplestoreService;
			default:
				return jenaTriplestoreService;
		}
		
	}
	
	public TriplestoreService getTriplestoreService(
			TriplestoreServiceType triplestoreServiceType) {
			
		switch ( triplestoreServiceType ) {
			case APACHE_JENA:
				return jenaTriplestoreService;
			case ONTOTEXT_GRAPHDB:
				return ontotextGraphDBTriplestoreService;
			default:
				return jenaTriplestoreService;
		}
		
	}

}
