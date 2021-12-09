package ai.hyperlearning.ontopop.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.storage.aws.s3.AwsS3StorageService;
import ai.hyperlearning.ontopop.storage.azure.storage.AzureStorageBlobStorageService;
import ai.hyperlearning.ontopop.storage.local.LocalFileStorageService;

/**
 * File Storage Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class FileStorageServiceFactory {
	
	@Autowired
	private LocalFileStorageService localFileStorageService;
	
	@Autowired
	private AwsS3StorageService awsS3StorageService;
	
	@Autowired
	private AzureStorageBlobStorageService azureStorageBlobStorageService;
	
	/**
	 * Select the relevant file storage service
	 * @param type
	 * @return
	 */
	
	public FileStorageService getFileStorageService(String type) {
		
		FileStorageServiceType fileStorageServiceType = 
				FileStorageServiceType.valueOfLabel(type.toUpperCase());
		switch ( fileStorageServiceType ) {
			case LOCAL:
				return localFileStorageService;
			case AWS_S3:
				return awsS3StorageService;
			case AZURE_STORAGE:
				return azureStorageBlobStorageService;
			default:
				return localFileStorageService;
		}
		
	}
	
	public FileStorageService getFileStorageService(
		FileStorageServiceType fileStorageServiceType) {
		
		switch ( fileStorageServiceType ) {
			case LOCAL:
				return localFileStorageService;
			case AWS_S3:
				return awsS3StorageService;
			case AZURE_STORAGE:
				return azureStorageBlobStorageService;
			default:
				return localFileStorageService;
		}
		
	}

}
