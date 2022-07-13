package ai.hyperlearning.ontopop.security.auth.iam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.security.auth.iam.providers.aws.cognito.AwsCognitoIamIdentity;

/**
 * IAM Identity Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class IamIdentityProviderFactory {
    
    @Autowired(required = false)
    private AwsCognitoIamIdentity awsCognitoIamIdentity;
    
    /**
     * Select the relevant IAM Identity service
     * 
     * @param type
     * @return
     */

    public IamIdentity getIamIdentityService(String provider) {

        IamIdentityProvider iamIdentityProvider =
                IamIdentityProvider.valueOfLabel(provider.toUpperCase());
        switch (iamIdentityProvider) {
            case AWS_COGNITO:
                return awsCognitoIamIdentity;
            default:
                return null;
        }

    }
    
    public IamIdentity getIamIdentityService(
            IamIdentityProvider iamIdentityProvider) {

        switch (iamIdentityProvider) {
            case AWS_COGNITO:
                return awsCognitoIamIdentity;
            default:
                return null;
        }

    }

}
