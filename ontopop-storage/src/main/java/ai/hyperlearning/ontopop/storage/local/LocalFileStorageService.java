package ai.hyperlearning.ontopop.storage.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.storage.ObjectStorageService;

/**
 * Local File Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class LocalFileStorageService implements ObjectStorageService {

	@Override
	public boolean doesObjectExist(String uri) {
		return Files.exists(Paths.get(uri));
	}

	@Override
	public boolean doesContainerExist(String uri) {
		return Files.exists(Paths.get(uri));
	}

	@Override
	public void createContainer(String uri) throws IOException {
		Files.createDirectories(Paths.get(uri));
	}

	@Override
	public void copyObject(String sourceUri, String targetUri) 
			throws IOException {
		Path source = Paths.get(sourceUri);
		Path target = Paths.get(targetUri);
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void copyContainerContents(String sourceContainerUri, 
			String targetContainerUri) throws IOException {
		File sourceDirectory = new File(sourceContainerUri);
		File targetDirectory = new File(targetContainerUri);
		FileUtils.copyDirectory(sourceDirectory, targetDirectory);
	}

	@Override
	public void uploadObject(String localSourceUri, String targetUri) 
			throws IOException {
		copyObject(localSourceUri, targetUri);
	}
	
	@Override
	public String downloadObject(String sourceUri, String filename) 
			throws IOException {
		Path temporaryFile = Files.createTempFile("", filename);
		copyObject(sourceUri, temporaryFile.toAbsolutePath().toString());
		return temporaryFile.toAbsolutePath().toString();
	}

	@Override
	public void downloadObject(String sourceUri, String targeContainertUri, 
			String filename) throws IOException {
		copyObject(sourceUri, targeContainertUri + File.separator + filename);
	}
	
	@Override
	public void cleanup() throws IOException  {
		
	}

}
