package ai.hyperlearning.ontopop.security.auth.iam.providers.aws.cognito;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenResult;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.hyperlearning.ontopop.security.auth.iam.IamIdentity;

/**
 * IAM Identity Service - AWS Cognito
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnExpression("'${security.authentication.api.enabled}'.equals('true') and "
        + "'${security.authentication.api.iam-guest-credentials.enabled}'.equals('true') and "
        + "'${security.iam.service}'.equals('aws-cognito')")
public class AwsCognitoIamIdentity implements IamIdentity {
    
    private static final ZoneId TIME_ZONE_UTC = ZoneId.of("UTC");
    
    @Value("${security.iam.aws-cognito.region}")
    private String region;

    @Override
    public String getOpenIdToken(String identity) 
            throws Exception {
        
        // Parse the credentials JSON object
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES
                .mappedFeature());
        AwsCognitoGuestCredentials guestCredentials = 
                mapper.readValue(identity, 
                        AwsCognitoGuestCredentials.class);
        
        // Check that the credentials have not expired
        LocalDateTime expirationDateTime = Instant
                .ofEpochSecond(guestCredentials.getExpiration())
                .atZone(TIME_ZONE_UTC)
                .toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now(TIME_ZONE_UTC);
        if ( expirationDateTime.isBefore(currentDateTime) )
            throw new NotAuthorizedException("Invalid Credentials");
        
        // Create a new Amazon Cognito Identity client using the
        // temporary credentials.
        AWSCredentials sessionCredentials = 
                new BasicSessionCredentials(
                        guestCredentials.getAccessKeyId(), 
                        guestCredentials.getSecretAccessKey(), 
                        guestCredentials.getSessionToken());
        AmazonCognitoIdentity identityClient = 
                AmazonCognitoIdentityClientBuilder.standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(sessionCredentials))
                .withRegion(region)
                .build();
        
        // Attempt to get an OpenID token using the guest credentials
        GetOpenIdTokenRequest getOpenIdTokenRequest = 
                new GetOpenIdTokenRequest();
        getOpenIdTokenRequest.setIdentityId(guestCredentials.getIdentityId());
        GetOpenIdTokenResult getOpenIdTokenResult = 
                identityClient.getOpenIdToken(getOpenIdTokenRequest);
        return getOpenIdTokenResult.getToken();
        
    }

}
