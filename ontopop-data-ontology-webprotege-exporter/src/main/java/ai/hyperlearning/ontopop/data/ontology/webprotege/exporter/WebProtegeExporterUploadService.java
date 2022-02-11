package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.storage.ObjectStorageService;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceFactory;
import ai.hyperlearning.ontopop.storage.ObjectStorageServiceType;

/**
 * WebProtege Exporter Upload Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class WebProtegeExporterUploadService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeExporterUploadService.class);
    
    @Autowired
    private ObjectStorageServiceFactory objectStorageServiceFactory;
    
    @Value("${storage.object.service}")
    private String storageObjectService;

    @Value("${storage.object.local.baseUri}")
    private String storageLocalBaseUri;
    
    @Value("${storage.object.containers.webprotege.exported}")
    private String webProtegeExportedDirectoryName;
    
    private ObjectStorageService objectStorageService;
    private String extractedOwlAbsolutePath;
    private String writeDirectoryUri;
    
    /**
     * Run the WebProtege exporter upload service
     * @param extractedOwlAbsolutePath
     * @throws Exception 
     */
    
    public void run(String extractedOwlAbsolutePath) throws Exception {
        
        LOGGER.info("WebProtege exporter upload service started.");
        this.extractedOwlAbsolutePath = extractedOwlAbsolutePath;
        
        try {
            
            // 1. Environment setup
            setup();
            
            // 2. Upload the exported OWL file to object storage
            upload();
            
        } finally {
            
            // 3. Close all storage clients
            cleanup();
            
        }
        
        LOGGER.info("WebProtege exporter upload service finished.");
        
    }
    
    /**
     * Instantiate the relevant object storage service
     * @throws IOException
     */
    
    private void setup() throws IOException {
        
        // 1. Select the relevant persistent storage service and
        // create the target directory if it does not already exist
        ObjectStorageServiceType objectStorageServiceType =
                ObjectStorageServiceType
                        .valueOfLabel(storageObjectService.toUpperCase());
        objectStorageService = objectStorageServiceFactory
                .getObjectStorageService(objectStorageServiceType);
        LOGGER.debug("Using the {} object storage service.",
                objectStorageServiceType);
        
        // 2. Define and create (if required) the relevant
        // target exported directory
        switch (objectStorageServiceType) {

            case LOCAL:

                // Create (if required) the local target loaded directory
                writeDirectoryUri = storageLocalBaseUri + File.separator
                        + webProtegeExportedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);
                break;

            default:

                // Create (if required) the Azure Storage container
                // or AWS S3 bucket
                writeDirectoryUri = webProtegeExportedDirectoryName;
                if (!objectStorageService.doesContainerExist(writeDirectoryUri))
                    objectStorageService.createContainer(writeDirectoryUri);

        }
        
    }
    
    /**
     * Upload the downloaded WebProtege exported OWL file to object storage
     * @throws IOException 
     */
    
    private void upload() throws IOException {
        
        LOGGER.info("WebProtege exporter upload service - "
                + "Started the persistence of the exported resource.");
        String exportedFilename = Paths.get(extractedOwlAbsolutePath)
                .getFileName().toString();
        String targetFilepath = writeDirectoryUri + "/" + exportedFilename;
        objectStorageService.uploadObject(
                extractedOwlAbsolutePath, targetFilepath);
        LOGGER.debug(
                "Successfully persisted exported ontology resource to '{}'.",
                targetFilepath);
        LOGGER.info("WebProtege exporter upload service - "
                + "Finished the persistence of the exported resource.");
        
    }
    
    /**
     * Cleanup any open resources
     * 
     * @throws Exception
     */

    private void cleanup() throws Exception {

        // Close any storage service clients
        objectStorageService.cleanup();
        
    }

}
