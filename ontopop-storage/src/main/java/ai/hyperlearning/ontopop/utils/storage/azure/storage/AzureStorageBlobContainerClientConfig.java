package ai.hyperlearning.ontopop.utils.storage.azure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

/**
 * Azure Storage Blob Container Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class AzureStorageBlobContainerClientConfig {
	
	@Value("${storage.file.azure-storage.account-name}")
	private String accountName;
	
	@Value("${storage.file.azure-storage.account-key}")
	private String accountKey; 
	
	@Value("${storage.file.azure-storage.blob-endpoint}")
	private String blobEndpoint;
	
	@Value("${storage.file.azure-storage.container-name}")
	private String containerName;
	
	@Bean
	public BlobContainerClient getBlobContainerClient() {
		
		// Instantiate a client that references an existing container
		// in the given Azure Storage account
		StorageSharedKeyCredential credential = 
				new StorageSharedKeyCredential(accountName, accountKey);
		BlobServiceClient storageClient = 
				new BlobServiceClientBuilder()
					.endpoint(blobEndpoint)
				 	.credential(credential)
				 	.buildClient();
		return storageClient.getBlobContainerClient(containerName);
		
	}

}
