package ai.hyperlearning.ontopop.security.secrets.aws.secretsmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.CreateSecretRequest;
import com.amazonaws.services.secretsmanager.model.DeleteSecretRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.ResourceExistsException;
import com.amazonaws.services.secretsmanager.model.UpdateSecretRequest;

import ai.hyperlearning.ontopop.security.secrets.SecretsService;

/**
 * AWS Secrets Manager Secrets Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "security.secrets.service",
        havingValue = "aws-secrets-manager")
public class AwsSecretsManagerSecretsService implements SecretsService {

    @Autowired
    @Qualifier("awsSecretsManager")
    private AWSSecretsManager secretsManager;
    
    @Value("${security.secrets.aws-secrets-manager.region}")
    private String region;

    /**
     * Connect to AWS Secrets Manager using basic session credentials
     * including a temporary access key, temporary secret key and
     * session token.
     */
    
    @Override
    public AWSSecretsManager getTemporaryClient(String... credentials) 
            throws Exception {
        AWSCredentials sessionCredentials = 
                new BasicSessionCredentials(credentials[0], 
                        credentials[1], credentials[2]);
        return AWSSecretsManagerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        sessionCredentials))
                .withRegion(Regions.fromName(region))
                .build();
    }
    
    /**
     * Request to retrieve a secret from AWS Secrets Manager
     * @param key
     * @return
     */
    
    private GetSecretValueRequest getSecretValueRequest(String key) {
        return new GetSecretValueRequest().withSecretId(key);
    }
    
    /**
     * Request to create a secret in AWS Secrets Manager
     * @param key
     * @param value
     * @return
     */
    
    private CreateSecretRequest createSecretRequest(String key, Object value) {
        return new CreateSecretRequest()
                .withName(key).withSecretString(value.toString());
    }
    
    /**
     * Request to update a secret in AWS Secrets Manager
     * @param key
     * @param value
     * @return
     */
    
    private UpdateSecretRequest updateSecretRequest(String key, Object value) {
        UpdateSecretRequest updateSecretRequest = new UpdateSecretRequest()
                .withSecretId(key);
        updateSecretRequest.setSecretString(value.toString());
        return updateSecretRequest;
    }
    
    /**
     * Request to delete a secret in AWS Secrets Manager
     * @param key
     * @return
     */
    
    private DeleteSecretRequest deleteSecretRequest(String key) {
        return new DeleteSecretRequest()
                .withSecretId(key).withForceDeleteWithoutRecovery(true);
    }
    
    /**
     * Retrieve a secret using the default client
     */
    
    @Override
    public String get(String key) {
        try {
            return secretsManager
                    .getSecretValue(getSecretValueRequest(key))
                    .getSecretString();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Retrieve a secret using a temporary client
     */
    
    @Override
    public String get(Object client, String key) {
        try {
            return ((AWSSecretsManager) client)
                    .getSecretValue(getSecretValueRequest(key))
                    .getSecretString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set a secret using the default client
     */
    
    @Override
    public void set(String key, Object value) throws Exception {
        try {
            secretsManager.createSecret(createSecretRequest(key, value));
        } catch (ResourceExistsException e) {
            secretsManager.updateSecret(updateSecretRequest(key, value));
        }
    }
    
    /**
     * Set a secret using a temporary client
     */
    
    @Override
    public void set(Object client, String key, Object value) throws Exception {
        try {
            ((AWSSecretsManager) client).createSecret(
                    createSecretRequest(key, value));
        } catch (ResourceExistsException e) {
            ((AWSSecretsManager) client).updateSecret(
                    updateSecretRequest(key, value));
        }
    }

    /**
     * Delete a secret using the default client
     */
    
    @Override
    public void delete(String key) throws Exception {
        secretsManager.deleteSecret(deleteSecretRequest(key));
    }
    
    /**
     * Delete a secret using a temporary client
     */
    
    @Override
    public void delete(Object client, String key) throws Exception {
        ((AWSSecretsManager) client).deleteSecret(deleteSecretRequest(key));
    }

}
