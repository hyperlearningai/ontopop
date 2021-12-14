package ai.hyperlearning.ontopop.triplestore;

import java.io.IOException;

/**
 * Triplestore Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface TriplestoreService {
	
	boolean doesRepositoryExist(int id) throws IOException;
	
	void createRepository(int id) throws IOException;
	
	void removeRepository(int id) throws IOException;
	
	void loadOntologyOwlRdfXml(int id, String owlSourceUri) 
			throws IOException;
	
	void cleanup() throws IOException;

}
