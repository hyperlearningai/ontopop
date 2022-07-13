package ai.hyperlearning.ontopop.security.auth.iam;

/**
 * IAM Identity Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface IamIdentity {
    
    public String getOpenIdToken(String identity) throws Exception;

}
