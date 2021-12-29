package ai.hyperlearning.ontopop.security.secrets.azure.keyvault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.core.util.polling.SyncPoller;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.DeletedSecret;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import ai.hyperlearning.ontopop.security.secrets.SecretsService;

/**
 * Microsoft Azure Key Vault Secrets Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AzureKeyVaultSecretsService implements SecretsService {
	
	@Autowired
	private SecretClient secretClient;
	
	@Override
	public String get(String key) {
		try {
			return secretClient.getSecret(key).getValue();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void set(String key, Object value) throws Exception {
		secretClient.setSecret(new KeyVaultSecret(key, value.toString()));
	}

	@Override
	public void delete(String key) throws Exception {
		SyncPoller<DeletedSecret, Void> deletedSecretPoller = 
				secretClient.beginDeleteSecret(key);
		deletedSecretPoller.poll();
		deletedSecretPoller.waitForCompletion();
		secretClient.purgeDeletedSecret(key);
	}

}
