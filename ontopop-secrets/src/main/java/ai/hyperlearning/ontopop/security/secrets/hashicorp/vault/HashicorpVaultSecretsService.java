package ai.hyperlearning.ontopop.security.secrets.hashicorp.vault;

import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

/**
 * Hashicorp Vault Secrets Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class HashicorpVaultSecretsService {
	
	private static final KeyValueBackend KV_BACKEND_VERSION = 
			KeyValueBackend.KV_2;
	
	/**
	 * Get a value given a path (e.g. ontopop/development) 
	 * and key (e.g. spring.datasource.password)
	 * @param path
	 * @param key
	 * @return
	 */
	
	public static Object get(VaultTemplate vaultTemplate, String mountPath, 
			String path, String key) throws NullPointerException {
		
		VaultResponse response = vaultTemplate
				.opsForKeyValue(mountPath, KV_BACKEND_VERSION)
				.get(path);
		return response.getData().get(key);
		
	}
	
	/**
	 * Get an object given a path (e.g. ontology/development/ontology/1)
	 * and class type
	 * @param <T>
	 * @param path
	 * @param className
	 * @return
	 */
	
	public static <T> VaultResponseSupport<T> get(VaultTemplate vaultTemplate, 
			String mountPath, String path, Class<T> className) {
		
		return vaultTemplate
			.opsForKeyValue(mountPath, KV_BACKEND_VERSION)
			.get(path, className);
		
	}
	
	/**
	 * PUT an object given a path (e.g. ontology/development/ontology/1). 
	 * Note that PUT deletes all existing secrets at the given path before
	 * creating the new secret.
	 * @param path
	 * @param object
	 */
	
	public static void put(VaultTemplate vaultTemplate, String mountPath, 
			String path, Object object) {
		
		VaultKeyValueOperations keyValueOps = vaultTemplate
				.opsForKeyValue(mountPath, KeyValueBackend.versioned());
		keyValueOps.put(path, object);
		
	}
	
	/**
	 * Delete all secrets at a given path 
	 * (e.g. ontology/development/ontology/1).
	 * @param vaultTemplate
	 * @param mountPath
	 * @param path
	 */
	
	public static void delete(VaultTemplate vaultTemplate, String mountPath, 
			String path ) {
		
		VaultKeyValueOperations keyValueOps = vaultTemplate
				.opsForKeyValue(mountPath, KeyValueBackend.versioned());
		keyValueOps.delete(path);
		
	}
	
}
