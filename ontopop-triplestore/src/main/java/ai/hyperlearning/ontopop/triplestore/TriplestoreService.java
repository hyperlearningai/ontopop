package ai.hyperlearning.ontopop.triplestore;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

/**
 * Triplestore Service Interface
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public interface TriplestoreService {

    /**************************************************************************
     * TRIPLESTORE MANAGEMENT
     *************************************************************************/
    
    Object getRepository(int id) throws IOException;

    void createRepository(int id) throws IOException;

    void deleteRepository(int id) throws IOException;

    void loadOntologyOwlRdfXml(int id, String owlSourceUri) throws IOException;

    void cleanup() throws IOException;
    
    /**************************************************************************
     * TRIPLESTORE QUERIES
     *************************************************************************/

    ResponseEntity<String> query(int id, String sparqlQuery) throws IOException;
    
    ResponseEntity<String> getData(int id) throws IOException;
    
}
