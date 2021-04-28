package ai.hyperlearning.ontology.services.etl.parsers;

import java.util.Set;

import ai.hyperlearning.ontology.services.model.datamanagement.Dataset;

/**
 * Data Catalogue Parser Abstract Class
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public abstract class DataCatalogueParser {
	
	public abstract Set<Dataset> parseDataCatalogue(String filename);

}
