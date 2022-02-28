package ai.hyperlearning.ontopop.security.auth.providers;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.security.auth.ApiKeyAuthenticationService;
import ai.hyperlearning.ontopop.security.auth.model.ApiKey;
import ai.hyperlearning.ontopop.security.auth.model.ApiKeys;
import ai.hyperlearning.ontopop.security.secrets.SecretsService;
import ai.hyperlearning.ontopop.security.secrets.SecretsServiceFactory;
import ai.hyperlearning.ontopop.security.secrets.SecretsServiceType;
import ai.hyperlearning.ontopop.security.secrets.hashicorp.vault.HashicorpVaultSecretsService;

/**
 * API Key Authentication Service Implementation - Secrets Engine
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class SecretsEngineApiKeyAuthenticationService 
        implements ApiKeyAuthenticationService {
    
    private static final String SECRET_KV_KEY = "apikeys";
    
    @Autowired(required = false)
    private VaultTemplate vaultTemplate;

    @Autowired
    private SecretsServiceFactory secretsServiceFactory;
    
    @Value("${spring.cloud.vault.kv.backend}")
    private String springCloudVaultKvBackend;

    @Value("${spring.cloud.vault.kv.default-context}")
    private String springCloudVaultKvDefaultContext;

    @Value("${security.secrets.service}")
    private String securitySecretsService;
    
    private SecretsService secretsService = null;
    private boolean useVault = false;

    @PostConstruct
    public void init() {

        // Ascertain whether to use a platform-specific secrets manager
        // or Hashicorp Vault
        SecretsServiceType secretsServiceType = SecretsServiceType
                .valueOfLabel(securitySecretsService.toUpperCase());
        secretsService =
                secretsServiceFactory.getSecretsService(secretsServiceType);
        if (secretsService == null && secretsServiceType
                .equals(SecretsServiceType.HASHICORP_VAULT))
            useVault = true;

    }
    
    @Override
    public ApiKeys getApiKeys() 
            throws JsonMappingException, JsonProcessingException {
        
        // Get the API Keys secret value
        ObjectMapper mapper = new ObjectMapper();
        String json = useVault ? HashicorpVaultSecretsService.get(
                vaultTemplate,
                springCloudVaultKvBackend, 
                springCloudVaultKvDefaultContext + "/" + SECRET_KV_KEY, 
                String.class).getData() : secretsService.get(SECRET_KV_KEY);
        return json != null ? mapper.readValue(json, ApiKeys.class) : null;
        
    }

    @Override
    public ApiKey get(String key) 
            throws JsonMappingException, JsonProcessingException {
        ApiKeys apiKeys = getApiKeys();
        if ( apiKeys != null ) {
            Map<String, ApiKey> apiKeysMap = apiKeys.getApiKeys();
            if ( !apiKeysMap.isEmpty() && apiKeysMap.containsKey(key) ) {
                return apiKeysMap.get(key);
            }
        }
        return null;
    }

    @Override
    public void create(ApiKey apiKey) 
            throws Exception {
        ApiKeys apiKeys = getApiKeys();
        if ( apiKeys == null )
            apiKeys = new ApiKeys();
        Map<String, ApiKey> apiKeysMap = apiKeys.getApiKeys();
        apiKeysMap.put(apiKey.getKey(), apiKey);
        if ( useVault )
            HashicorpVaultSecretsService.put(vaultTemplate,
                    springCloudVaultKvBackend,
                    springCloudVaultKvDefaultContext + "/" + SECRET_KV_KEY,
                    apiKeysMap);
        else secretsService.set(SECRET_KV_KEY, apiKeysMap.toString());
    }

    @Override
    public void delete(String key) throws Exception {
        ApiKeys apiKeys = getApiKeys();
        if ( apiKeys != null ) {
            Map<String, ApiKey> apiKeysMap = apiKeys.getApiKeys();
            if ( apiKeysMap.containsKey(key) ) {
                apiKeysMap.remove(key);
                if ( useVault )
                    HashicorpVaultSecretsService.put(vaultTemplate,
                            springCloudVaultKvBackend,
                            springCloudVaultKvDefaultContext 
                                + "/" + SECRET_KV_KEY,
                            apiKeysMap.toString());
                else secretsService.set(SECRET_KV_KEY, apiKeysMap.toString());
            }
        }
    }

    @Override
    public void setEnabled(String key, boolean enabled) 
            throws Exception {
        ApiKey apiKey = get(key);
        if ( apiKey != null ) {
            apiKey.setEnabled(enabled);
            ApiKeys apiKeys = getApiKeys();
            if ( apiKeys != null ) {
                Map<String, ApiKey> apiKeysMap = apiKeys.getApiKeys();
                apiKeysMap.put(apiKey.getKey(), apiKey);
                if ( useVault )
                    HashicorpVaultSecretsService.put(vaultTemplate,
                            springCloudVaultKvBackend,
                            springCloudVaultKvDefaultContext 
                                + "/" + SECRET_KV_KEY,
                            apiKeysMap.toString());
                else secretsService.set(SECRET_KV_KEY, apiKeysMap.toString());
            }
        }
    }

    @Override
    public boolean authenticate(String key) 
            throws JsonMappingException, JsonProcessingException {
        ApiKey apiKey = get(key);
        if ( apiKey!= null ) {
            return apiKey.isEnabled();
        }
        return false;
    }

}
