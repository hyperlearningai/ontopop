package ai.hyperlearning.ontopop.storage.aws.s3;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;

import ai.hyperlearning.ontopop.storage.FileStorageService;

/**
 * AWS S3 Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AwsS3StorageService implements FileStorageService {
	
	@Autowired
	private AmazonS3 s3;
	
	@Value("${storage.file.aws-s3.bucket-name}")
	private String bucketName;

	@Override
	public boolean doesFileExist(String uri) {
		return false;
	}

	@Override
	public boolean doesDirectoryExist(String uri) {
		
		// Returns whether the specified bucket exists
		return s3.doesBucketExistV2(bucketName);
		
	}

	@Override
	public void createDirectory(String uri) throws IOException {
		
		// Create the specified container
		s3.createBucket(bucketName);
		
	}

	@Override
	public void copyFile(String sourceUri, String targetUri) 
			throws IOException {
		
	}

	@Override
	public void copyDirectoryContents(String sourceDirectoryUri, 
			String targetDirectoryUri) throws IOException {
		
	}

	@Override
	public void uploadFile(String localSourceUri, String targetUri) 
			throws IOException {
		
		// Upload the source file using the S3 client
		s3.putObject(
				bucketName,
				targetUri,
				new File(localSourceUri));
		
	}

	@Override
	public void cleanup() throws IOException {
		
	}

}
