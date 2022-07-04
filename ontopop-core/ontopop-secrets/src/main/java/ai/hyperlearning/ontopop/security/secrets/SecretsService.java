package ai.hyperlearning.ontopop.security.secrets;

/**
 * Secrets Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface SecretsService {
    
    public Object getTemporaryClient(String credentials) throws Exception;
    
    public String get(String key);
    
    public String get(Object client, String key);

    public void set(String key, Object value) throws Exception;
    
    public void set(Object client, String key, Object value) throws Exception;

    public void delete(String key) throws Exception;
    
    public void delete(Object client, String key) throws Exception;

}
