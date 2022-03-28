package ai.hyperlearning.ontopop.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Object Storage Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum ObjectStorageServiceType {

    LOCAL("LOCAL"), 
    AWS_S3("AWS-S3"), 
    AZURE_STORAGE("AZURE-STORAGE");

    private final String label;
    private static final Map<String, ObjectStorageServiceType> LABEL_MAP =
            new HashMap<>();

    static {
        for (ObjectStorageServiceType f : values()) {
            LABEL_MAP.put(f.label, f);
        }
    }

    private ObjectStorageServiceType(final String label) {
        this.label = label;
    }

    public static ObjectStorageServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }

    @Override
    public String toString() {
        return label;
    }

}
