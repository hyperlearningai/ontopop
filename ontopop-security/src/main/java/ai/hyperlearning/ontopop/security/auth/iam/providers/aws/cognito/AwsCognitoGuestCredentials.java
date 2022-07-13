package ai.hyperlearning.ontopop.security.secrets.aws.secretsmanager;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * AWS Secrets Manager Guest Credentials Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AwsSecretsManagerGuestCredentials implements Serializable {

    private static final long serialVersionUID = -1465227188567966714L;
    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private long expiration;
    private String appId;
    
    public AwsSecretsManagerGuestCredentials() {
        
    }

    public AwsSecretsManagerGuestCredentials(String accessKeyId, 
            String secretAccessKey,
            String sessionToken, 
            long expiration, 
            String appId) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.expiration = expiration;
        this.appId = appId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessKeyId, secretAccessKey, sessionToken);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AwsSecretsManagerGuestCredentials other = 
                (AwsSecretsManagerGuestCredentials) obj;
        return Objects.equals(accessKeyId, other.accessKeyId)
                && Objects.equals(secretAccessKey, other.secretAccessKey)
                && Objects.equals(sessionToken, other.sessionToken);
    }

    @Override
    public String toString() {
        return "AwsSecretsManagerGuestCredentials ["
                + "accessKeyId=" + accessKeyId + ", "
                + "secretAccessKey=" + secretAccessKey + ", "
                + "sessionToken=" + sessionToken + ", "
                + "expiration=" + expiration + ", "
                + "appId=" + appId 
                + "]";
    }

}
