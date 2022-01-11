package ai.hyperlearning.ontopop.security.secrets.aws.secretsmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;

/**
 * AWS Secrets Manager Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value = "security.secrets.service",
        havingValue = "aws-secrets-manager")
public class AwsSecretsManagerClientConfig {

    @Value("${security.secrets.aws-secrets-manager.access-key-id}")
    private String accessKeyId;

    @Value("${security.secrets.aws-secrets-manager.access-key-secret}")
    private String accessKeySecret;

    @Value("${security.secrets.aws-secrets-manager.region}")
    private String region;

    @Bean
    public AWSSecretsManager getSecretsManagerClient() {
        AWSCredentials credentials =
                new BasicAWSCredentials(accessKeyId, accessKeySecret);
        return AWSSecretsManagerClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(region)).build();
    }

}
