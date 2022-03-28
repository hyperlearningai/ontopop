package ai.hyperlearning.ontopop.security.secrets.azure.keyvault;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

/**
 * Microsoft Azure Key Vault Secret Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value = "security.secrets.service",
        havingValue = "azure-key-vault")
public class AzureKeyVaultSecretClientConfig {

    @Value("${security.secrets.azure-key-vault.url}")
    private String vaultUrl;
    
    @Value("${security.secrets.azure-key-vault.clientId}")
    private String clientId;
    
    @Value("${security.secrets.azure-key-vault.clientSecret}")
    private String clientSecret;
    
    @Value("${security.secrets.azure-key-vault.tenantId}")
    private String tenantId;

    @Bean
    public SecretClient getSecretClient() {
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
        return new SecretClientBuilder().vaultUrl(vaultUrl)
                .credential(credential)
                .buildClient();
    }

}
