package ai.hyperlearning.ontopop.utils.storage.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.utils.storage.FileStorageService;

/**
 * Local File Storage Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
public class LocalFileStorageService implements FileStorageService {

	@Override
	public boolean doesFileExist(String uri) {
		return Files.exists(Paths.get(uri));
	}

	@Override
	public boolean doesDirectoryExist(String uri) {
		return Files.exists(Paths.get(uri));
	}

	@Override
	public void createDirectory(String uri) throws IOException {
		Files.createDirectory(Paths.get(uri));
	}

	@Override
	public void copyFile(String sourceUri, String targetUri) throws IOException {
		Path source = Paths.get(sourceUri);
		Path target = Paths.get(targetUri);
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	@Override
	public void copyDirectoryContents(String sourceDirectoryUri, 
			String targetDirectoryUri) throws IOException {
		File sourceDirectory = new File(sourceDirectoryUri);
		File targetDirectory = new File(targetDirectoryUri);
		FileUtils.copyDirectory(sourceDirectory, targetDirectory);
	}
	
	public void writeStringToLocalTempFile(
			String contents, String relativePath) throws IOException {
		
		Path tempFile = Files.createTempFile(null, null);
		
	}

}
