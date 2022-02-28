package ai.hyperlearning.ontopop.security.auth.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Collection of API Keys
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class ApiKeys implements Serializable {

    private static final long serialVersionUID = -7313459138490720602L;
    private Map<String, ApiKey> apiKeys = new HashMap<>();
    
    public ApiKeys() {
        
    }

    public ApiKeys(Map<String, ApiKey> apiKeys) {
        super();
        this.apiKeys = apiKeys;
    }

    public Map<String, ApiKey> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(Map<String, ApiKey> apiKeys) {
        this.apiKeys = apiKeys;
    }

    @Override
    public String toString() {
        return "ApiKeys ["
                + "apiKeys=" + apiKeys 
                + "]";
    }

}
