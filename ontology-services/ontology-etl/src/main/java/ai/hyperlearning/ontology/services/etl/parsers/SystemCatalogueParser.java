package ai.hyperlearning.ontology.services.etl.parsers;

import java.util.Set;

/**
 * System Catalogue Parser Abstract Class
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public abstract class SystemCatalogueParser {
	
	public abstract Set<System> parseSystemCatalogue(String filename);

}
