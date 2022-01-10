package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JanusGraph Create Schema Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class JanusGraphCreateSchemaApp {
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(JanusGraphCreateSchemaApp.class);
    
    public static void main(String[] args) {
        LOGGER.info("Running the JanusGraph schema creation service.");
        try {
            LOGGER.info("Creating the JanusGraph schema.");
            JanusGraphUtilityServices.createSchema(args[0]);
        } catch (Exception e) {
            LOGGER.error("Error encountered when creating the "
                    + "JanusGraph schema", e);
        }
        LOGGER.info("Finished running the JanusGraph schema creation service.");
        System.exit(0);
    }

}
