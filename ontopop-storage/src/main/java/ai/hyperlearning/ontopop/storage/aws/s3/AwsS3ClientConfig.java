package ai.hyperlearning.ontopop.storage.aws.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * AWS S3 Storage Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class AwsS3ClientConfig {
	
	@Value("${storage.file.aws-s3.access-key-id}")
	private String accessKeyId;
	
	@Value("${storage.file.aws-s3.access-key-secret}")
	private String accessKeySecret;
	
	@Value("${storage.file.aws-s3.region}")
	private String region;
	
	@Bean
	public AmazonS3 getS3Client() {
		
		// Instantiate a client to AWS S3
		AWSCredentials credentials = new BasicAWSCredentials(
				accessKeyId, accessKeySecret);
		return AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.fromName(region))
				.build();
		
	}

}
