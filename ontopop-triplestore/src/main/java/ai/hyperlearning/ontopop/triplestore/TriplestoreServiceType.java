package ai.hyperlearning.ontopop.triplestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported Triplestore Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum TriplestoreServiceType {
	
	APACHE_JENA("APACHE-JENA"), 
	ONTOTEXT_GRAPHDB("ONTOTEXT-GRAPHDB");
	
	private final String label;
	private static final Map<String, TriplestoreServiceType> LABEL_MAP = 
			new HashMap<>();
	
	static {
        for (TriplestoreServiceType f: values()) {
        		LABEL_MAP.put(f.label, f);
        }
    }
	
	private TriplestoreServiceType(final String label) {
		this.label = label;
	}
	
	public static TriplestoreServiceType valueOfLabel(String label) {
        return LABEL_MAP.get(label);
    }
	
	@Override
	public String toString() {
		return label;
	}

}
