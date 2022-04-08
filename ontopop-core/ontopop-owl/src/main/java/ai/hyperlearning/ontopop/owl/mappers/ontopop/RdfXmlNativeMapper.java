package ai.hyperlearning.ontopop.owl.mappers.ontopop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;

/**
 * RDF/XML to native OntoPop JSON format mapper
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class RdfXmlNativeMapper {
    
    private RdfXmlNativeMapper() {
        throw new IllegalStateException("The NativeRdfXmlModeller utility "
            + "class cannot be instantiated.");
    }
    
    /**
     * Map the RDF/XML contents of a given OWL file
     * to OntoPop's native property graph entity model
     * @param owlFile
     * @return
     * @throws JsonProcessingException 
     */
    
    public static String map(
            SimpleOntologyPropertyGraph simpleOntologyPropertyGraph) 
                    throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper()
                .writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(simpleOntologyPropertyGraph);
    }

}
