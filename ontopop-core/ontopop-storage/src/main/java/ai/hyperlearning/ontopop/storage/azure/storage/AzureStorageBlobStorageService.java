package ai.hyperlearning.ontopop.storage.azure.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.specialized.BlockBlobClient;

import ai.hyperlearning.ontopop.storage.ObjectStorageService;

/**
 * Azure Storage Blob Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.object.service",
        havingValue = "azure-storage")
public class AzureStorageBlobStorageService implements ObjectStorageService {

    @Autowired
    private BlobContainerClient blobContainerClient;

    @Override
    public boolean doesObjectExist(String uri) {

        // Instantiate a client that references a blob
        // in the given Azure Storage account
        BlockBlobClient blobClient =
                blobContainerClient.getBlobClient(uri).getBlockBlobClient();
        return blobClient.exists();

    }

    @Override
    public boolean doesContainerExist(String uri) {

        // Returns whether the specified container exists
        return blobContainerClient.exists();

    }

    @Override
    public void createContainer(String uri) throws IOException {

        // Create the specified container
        blobContainerClient.create();

    }

    @Override
    public void copyObject(String sourceUri, String targetUri)
            throws IOException {

    }

    @Override
    public void copyContainerContents(String sourceContainerUri,
            String targetContainerUri) throws IOException {

    }

    @Override
    public void uploadObject(String localSourceUri, String targetUri)
            throws IOException {

        // Instantiate a client that references a to-be-created blob
        // in the given Azure Storage account
        BlockBlobClient blobClient = blobContainerClient
                .getBlobClient(targetUri).getBlockBlobClient();

        // Upload the source file using the blob client
        File sourceFile = new File(localSourceUri);
        try (InputStream dataStream = new ByteArrayInputStream(
                FileUtils.readFileToByteArray(sourceFile))) {
            blobClient.upload(dataStream, sourceFile.length());
        }

    }

    @Override
    public String downloadObject(String sourceUri, String filename)
            throws IOException {

        // Instantiate a client that references a blob
        // in the given Azure Storage account
        BlockBlobClient blobClient = blobContainerClient
                .getBlobClient(sourceUri).getBlockBlobClient();

        // Download the source file to a local temporary file
        Path temporaryFile = Files.createTempFile("", filename);
        blobClient.downloadToFile(temporaryFile.toAbsolutePath().toString(),
                true);
        return temporaryFile.toAbsolutePath().toString();

    }

    @Override
    public void downloadObject(String sourceUri, String targetContainerUri,
            String filename) throws IOException {

    }

    @Override
    public void cleanup() throws IOException {

    }

}
