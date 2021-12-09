package ai.hyperlearning.ontopop.utils.storage;

import java.util.HashMap;
import java.util.Map;

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
	
	private final String label;
	private static final Map<String, FileStorageServiceType> LABEL_MAP = 
			new HashMap<>();
	
	static {
        for (FileStorageServiceType f: values()) {
        		LABEL_MAP.put(f.label, f);
        }
    }
	
	private FileStorageServiceType(final String label) {
		this.label = label;
	}
	
	public static FileStorageServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }
	
	@Override
	public String toString() {
		return label;
	}
	
}
