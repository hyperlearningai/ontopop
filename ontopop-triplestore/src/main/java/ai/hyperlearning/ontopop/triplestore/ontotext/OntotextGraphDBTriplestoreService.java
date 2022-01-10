package ai.hyperlearning.ontopop.triplestore.ontotext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.triplestore.TriplestoreService;

/**
 * Ontotext GraphDB Triplestore Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value="storage.triplestore.service", 
        havingValue = "ontotext-graphdb")
public class OntotextGraphDBTriplestoreService implements TriplestoreService {
	
	private static final String REPOSITORY_DEFAULT_CONFIGURATION_FILENAME = 
			"ontotext-graphdb-repository-defaults.ttl";
	private static final String REPOSITORY_BASE_URI = "/repositories/";
	private RepositoryManager repositoryManager = null;
	private String url;
	
	public OntotextGraphDBTriplestoreService (
			@Value("${storage.triplestore.ontotext-graphdb.url}") String url) 
					throws IOException {
		this.url = url;
		repositoryManager = RepositoryProvider.getRepositoryManager(url);
		repositoryManager.init();
	}
	
	@Override
	public Repository getRepository(int id) throws IOException {
		
		// Get a repository - if it does not exist then null will be returned
		return repositoryManager.getRepository(
				String.valueOf(id));
		
	}
	
	@Override
	public void createRepository(int id) throws IOException {
			
		// Instantiate a repository graph model
		TreeModel graph = new TreeModel();
		
		// Read the default repository configuration file
		RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
		rdfParser.setRDFHandler(new StatementCollector(graph));
		try (InputStream configuration = new FileInputStream(
				"/" + REPOSITORY_DEFAULT_CONFIGURATION_FILENAME)) {
			rdfParser.parse(configuration, RepositoryConfigSchema.NAMESPACE);
		}
		
		// Retrieve the repository node as a resource
		Resource repositoryNode =  Models.subject(graph
				.filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY))
				.get();
		
		// Create a repository configuration object 
		// and add it to the repository manager
		RepositoryConfig repositoryConfiguration = 
				RepositoryConfig.create(graph, repositoryNode);
		repositoryConfiguration.setID(String.valueOf(id));
		repositoryManager.addRepositoryConfig(repositoryConfiguration);
		
	}
	
	@Override
	public void deleteRepository(int id) throws IOException {
		
		// Remove the given repository
		repositoryManager.removeRepository(String.valueOf(id));
		
	}

	private RepositoryConnection getRepositoryConnection(int id) {
		
		HTTPRepository repository = new HTTPRepository(
				url + REPOSITORY_BASE_URI + String.valueOf(id));
		return repository.getConnection();
		
	}
	
	@Override
	public void loadOntologyOwlRdfXml(int id, String owlSourceUri) 
			throws IOException {
		
		// Naively (delete and) create a repository (POC only)
		// To do: Research Ontotext GraphDB update mode
		if (getRepository(id) != null)
			deleteRepository(id);
		createRepository(id);
		
		// Get a connection to the repository
		RepositoryConnection connection = getRepositoryConnection(id);
		
		// Add RDF data
		try (InputStream input = new FileInputStream(owlSourceUri)) {
			connection.begin();
			connection.add(input, null, RDFFormat.RDFXML);
			connection.commit();
		} finally {
			connection.close();
		}
		
	}

	@Override
	public void cleanup() throws IOException {
		
		if ( repositoryManager != null )
			repositoryManager.shutDown();
		
	}

}
