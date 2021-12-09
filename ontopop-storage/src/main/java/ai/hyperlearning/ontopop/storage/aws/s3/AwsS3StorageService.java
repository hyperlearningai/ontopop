package ai.hyperlearning.ontopop.storage.aws.s3;

import java.io.IOException;

import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.storage.FileStorageService;

/**
 * AWS S3 File Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AwsS3FileStorageService implements FileStorageService {

	@Override
	public boolean doesFileExist(String uri) {
		return false;
	}

	@Override
	public boolean doesDirectoryExist(String uri) {
		return false;
	}

	@Override
	public void createDirectory(String uri) throws IOException {
		
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
		
	}

	@Override
	public void cleanup() throws IOException {
		
	}

}
