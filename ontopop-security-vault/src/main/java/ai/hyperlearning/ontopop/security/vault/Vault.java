package ai.hyperlearning.ontopop.security.vault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport.KeyValueBackend;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;

/**
 * Spring Cloud Vault Common Methods
 *
 * @author jillurquddus
 * @param <T>
 * @since 2.0.0
 */

public class Vault<T> {

	@Autowired
	private VaultTemplate vaultTemplate;
	
	private String mountPath;
	private KeyValueBackend version;
	private VaultKeyValueOperations keyValueOps;
	
	/**
	 * Set the mount path (e.g. secret) and the KV version (e.g. KV_2)
	 * @param mountPath
	 * @param version
	 */
	
	public Vault(String mountPath, KeyValueBackend version) {
		this.mountPath = mountPath;
		this.version = version;
		this.keyValueOps = vaultTemplate
				.opsForKeyValue(mountPath, KeyValueBackend.versioned());
	}
	
	/**
	 * Get a value given a path (e.g. ontopop/development) 
	 * and key (e.g. spring.datasource.password)
	 * @param path
	 * @param key
	 * @return
	 */
	
	public Object get(String path, String key) throws NullPointerException {
		
		VaultResponse response = vaultTemplate
				.opsForKeyValue(mountPath, version)
				.get(path);
		return response.getData().get(key);
		
	}
	
	/**
	 * Get an object given a path (e.g. ontology/development/ontology/1)
	 * and class type
	 * @param path
	 * @param className
	 * @return
	 */
	
	public VaultResponseSupport<T> get(String path, Class<T> className) {
		
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
	
	public void put(String path, Object object) {
		keyValueOps.put(path, object);
	}
	
}
