package ai.hyperlearning.ontology.services.search.interfaces;

/**
 * Search Service Interface
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public interface SearchService {
	
	/**************************************************************************
	 * SEARCH MANAGEMENT
	 *************************************************************************/
	
	public boolean doesIndexExist() throws Exception;
	
	public boolean createIndex() throws Exception;
	
	public boolean deleteIndex() throws Exception;
	
	/**************************************************************************
	 * DOCUMENT MANAGEMENT
	 *************************************************************************/

	public boolean indexDocuments(String documentsJson) throws Exception;
	
	public boolean deleteDocument(int id) throws Exception;
	
}
