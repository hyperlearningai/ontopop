package ai.hyperlearning.ontopop.security.auth;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Authorization Header Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationHeader implements Serializable {

    private static final long serialVersionUID = 4947371267484358985L;
    private String appId;
    
    public AuthorizationHeader() {
        
    }

    public AuthorizationHeader(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AuthorizationHeader other = (AuthorizationHeader) obj;
        return Objects.equals(appId, other.appId);
    }

    @Override
    public String toString() {
        return "AuthorizationHeader ["
                + "appId=" + appId 
                + "]";
    }

}
