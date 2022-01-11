package ai.hyperlearning.ontopop.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.storage.aws.s3.AwsS3StorageService;
import ai.hyperlearning.ontopop.storage.azure.storage.AzureStorageBlobStorageService;
import ai.hyperlearning.ontopop.storage.local.LocalFileStorageService;

/**
 * Object Storage Service Factory
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class ObjectStorageServiceFactory {

    @Autowired
    private LocalFileStorageService localFileStorageService;

    @Autowired(required = false)
    private AwsS3StorageService awsS3StorageService;

    @Autowired(required = false)
    private AzureStorageBlobStorageService azureStorageBlobStorageService;

    /**
     * Select the relevant object storage service
     * 
     * @param type
     * @return
     */

    public ObjectStorageService getObjectStorageService(String type) {

        ObjectStorageServiceType objectStorageServiceType =
                ObjectStorageServiceType.valueOfLabel(type.toUpperCase());
        switch (objectStorageServiceType) {
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

    public ObjectStorageService getObjectStorageService(
            ObjectStorageServiceType objectStorageServiceType) {

        switch (objectStorageServiceType) {
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
