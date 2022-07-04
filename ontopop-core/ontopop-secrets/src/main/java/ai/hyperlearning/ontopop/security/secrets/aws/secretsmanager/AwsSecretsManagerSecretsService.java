package ai.hyperlearning.ontopop.security.secrets.aws.secretsmanager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.ResourceExistsException;
import com.amazonaws.services.secretsmanager.model.UpdateSecretRequest;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AwsSecretsManagerSecretsService.class);

    private static final ZoneId TIME_ZONE_UTC = ZoneId.of("UTC");
    
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
    public AWSSecretsManager getTemporaryClient(String credentials) 
            throws Exception {
        
        // Parse the credentials JSON object
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES
                .mappedFeature());
        AwsSecretsManagerGuestCredentials guestCredentials = 
                mapper.readValue(credentials, 
                        AwsSecretsManagerGuestCredentials.class);
        
        // Check that the credentials have not expired
        LocalDateTime expirationDateTime = Instant
                .ofEpochSecond(guestCredentials.getExpiration())
                .atZone(TIME_ZONE_UTC)
                .toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now(TIME_ZONE_UTC);
        if ( expirationDateTime.isBefore(currentDateTime) )
            throw new InternalServiceErrorException("Invalid Credentials");
        
        // Create a new AWS Secrets Manager client using the
        // temporary credentials.
        AWSCredentials sessionCredentials = 
                new BasicSessionCredentials(
                        guestCredentials.getAccessKeyId(), 
                        guestCredentials.getSecretAccessKey(), 
                        guestCredentials.getSessionToken());
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
            LOGGER.error("Error encountered when attempting to retrieve a "
                    + "secret using guest credentials.", e);
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
