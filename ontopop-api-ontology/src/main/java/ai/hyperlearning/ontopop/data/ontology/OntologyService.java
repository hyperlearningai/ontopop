package ai.hyperlearning.ontopop.data.ontology;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.mappers.OntologyMapper;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyNonSecretData;
import ai.hyperlearning.ontopop.security.secrets.managers.OntologySecretDataManager;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;

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
	private OntologySecretDataManager ontologySecretDataManager;
	
	/**
	 * Create a new ontology
	 * @param ontology
	 * @return
	 * @throws Exception 
	 */
	
	protected Ontology create(Ontology ontology) throws Exception {
		
		// Persist the new ontology
		ontology.setDateCreated(LocalDateTime.now());
		ontology.setDateLastUpdated(LocalDateTime.now());
		Ontology newOntology = ontologyRepository.save(ontology);
		
		// Create a new ontology secret data object
		OntologySecretData newOntologySecretData = 
				new OntologySecretData(newOntology.getId(), 
						newOntology.getRepoToken(), 
						newOntology.getRepoWebhookSecret());
		
		// Persist the new ontology secret data
		ontologySecretDataManager.put(newOntologySecretData);
		
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
	 * @throws Exception 
	 */
	
	protected void update(int id, 
			OntologySecretData ontologySecretData) throws Exception {
		
		// Get the current sensitive attributes for this ontology
		OntologySecretData currentOntologySecretData = 
				ontologySecretDataManager.get(id);
		
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
			ontologySecretDataManager.put(currentOntologySecretData);
		LOGGER.debug("Updated ontology (sensitive) with ID: {}", id);
		
	}
	
	/**
	 * Delete an ontology
	 * @param id
	 * @throws Exception 
	 */
	
	protected void delete(int id) throws Exception {
		
		// Delete the ontology from storage
		ontologyRepository.deleteById(id);
		
		// Delete the ontology secret data
		ontologySecretDataManager.delete(id);
		LOGGER.debug("Deleted ontology with ID: {}", id);
		
	}

}
