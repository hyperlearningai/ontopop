package ai.hyperlearning.ontopop.owl.modellers.ontopop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ai.hyperlearning.ontopop.model.graph.SimpleOntologyPropertyGraph;

/**
 * RDF/XML to native OntoPop JSON format modeller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class NativeRdfXmlModeller {
    
    private NativeRdfXmlModeller() {
        throw new IllegalStateException("The NativeRdfXmlModeller utility "
            + "class cannot be instantiated.");
    }
    
    /**
     * Model the RDF/XML contents of a given OWL file
     * using OntoPop's native property graph entity model
     * @param owlFile
     * @return
     * @throws JsonProcessingException 
     */
    
    public static String model(
            SimpleOntologyPropertyGraph simpleOntologyPropertyGraph) 
                    throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper()
                .writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(simpleOntologyPropertyGraph);
    }

}
