package ai.hyperlearning.ontopop.utils.storage;

import java.io.IOException;

/**
 * File Storage Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface FileStorageService {

	boolean doesFileExist(String uri);
	
	boolean doesDirectoryExist(String uri);
	
	void createDirectory(String uri) throws IOException;
	
	void copyFile(String sourceUri, String targetUri) throws IOException;
	
	void copyDirectoryContents(String sourceDirectoryUri, 
			String targetDirectoryUri) throws IOException;
	
	void uploadFile(String localSourceUri, String targetUri) throws IOException;
	
}
