package ai.hyperlearning.ontopop.security.vault;

import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

/**
 * Spring Cloud Vault Common Methods
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class Vault {
	
	/**
	 * Get a value given a path (e.g. ontopop/development) 
	 * and key (e.g. spring.datasource.password)
	 * @param path
	 * @param key
	 * @return
	 */
	
	public static Object get(VaultTemplate vaultTemplate, String mountPath, 
			KeyValueBackend version, String path, String key) 
					throws NullPointerException {
		
		VaultResponse response = vaultTemplate
				.opsForKeyValue(mountPath, version)
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
			String mountPath, KeyValueBackend version, 
			String path, Class<T> className) {
		
		return vaultTemplate
			.opsForKeyValue(mountPath, version)
			.get(path, className);
		
	}
	
	/**
	 * PUT an object given a path (e.g. ontology/development/ontology/1). 
	 * Note that PUT deletes all existing secrets at the given path.
	 * @param path
	 * @param object
	 */
	
	public static void put(VaultTemplate vaultTemplate, String mountPath, 
			String path, Object object) {
		
		VaultKeyValueOperations keyValueOps = vaultTemplate
				.opsForKeyValue(mountPath, KeyValueBackend.versioned());
		keyValueOps.put(path, object);
		
	}
	
}
