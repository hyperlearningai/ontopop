package ai.hyperlearning.ontopop.storage.azure.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;

import ai.hyperlearning.ontopop.storage.FileStorageService;

/**
 * Azure Storage Blob Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AzureStorageBlobStorageService implements FileStorageService {

	@Autowired
	private BlobContainerClient blobContainerClient;
	
	@Override
	public boolean doesFileExist(String uri) {
		
		// Instantiate a client that references a blob
		// in the given Azure Storage account
		BlockBlobClient blobClient = blobContainerClient
				.getBlobClient(uri)
				.getBlockBlobClient();
		return blobClient.exists();
		
	}

	@Override
	public boolean doesDirectoryExist(String uri) {
		
		// Returns whether the specified container exists
		return blobContainerClient.exists();
		
	}

	@Override
	public void createDirectory(String uri) throws IOException {
		
		// Create the specified container
		blobContainerClient.create();
		
	}

	@Override
	public void copyFile(String sourceUri, String targetUri) 
			throws IOException {
		
	}

	@Override
	public void copyDirectoryContents(
			String sourceDirectoryUri, String targetDirectoryUri) 
					throws IOException {
		
	}

	@Override
	public void uploadFile(String localSourceUri, String targetUri) 
			throws IOException {
		
		// Instantiate a client that references a to-be-created blob
		// in the given Azure Storage account
		BlockBlobClient blobClient = blobContainerClient
				.getBlobClient(targetUri)
				.getBlockBlobClient();
		
		// Upload the source file using the blob client
		File sourceFile = new File(localSourceUri);
		try (InputStream dataStream = new ByteArrayInputStream(
				FileUtils.readFileToByteArray(sourceFile))) {
			blobClient.upload(dataStream, sourceFile.length());
		}
		
	}
	
	@Override
	public void cleanup() throws IOException {
		
	}

}
