package ai.hyperlearning.ontopop.security.secrets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.security.secrets.aws.secretsmanager.AwsSecretsManagerSecretsService;
import ai.hyperlearning.ontopop.security.secrets.azure.keyvault.AzureKeyVaultSecretsService;

/**
 * Secrets Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class SecretsServiceFactory {
	
	@Autowired
	private AwsSecretsManagerSecretsService awsSecretsManagerSecretsService;
	
	@Autowired
	private AzureKeyVaultSecretsService azureKeyVaultSecretsService;
	
	/**
	 * Select the relevant object storage service
	 * @param type
	 * @return
	 */
	
	public SecretsService getSecretsService(String type) {
		
		SecretsServiceType secretsServiceType = 
				SecretsServiceType.valueOfLabel(type.toUpperCase());
		switch ( secretsServiceType ) {
			case HASHICORP_VAULT:
				return null;
			case AWS_SECRETS_MANAGER:
				return awsSecretsManagerSecretsService;
			case AZURE_KEY_VAULT:
				return azureKeyVaultSecretsService;
			default:
				return null;
		}
		
	}
	
	public SecretsService getSecretsService(
			SecretsServiceType secretsServiceType) {
			
		switch ( secretsServiceType ) {
			case HASHICORP_VAULT:
				return null;
			case AWS_SECRETS_MANAGER:
				return awsSecretsManagerSecretsService;
			case AZURE_KEY_VAULT:
				return azureKeyVaultSecretsService;
			default:
				return null;
		}
			
	}

}
