package ai.hyperlearning.ontopop.utils;

import java.nio.file.Paths;

/**
 * Ontology Resource Helper Functions
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class OntologyResourceUtils {
	
	/**
	 * Generate ontology resource filename
	 * @param ontologyId
	 * @param webhookEventId
	 * @param resourceFilename
	 * @param separator
	 * @return
	 */
	
	public static String generateOntologyResourceFilename(
			int ontologyId, 
			long webhookEventId, 
			String resourceFilename, 
			String separator) {
		return ontologyId 
				+ separator 
				+ webhookEventId 
				+ separator 
				+ resourceFilename;
	}
	
	/**
	 * Generate ontology filename given a resource path
	 * @param resourcePath
	 * @return
	 */
	
	public static String generateOntologyFilenameFromResourcePath(
			String resourcePath) {
		return Paths.get(resourcePath)
				.getFileName()
				.toString()
				.replaceAll(" ", "-")
				.trim();
	}

}
