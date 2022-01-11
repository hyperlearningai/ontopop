package ai.hyperlearning.ontopop.security.secrets;

/**
 * Secrets Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface SecretsService {

    public String get(String key);

    public void set(String key, Object value) throws Exception;

    public void delete(String key) throws Exception;

}
