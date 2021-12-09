package ai.hyperlearning.ontopop.storage;

import java.io.IOException;

/**
 * Object Storage Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface ObjectStorageService {
	
	boolean doesObjectExist(String uri);
	
	boolean doesContainerExist(String uri);
	
	void createContainer(String uri) throws IOException;
	
	void copyObject(String sourceUri, String targetUri) throws IOException;
	
	void copyContainerContents(String sourceContainerUri, 
			String targetContainerUri) throws IOException;
	
	void uploadObject(String localSourceUri, String targetUri) throws IOException;
	
	void cleanup() throws IOException;
	
}
