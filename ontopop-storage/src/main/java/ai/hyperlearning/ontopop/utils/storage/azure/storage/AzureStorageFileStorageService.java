package ai.hyperlearning.ontopop.utils.storage.azure.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.azure.storage.common.StorageSharedKeyCredential;

import ai.hyperlearning.ontopop.utils.storage.FileStorageService;

/**
 * Azure Storage File Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class AzureStorageFileStorageService implements FileStorageService {

	private BlobContainerClient blobContainerClient;
	
	public AzureStorageFileStorageService(
			@Value("${storage.file.azure-storage.account-name}") String accountName, 
			@Value("${storage.file.azure-storage.account-key}") String accountKey, 
			@Value("${storage.file.azure-storage.blob-endpoint}") String blobEndpoint, 
			@Value("${storage.file.azure-storage.container-name}") String containerName) {
		
		// Instantiate a client that references an existing container
		// in the given Azure Storage account
		StorageSharedKeyCredential credential = 
				new StorageSharedKeyCredential(accountName, accountKey);
		BlobServiceClient storageClient = 
				new BlobServiceClientBuilder()
					.endpoint(blobEndpoint)
				 	.credential(credential)
				 	.buildClient();
		blobContainerClient = 
				storageClient.getBlobContainerClient(containerName);
		
	}
	
	@Override
	public boolean doesFileExist(String uri) {
		
		// Instantiate a client that references a blob
		// in the given Azure Storage account
		BlockBlobClient blobClient = blobContainerClient
				.getBlobClient(uri)
				.getBlockBlobClient();
		boolean doesBlobExist = blobClient.exists();
		blobClient.delete();
		return doesBlobExist;
		
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
		
		// Close the client
		blobClient.delete();
		
	}
	
	@Override
	public void cleanup() throws IOException {
		blobContainerClient.delete();
	}

}
