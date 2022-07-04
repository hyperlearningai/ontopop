package ai.hyperlearning.ontopop.security.secrets.azure.keyvault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(
        value = "security.secrets.service",
        havingValue = "azure-key-vault")
public class AzureKeyVaultSecretsService implements SecretsService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AzureKeyVaultSecretsService.class);

    @Autowired
    private SecretClient secretClient;

    @Override
    public SecretClient getTemporaryClient(String credentials) {
        return secretClient;
    }
    
    /**
     * Retrieve a secret
     */
    
    @Override
    public String get(String key) {
        try {
            return secretClient.getSecret(key).getValue();
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public String get(Object client, String key) {
        try {
            return ((SecretClient) client).getSecret(key).getValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set a secret
     */
    
    @Override
    public void set(String key, Object value) throws Exception {
        secretClient.setSecret(
                new KeyVaultSecret(key, value.toString()));
    }
    
    @Override
    public void set(Object client, String key, Object value) throws Exception {
        ((SecretClient) client).setSecret(
                new KeyVaultSecret(key, value.toString()));
    }

    /**
     * Delete a scret
     */
    
    @Override
    public void delete(String key) throws Exception {
        SyncPoller<DeletedSecret, Void> deletedSecretPoller =
                secretClient.beginDeleteSecret(key);
        deletedSecretPoller.poll();
        deletedSecretPoller.waitForCompletion();
        try {
            secretClient.purgeDeletedSecret(key);
        } catch (Exception e) {
            LOGGER.error("Error encountered when attempting to purge a "
                    + "deleted secret.", e);
        }
    }
    
    @Override
    public void delete(Object client, String key) throws Exception {
        SyncPoller<DeletedSecret, Void> deletedSecretPoller =
                ((SecretClient) client).beginDeleteSecret(key);
        deletedSecretPoller.poll();
        deletedSecretPoller.waitForCompletion();
        try {
            ((SecretClient) client).purgeDeletedSecret(key);
        } catch (Exception e) {
            LOGGER.error("Error encountered when attempting to purge a "
                    + "deleted secret.", e);
        }
    }

}
