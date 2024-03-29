package ai.hyperlearning.ontopop.data.ontology.management;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;

import ai.hyperlearning.ontopop.data.jpa.repositories.GitWebhookRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyCreateConflictException;
import ai.hyperlearning.ontopop.exceptions.ontology.OntologyNotFoundException;
import ai.hyperlearning.ontopop.mappers.OntologyMapper;
import ai.hyperlearning.ontopop.model.git.GitWebhook;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.ontology.OntologyNonSecretData;
import ai.hyperlearning.ontopop.security.secrets.managers.OntologySecretDataManager;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;

/**
 * Ontology Management Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@Transactional
public class OntologyManagementService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyManagementService.class);

    @Autowired
    private OntologyRepository ontologyRepository;
    
    @Autowired
    private GitWebhookRepository gitWebhookRepository;

    @Autowired
    private OntologyMapper ontologyMapper;

    @Autowired
    private OntologySecretDataManager ontologySecretDataManager;

    /**
     * Create a new ontology
     * 
     * @param ontology
     * @return
     * @throws Exception
     */

    public Ontology create(Ontology ontology) throws Exception {

        // Check whether this ontology already exists
        List<Ontology> existingOntology = ontologyRepository
                .findByRepoUrlPathBranch(
                        ontology.getRepoUrl(), 
                        ontology.getRepoResourcePath(), 
                        ontology.getRepoBranch());
        if ( existingOntology.isEmpty() ) {
            
            // Persist the new ontology
            ontology.setDateCreated(LocalDateTime.now());
            ontology.setDateLastUpdated(LocalDateTime.now());
            ontology.setLatestWebProtegeRevisionNumber(-1);
            Ontology newOntology = ontologyRepository.save(ontology);

            // Create a new ontology secret data object
            OntologySecretData newOntologySecretData = new OntologySecretData(
                    newOntology.getId(), 
                    newOntology.getRepoToken(),
                    newOntology.getRepoWebhookSecret());

            // Persist the new ontology secret data
            ontologySecretDataManager.put(newOntologySecretData);

            // Sanitize and return the new ontology
            newOntology.clearSecretData();
            LOGGER.debug("Created a new ontology: {}", newOntology);
            return newOntology;
            
        } else 
            throw new OntologyCreateConflictException();

    }

    /**
     * Update an ontology (non-sensitive attributes)
     * 
     * @param ontologyNonSecretData
     * @return
     */

    public Ontology update(int id,
            OntologyNonSecretData ontologyNonSecretData) 
                    throws OntologyNotFoundException {

        // Get the current ontology
        Ontology ontology = ontologyRepository.findById(id)
                .orElseThrow(OntologyNotFoundException::new);

        // Persist the partially updated ontology
        ontologyNonSecretData.setId(id);
        ontologyNonSecretData.setDateLastUpdated(LocalDateTime.now());
        
        // Overcome MapStruct mapping feature.bug where if a boolean 
        // is not provided, it defaults to false.
        if (ontologyNonSecretData.isRepoPrivate() == null )
            ontologyNonSecretData.setRepoPrivate(ontology.isRepoPrivate());
        
        // Map the OntologyNonSecretData object to the ontology object
        ontologyMapper.updateOntology(ontologyNonSecretData, ontology);
        Ontology updatedOntology = ontologyRepository.save(ontology);
        LOGGER.debug("Updated ontology with ID: {}", id);
        return updatedOntology;

    }

    /**
     * Update ontology secrets
     * 
     * @param ontologySecretData
     * @throws Exception
     */

    public void update(int id, OntologySecretData ontologySecretData)
            throws Exception {

        // Get the current sensitive attributes for this ontology
        OntologySecretData currentOntologySecretData =
                ontologySecretDataManager.get(id);

        // Set the new sensitive attributes
        if (!Strings.isNullOrEmpty(ontologySecretData.getRepoToken()))
            currentOntologySecretData
                    .setRepoToken(ontologySecretData.getRepoToken());
        if (!Strings.isNullOrEmpty(ontologySecretData.getRepoWebhookSecret()))
            currentOntologySecretData.setRepoWebhookSecret(
                    ontologySecretData.getRepoWebhookSecret());

        // Persist the updated ontology secret data
        if (!Strings.isNullOrEmpty(ontologySecretData.getRepoToken())
                || !Strings.isNullOrEmpty(
                        ontologySecretData.getRepoWebhookSecret()))
            ontologySecretDataManager.put(currentOntologySecretData);
        LOGGER.debug("Updated ontology (sensitive) with ID: {}", id);

    }

    /**
     * Delete an ontology
     * 
     * @param id
     * @throws Exception
     */

    public void delete(int id) throws Exception {

        // Delete the ontology from storage
        ontologyRepository.deleteById(id);

        // Delete the ontology secret data
        ontologySecretDataManager.delete(id);
        LOGGER.debug("Deleted ontology with ID: {}", id);

    }
    
    /**
     * Get the latest Git webhook ID consumed for a given ontology
     * @param id
     * @return
     */
    
    public Long getLatestGitWebhookId(int id) {
        
        // Get the Git webhooks consumed for this ontology
        List<GitWebhook> gitWebhooks = gitWebhookRepository
                .findByOntologyId(id);
        
        // Get the latest Git webhook ID
        if ( !gitWebhooks.isEmpty() ) {
            Optional<GitWebhook> latestGitWebhook = gitWebhooks.stream()
                    .max(Comparator.comparing(GitWebhook::getId));
            if ( latestGitWebhook.isPresent() )
                return latestGitWebhook.get().getId();
        }
        
        // If no Git webhooks have been consumed for this ontology
        // then return a default value
        return null;
        
    }

}
