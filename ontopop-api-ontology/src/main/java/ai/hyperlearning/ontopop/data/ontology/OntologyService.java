package ai.hyperlearning.ontopop.data.ontology;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;

import com.google.common.base.Strings;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.mappers.OntologyMapper;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyNonSecretData;
import ai.hyperlearning.ontopop.model.ontology.OntologySecretData;
import ai.hyperlearning.ontopop.security.vault.Vault;

/**
 * Ontology Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@Transactional
public class OntologyService {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(OntologyService.class);
	
	@Autowired
    private OntologyRepository ontologyRepository;
	
	@Autowired
	private OntologyMapper ontologyMapper;
	
	@Autowired
	private VaultTemplate vaultTemplate;
	
	@Value("${spring.cloud.vault.kv.backend}")
	String springCloudVaultKvBackend;
	
	@Value("${spring.cloud.vault.kv.default-context}")
	String springCloudVaultKvDefaultContext;
	
	@Value("${security.vault.paths.subpaths.ontologies}")
	private String vaultSubpathOntologies;
	
	/**
	 * Create a new ontology
	 * @param ontology
	 * @return
	 */
	
	protected Ontology create(Ontology ontology) {
		
		// Persist the new ontology
		ontology.setDateCreated(LocalDateTime.now());
		ontology.setDateLastUpdated(LocalDateTime.now());
		Ontology newOntology = ontologyRepository.save(ontology);
		
		// Create a new ontology secret data object
		OntologySecretData newOntologySecretData = 
				new OntologySecretData(newOntology);
		
		// Persist the new ontology secret data
		Vault.put(vaultTemplate, 
				springCloudVaultKvBackend, 
				springCloudVaultKvDefaultContext 
					+ vaultSubpathOntologies 
						+ newOntology.getId(), 
				newOntologySecretData);
		
		// Sanitize and return the new ontology
		newOntology.clearSecretData();
		LOGGER.debug("Created a new ontology: {}", newOntology.toString());
		return newOntology;
		
	}
	
	/**
	 * Update an ontology (non-sensitive attributes)
	 * @param ontologyNonSecretData
	 * @return
	 */
	
	protected Ontology update(int id, 
			OntologyNonSecretData ontologyNonSecretData) {
		
		// Get the current ontology
		Ontology ontology = ontologyRepository.findById(id)
					.orElseThrow(
							() -> new OntologyNotFoundException(id));
		
		// Persist the partially updated ontology
		ontologyNonSecretData.setId(id);
		ontologyNonSecretData.setDateLastUpdated(LocalDateTime.now());
		ontologyMapper.updateOntology(ontologyNonSecretData, ontology);
		Ontology updatedOntology = ontologyRepository.save(ontology);
		LOGGER.debug("Updated ontology with ID: {}", id);
		return updatedOntology;
		
	}
	
	/**
	 * Update ontology secrets
	 * @param ontologySecretData
	 */
	
	protected void update(int id, 
			OntologySecretData ontologySecretData) {
		
		// Get the current sensitive attributes for this ontology
		OntologySecretData currentOntologySecretData = 
				Vault.get(vaultTemplate, 
						springCloudVaultKvBackend, 
						KeyValueBackend.KV_2, 
						springCloudVaultKvDefaultContext 
							+ vaultSubpathOntologies + id, 
						OntologySecretData.class).getData();
		
		// Set the new sensitive attributes
		if ( !Strings.isNullOrEmpty(ontologySecretData.getRepoToken()) )
			currentOntologySecretData.setRepoToken(
					ontologySecretData.getRepoToken());
		if ( !Strings.isNullOrEmpty(ontologySecretData.getRepoWebhookSecret()) )
			currentOntologySecretData.setRepoWebhookSecret(
					ontologySecretData.getRepoWebhookSecret());
		
		// Persist the updated ontology secret data
		if ( !Strings.isNullOrEmpty(
				ontologySecretData.getRepoToken()) 
				|| !Strings.isNullOrEmpty(
						ontologySecretData.getRepoWebhookSecret()) )
			Vault.put(vaultTemplate, 
					springCloudVaultKvBackend, 
					springCloudVaultKvDefaultContext 
						+ vaultSubpathOntologies +  id, 
					currentOntologySecretData);
		LOGGER.debug("Updated ontology (sensitive) with ID: {}", id);
		
	}
	
	/**
	 * Delete an ontology
	 * @param id
	 */
	
	protected void delete(int id) {
		
		// Delete the ontology from storage
		ontologyRepository.deleteById(id);
		
		// Delete the ontology secret data
		Vault.delete(vaultTemplate, 
				springCloudVaultKvBackend, 
				springCloudVaultKvDefaultContext 
					+ vaultSubpathOntologies + id);
		LOGGER.debug("Deleted ontology with ID: {}", id);
		
	}

}