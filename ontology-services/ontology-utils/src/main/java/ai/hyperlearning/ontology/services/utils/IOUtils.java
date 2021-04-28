package ai.hyperlearning.ontology.services.utils;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * IO Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class IOUtils {
	
	/**
	 * List all files in a given directory (non-recursive) and
	 * filter based on a given extensions filter
	 * @param directoryPath
	 * @param extensionsFilter
	 * @return
	 */
	
	public static List<File> listFilesInDirectoryNonRecursive(
			String directoryPath, String[] extensionsFilter) {
		
		File directory = new File(directoryPath);
		return (List<File>) FileUtils.listFiles(
				directory, extensionsFilter, false);
		
	}

}
