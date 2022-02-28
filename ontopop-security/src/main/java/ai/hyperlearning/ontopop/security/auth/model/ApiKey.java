package ai.hyperlearning.ontopop.security.auth.model;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    private LocalDateTime issueDate;
    private LocalDateTime expirationDate;
    private String issuer;
    private String client;
    private boolean enabled = false;
    private Set<String> roles;
    
    public ApiKey() {
        
    }

    public ApiKey(String key, LocalDateTime issueDate,
            LocalDateTime expirationDate, String issuer, String client,
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

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
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
