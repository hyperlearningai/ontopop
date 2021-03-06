package ai.hyperlearning.ontopop.storage.aws.s3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import ai.hyperlearning.ontopop.storage.ObjectStorageService;

/**
 * AWS S3 Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(value = "storage.object.service", havingValue = "aws-s3")
public class AwsS3StorageService implements ObjectStorageService {

    @Autowired
    private AmazonS3 s3;

    @Value("${storage.object.aws-s3.bucket-name}")
    private String bucketName;

    @Override
    public boolean doesObjectExist(String uri) {
        return s3.doesObjectExist(bucketName, uri);
    }

    @Override
    public boolean doesContainerExist(String uri) {

        // Returns whether the specified bucket exists
        return s3.doesBucketExistV2(bucketName);

    }

    @Override
    public void createContainer(String uri) throws IOException {

        // Create the specified container
        s3.createBucket(bucketName);

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

        // Upload the source file using the S3 client
        s3.putObject(bucketName, targetUri, new File(localSourceUri));

    }

    @Override
    public String downloadObject(String sourceUri, String filename)
            throws IOException {

        // Download the source file to a temporary local file
        Path temporaryFile = Files.createTempFile("", filename);
        S3Object s3object = s3.getObject(bucketName, sourceUri);
        try (S3ObjectInputStream inputStream = s3object.getObjectContent()) {
            FileUtils.copyInputStreamToFile(inputStream,
                    new File(temporaryFile.toAbsolutePath().toString()));
        }

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
