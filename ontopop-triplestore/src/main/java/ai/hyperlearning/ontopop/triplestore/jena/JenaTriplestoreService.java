package ai.hyperlearning.ontopop.triplestore.jena;

import java.io.IOException;

import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.triplestore.TriplestoreService;

/**
 * Apache Jena Triplestore Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class JenaTriplestoreService implements TriplestoreService {

	@Override
	public boolean doesRepositoryExist(int id) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createRepository(int id) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeRepository(int id) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadOntologyOwlRdfXml(int id, String owlSourceUri) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
