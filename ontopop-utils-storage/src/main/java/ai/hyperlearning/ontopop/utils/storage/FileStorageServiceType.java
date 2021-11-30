package ai.hyperlearning.ontopop.utils.storage;

/**
 * Supported File Storage Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum FileStorageServiceType {

	LOCAL("LOCAL"), 
	AWS_S3("AWS-S3"), 
	AZURE_STORAGE("AZURE-STORAGE");
	
	private final String text;
	
	FileStorageServiceType(final String text) {
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
	
}
