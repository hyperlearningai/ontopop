package ai.hyperlearning.ontopop.security.auth.api.apikey.engines;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.security.auth.api.apikey.ApiKeyAuthentication;
import ai.hyperlearning.ontopop.security.auth.api.apikey.model.ApiKey;
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
@ConditionalOnExpression("'${security.authentication.api.enabled}'.equals('true') and "
        + "'${security.authentication.api.apiKeyLookup.enabled}'.equals('true') and "
        + "'${security.authentication.api.apiKeyLookup.engine}'.equals('secrets')")
public class SecretsEngineApiKeyAuthentication 
        extends ApiKeyAuthentication {
    
    private static final DateTimeFormatter API_KEY_DEFAULT_DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final int API_KEY_DEFAULT_LENGTH = 24;
    private static final int API_KEY_DEFAULT_DURATION_DAYS = 90;
    private static final String API_KEY_DEFAULT_ISSUER = "HyperLearning AI";
    private static final String API_KEY_PREFIX = "APIKEY_";
    
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

    /**
     * Identify and instantiate the secrets service implementation client.
     * For example a AWS Secrets Manager or Azure Key Vault client.
     */
    
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

    /**
     * Retrieve a given API Key.
     */
    
    @Override
    public ApiKey get(String key) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = useVault ? (String) HashicorpVaultSecretsService.get(
                vaultTemplate,
                springCloudVaultKvBackend, 
                springCloudVaultKvDefaultContext,  
                API_KEY_PREFIX + key) : 
                    secretsService.get(API_KEY_PREFIX + key);
        return json != null ? mapper.readValue(json, ApiKey.class) : null;
    }
    
    /**
     * Retrieve a given API Key using temporary credentials.
     * @throws Exception 
     */
    
    @Override
    public ApiKey get(String temporaryCredentials, String key) 
            throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        
        // HashiCorp Vault
        if (useVault) 
            json = (String) HashicorpVaultSecretsService.get(
                    vaultTemplate,
                    springCloudVaultKvBackend, 
                    springCloudVaultKvDefaultContext,  
                    API_KEY_PREFIX + key);
        
        // Other secrets engines (e.g. AWS Secrets Manager or Azure Key Vault)
        else {
            
            // Create a temporary client to the secrets engine
            Object secretsEngineClient = secretsService
                    .getTemporaryClient(temporaryCredentials);
            json = secretsService.get(secretsEngineClient, 
                    API_KEY_PREFIX + key);
            
        }
        
        // Return the parsed API Key object
        return json != null ? mapper.readValue(json, ApiKey.class) : null;
    
    }

    /**
     * Create an API Key given an API Key object.
     */
    
    @Override
    public void create(ApiKey apiKey) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(apiKey);
        if ( useVault )
            HashicorpVaultSecretsService.put(vaultTemplate,
                    springCloudVaultKvBackend,
                    springCloudVaultKvDefaultContext 
                        + "/" + API_KEY_PREFIX + apiKey.getKey(),
                        json);
        else secretsService.set(API_KEY_PREFIX + apiKey.getKey(), json);
    }
    
    /**
     * Create an API Key using default values and given the 
     * client application name.
     */
    
    @Override
    public void create(String client) throws Exception {
        ApiKey apiKey = new ApiKey();
        apiKey.setKey(RandomStringUtils.randomAlphanumeric(
                API_KEY_DEFAULT_LENGTH).toUpperCase());
        apiKey.setIssueDate(LocalDateTime.now(ZoneOffset.UTC)
                .format(API_KEY_DEFAULT_DATE_TIME_FORMATTER));
        apiKey.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC)
                .plusDays(API_KEY_DEFAULT_DURATION_DAYS)
                .format(API_KEY_DEFAULT_DATE_TIME_FORMATTER));
        apiKey.setIssuer(API_KEY_DEFAULT_ISSUER);
        apiKey.setClient(client);
        apiKey.setEnabled(true);
        create(apiKey);
    }

    /**
     * Create an API Key using default values but given the
     * client application name and explicit set of authorities.
     */
    
    @Override
    public void create(String client, Set<String> roles) throws Exception {
        ApiKey apiKey = new ApiKey();
        apiKey.setKey(RandomStringUtils.random(
                API_KEY_DEFAULT_LENGTH, true, true).toUpperCase());
        apiKey.setIssueDate(LocalDateTime.now(ZoneOffset.UTC)
                .format(API_KEY_DEFAULT_DATE_TIME_FORMATTER));
        apiKey.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC)
                .plusDays(API_KEY_DEFAULT_DURATION_DAYS)
                .format(API_KEY_DEFAULT_DATE_TIME_FORMATTER));
        apiKey.setIssuer(API_KEY_DEFAULT_ISSUER);
        apiKey.setClient(client);
        apiKey.setEnabled(true);
        apiKey.setRoles(roles);
        create(apiKey);
    }
    
    /**
     * Create an API Key using default values but given the
     * client application name, explicit set of authorities and duration.
     */
    
    @Override
    public void create(String client, Set<String> roles, int durationDays) 
            throws Exception {
        ApiKey apiKey = new ApiKey();
        apiKey.setKey(RandomStringUtils.random(
                API_KEY_DEFAULT_LENGTH, true, true).toUpperCase());
        apiKey.setIssueDate(LocalDateTime.now(ZoneOffset.UTC)
                .format(API_KEY_DEFAULT_DATE_TIME_FORMATTER));
        apiKey.setExpirationDate(LocalDateTime.now(ZoneOffset.UTC)
                .plusDays(durationDays)
                .format(API_KEY_DEFAULT_DATE_TIME_FORMATTER));
        apiKey.setIssuer(API_KEY_DEFAULT_ISSUER);
        apiKey.setClient(client);
        apiKey.setEnabled(true);
        apiKey.setRoles(roles);
        create(apiKey);
    }

    /**
     * Delete a given API Key.
     */
    
    @Override
    public void delete(String key) throws Exception {
        if ( useVault )
            HashicorpVaultSecretsService.delete(vaultTemplate, 
                    springCloudVaultKvBackend, 
                    springCloudVaultKvDefaultContext 
                        + "/" + API_KEY_PREFIX + key);
        else secretsService.delete(API_KEY_PREFIX + key);
    }

    /**
     * Authenticate a given API Key by checking that it exists and that 
     * the expiration date has not passed.
     */
    
    @Override
    public boolean authenticate(String key) throws JsonProcessingException {
        ApiKey apiKey = get(key);
        if ( apiKey!= null ) {
            LocalDateTime expirationDate = LocalDateTime.parse(
                    apiKey.getExpirationDate(), 
                    API_KEY_DEFAULT_DATE_TIME_FORMATTER);
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            return apiKey.isEnabled() && expirationDate.isAfter(now);
        }
        return false;
    }
    
    /**
     * Authenticate a given API Key object.
     */
    
    @Override
    public boolean authenticate(ApiKey apiKey) throws JsonProcessingException {
        return authenticate(apiKey.getKey());
    }

}
