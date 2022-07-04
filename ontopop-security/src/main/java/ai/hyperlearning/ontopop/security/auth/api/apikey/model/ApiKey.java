package ai.hyperlearning.ontopop.security.auth.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * API Key Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class ApiKey implements Serializable {

    private static final long serialVersionUID = 8216664197904580728L;
    private String key;
    private String issueDate;
    private String expirationDate;
    private String issuer;
    private String client;
    private boolean enabled = false;
    private Set<String> roles = new HashSet<>();
    
    public ApiKey() {
        
    }

    public ApiKey(String key, String issueDate,
            String expirationDate, String issuer, String client,
            boolean enabled, Set<String> roles) {
        super();
        this.key = key;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.issuer = issuer;
        this.client = client;
        this.enabled = enabled;
        this.roles = roles;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate.toString();
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate.toString();
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApiKey other = (ApiKey) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ApiKey ["
                + "issueDate=" + issueDate + ", "
                + "expirationDate=" + expirationDate + ", "
                + "issuer=" + issuer + ", "
                + "client=" + client + ", "
                + "enabled=" + enabled + ", "
                + "roles=" + roles
                + "]";
    }
    
}
