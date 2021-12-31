package ai.hyperlearning.ontopop.security.secrets.managers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;

import ai.hyperlearning.ontopop.security.secrets.SecretsService;
import ai.hyperlearning.ontopop.security.secrets.SecretsServiceFactory;
import ai.hyperlearning.ontopop.security.secrets.SecretsServiceType;
import ai.hyperlearning.ontopop.security.secrets.hashicorp.vault.HashicorpVaultSecretsService;
import ai.hyperlearning.ontopop.security.secrets.model.OntologySecretData;

/**
 * Ontology Secret Data Manager
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class OntologySecretDataManager {
	
	private static final String ONTOLOGY_SECRET_REPO_TOKEN_KEY = 
			"ontologyRepoToken";
	private static final String ONTOLOGY_SECRET_REPO_WEBHOOK_SECRET_KEY = 
			"ontologyRepoWebhookSecret";
	
	@Autowired
	private VaultTemplate vaultTemplate;
	
	@Autowired
	private SecretsServiceFactory secretsServiceFactory;
	
	@Value("${spring.cloud.vault.kv.backend}")
	private String springCloudVaultKvBackend;
	
	@Value("${spring.cloud.vault.kv.default-context}")
	private String springCloudVaultKvDefaultContext;
	
	@Value("${security.secrets.service}")
	private String securitySecretsService;
	
	@Value("${security.secrets.hashicorp-vault.paths.subpaths.ontologies}")
	private String vaultSubpathOntologies;
	
	private SecretsService secretsService = null;
	private boolean useVault = false;
	
	@PostConstruct
    public void init() {
		
		// Ascertain whether to use a platform-specific secrets manager
		// or Hashicorp Vault
		SecretsServiceType secretsServiceType = 
				SecretsServiceType.valueOfLabel(
						securitySecretsService.toUpperCase());
		secretsService = secretsServiceFactory
				.getSecretsService(secretsServiceType);
		if ( secretsService == null && 
				secretsServiceType.equals(SecretsServiceType.HASHICORP_VAULT) )
			useVault = true;
		
	}
	
	/**
	 * Persist ontology secret data
	 * @param ontologySecretData
	 * @throws Exception
	 */
	
	public void put(OntologySecretData ontologySecretData) throws Exception {
		
		// Hashicorp Vault
		if ( useVault ) {
			HashicorpVaultSecretsService.put(vaultTemplate, 
					springCloudVaultKvBackend, 
					springCloudVaultKvDefaultContext 
						+ vaultSubpathOntologies 
							+ ontologySecretData.getId(), 
					ontologySecretData);
		}
		
		// Platform-specific secrets manager
		else {
			secretsService.set(ONTOLOGY_SECRET_REPO_TOKEN_KEY 
					+ ontologySecretData.getId(), 
					ontologySecretData.getRepoToken());
			secretsService.set(ONTOLOGY_SECRET_REPO_WEBHOOK_SECRET_KEY 
					+ ontologySecretData.getId(), 
					ontologySecretData.getRepoWebhookSecret());
		}
		
	}
	
	/**
	 * Get ontology secret data given an ontology ID
	 * @param ontologyId
	 * @return
	 * @throws Exception
	 */
	
	public OntologySecretData get(int ontologyId) throws Exception {
		
		// Hashicorp Vault
		if ( useVault ) {
			return HashicorpVaultSecretsService.get(vaultTemplate, 
					springCloudVaultKvBackend, 
					springCloudVaultKvDefaultContext 
						+ vaultSubpathOntologies + ontologyId, 
					OntologySecretData.class).getData();
		}
		
		// Platform-specific secrets manager
		else {
			String repoToken = secretsService.get(
					ONTOLOGY_SECRET_REPO_TOKEN_KEY + ontologyId);
			String repoWebhookSecret = secretsService.get(
					ONTOLOGY_SECRET_REPO_WEBHOOK_SECRET_KEY + ontologyId);
			return new OntologySecretData(ontologyId, 
					repoToken, repoWebhookSecret);
		}
		
	}
	
	/**
	 * Delete ontology secret data
	 * @param ontologyId
	 * @throws Exception
	 */
	
	public void delete(int ontologyId) throws Exception {
		
		// Hashicorp Vault
		if ( useVault ) {
			HashicorpVaultSecretsService.delete(vaultTemplate, 
					springCloudVaultKvBackend, 
					springCloudVaultKvDefaultContext 
						+ vaultSubpathOntologies + ontologyId);
		}
		
		// Platform-specific secrets manager
		else {
			secretsService.delete(
					ONTOLOGY_SECRET_REPO_TOKEN_KEY + ontologyId);
			secretsService.delete(
					ONTOLOGY_SECRET_REPO_WEBHOOK_SECRET_KEY + ontologyId);
		}
		
	}

}
