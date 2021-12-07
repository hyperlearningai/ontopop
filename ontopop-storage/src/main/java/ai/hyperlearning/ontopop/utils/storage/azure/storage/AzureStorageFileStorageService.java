package ai.hyperlearning.ontopop.utils.storage.azure.storage;

import java.io.IOException;

import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.utils.storage.FileStorageService;

/**
 * Azure Storage File Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AzureStorageFileStorageService implements FileStorageService {

	@Override
	public boolean doesFileExist(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doesDirectoryExist(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createDirectory(String uri) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyFile(String sourceUri, String targetUri) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyDirectoryContents(String sourceDirectoryUri, String targetDirectoryUri) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uploadFile(String localSourceUri, String targetUri) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
