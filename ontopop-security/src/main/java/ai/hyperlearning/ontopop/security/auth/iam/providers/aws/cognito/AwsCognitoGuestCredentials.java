package ai.hyperlearning.ontopop.security.auth.iam.providers.aws.cognito;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * AWS Cognito Guest Credentials Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AwsCognitoGuestCredentials implements Serializable {

    private static final long serialVersionUID = -1465227188567966714L;
    private String accessKeyId;
    private String secretAccessKey;
    private String identityId;
    private String sessionToken;
    private long expiration;
    
    public AwsCognitoGuestCredentials() {
        
    }

    public AwsCognitoGuestCredentials(String accessKeyId, 
            String secretAccessKey,
            String sessionToken, 
            String identityId, 
            long expiration) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.identityId = identityId;
        this.expiration = expiration;
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

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
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
        AwsCognitoGuestCredentials other = 
                (AwsCognitoGuestCredentials) obj;
        return Objects.equals(accessKeyId, other.accessKeyId)
                && Objects.equals(secretAccessKey, other.secretAccessKey)
                && Objects.equals(sessionToken, other.sessionToken);
    }

    @Override
    public String toString() {
        return "AwsCognitoGuestCredentials ["
                + "identityId=" + identityId + ", "
                + "sessionToken=" + sessionToken + ", "
                + "expiration=" + expiration 
                + "]";
    }

}
