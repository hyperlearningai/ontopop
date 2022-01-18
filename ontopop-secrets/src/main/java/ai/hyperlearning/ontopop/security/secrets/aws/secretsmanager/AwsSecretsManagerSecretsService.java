package ai.hyperlearning.ontopop.security.secrets.aws.secretsmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.CreateSecretRequest;
import com.amazonaws.services.secretsmanager.model.DeleteSecretRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;

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

    @Override
    public String get(String key) {
        try {
            GetSecretValueRequest getSecretValueRequest =
                    new GetSecretValueRequest().withSecretId(key);
            return secretsManager.getSecretValue(getSecretValueRequest)
                    .getSecretString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void set(String key, Object value) throws Exception {
        CreateSecretRequest createSecretRequest = new CreateSecretRequest()
                .withName(key).withSecretString(value.toString());
        secretsManager.createSecret(createSecretRequest);
    }

    @Override
    public void delete(String key) throws Exception {
        DeleteSecretRequest deleteSecretRequest = new DeleteSecretRequest()
                .withSecretId(key).withForceDeleteWithoutRecovery(true);
        secretsManager.deleteSecret(deleteSecretRequest);
    }

}
