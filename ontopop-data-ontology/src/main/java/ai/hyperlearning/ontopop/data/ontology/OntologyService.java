package ai.hyperlearning.ontopop.data.ontology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologySecretData;
import ai.hyperlearning.ontopop.security.vault.Vault;

/**
 * Ontology Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class OntologyService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyService.class);
	private static final String VAULT_SUBPATH_ONTOLOGIES = "/ontologies/";
	
	@Autowired
    private OntologyRepository ontologyRepository;
	
	@Autowired
	private VaultTemplate vaultTemplate;
	
	@Value("${spring.cloud.vault.kv.backend}")
	String springCloudVaultKvBackend;
	
	@Value("${spring.cloud.vault.kv.default-context}")
	String springCloudVaultKvDefaultContext;
	
	/**
	 * Create a new ontology
	 * @param ontology
	 * @return
	 */
	
	protected Ontology create(Ontology ontology) {
		
		// Persist the new ontology
		Ontology newOntology = ontologyRepository.save(ontology);
		
		// Create a new ontology secret data object
		OntologySecretData newOntologySecretData = 
				new OntologySecretData(newOntology);
		
		// Persist the new ontology secret data
		Vault.put(vaultTemplate, 
				springCloudVaultKvBackend, 
				springCloudVaultKvDefaultContext 
					+ VAULT_SUBPATH_ONTOLOGIES 
						+ newOntology.getId(), 
				newOntologySecretData);
		
		// Sanitize and return the new ontology
		newOntology.clearSecretData();
		LOGGER.debug("Created a new ontology: {}", newOntology.toString());
		return newOntology;
		
	}

}
