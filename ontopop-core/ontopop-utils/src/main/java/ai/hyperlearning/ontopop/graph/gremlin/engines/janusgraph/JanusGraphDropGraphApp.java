package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JanusGraph Drop Graph Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class JanusGraphDropGraphApp {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JanusGraphDropGraphApp.class);

    public static void main(String[] args) {
        LOGGER.info("Running the JanusGraph graph deletion service.");
        try {
            LOGGER.info("Deleting the JanusGraph graph.");
            JanusGraphUtilityServices.deleteGraph(args[0]);
            LOGGER.info("Finished deleting the JanusGraph graph.");
        } catch (Exception e) {
            LOGGER.error(
                    "Error encountered when deleting the " + "JanusGraph graph",
                    e);
        }
        LOGGER.info("Finished running the JanusGraph graph deletion service.");
        System.exit(0);
    }

}
