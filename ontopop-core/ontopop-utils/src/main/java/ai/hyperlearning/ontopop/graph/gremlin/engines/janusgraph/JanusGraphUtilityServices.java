package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.diskstorage.BackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JanusGraph Utility Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class JanusGraphUtilityServices {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(JanusGraphUtilityServices.class);

    private JanusGraphUtilityServices() {
        throw new IllegalStateException("JanusGraph Utility Services class "
                + "cannot be instantiated.");
    }

    /**
     * Delete the graph managed by the JanusGraph instance
     * 
     * @param janusGraphConfFilePath
     * @throws BackendException
     */

    public static void deleteGraph(String janusGraphConfFilePath)
            throws BackendException {
        final JanusGraph graph =
                (JanusGraph) GraphFactory.open(janusGraphConfFilePath);
        JanusGraphFactory.drop(graph);
    }

    /**
     * Create the JanusGraph graph schema
     * 
     * @param janusGraphConfFilePath
     */

    public static void createSchema(String janusGraphConfFilePath) {

        // Get the JanusGraph management service
        final JanusGraph graph =
                (JanusGraph) GraphFactory.open(janusGraphConfFilePath);
        final JanusGraphManagement management = graph.openManagement();
        try {

            if (!doesSchemaExist(management)) {

                LOGGER.info("JanusGraph schema does not already exist.");
                createProperties(management);
                createVertexLabels(management);
                createEdgeLabels(management);
                createCompositeIndexes(management);
                management.commit();

            } else {
                LOGGER.info("JanusGraph schema already exists. Consider "
                        + "running the JanusGraph deletion service and "
                        + "then recreating the schema.");
            }

        } catch (Exception e) {

            management.rollback();
            LOGGER.error("Error encountered when creating the "
                    + "JanusGraph schema", e);

        } finally {
            try {
                graph.close();
            } catch (Exception e) {

            }
        }

    }

    /**
     * Naive check as to whether the schema already exists
     * 
     * @param management
     * @return
     */

    private static boolean doesSchemaExist(
            final JanusGraphManagement management) {
        return management.getVertexLabel("class") != null;
    }

    /**
     * Create vertex labels
     * 
     * @param management
     */

    private static void createVertexLabels(
            final JanusGraphManagement management) {
        LOGGER.info("Creating JanusGraph vertex labels");
        management.makeVertexLabel("class").make();
    }

    /**
     * Create edge labels
     * 
     * @param management
     */

    private static void createEdgeLabels(
            final JanusGraphManagement management) {
        LOGGER.info("Creating JanusGraph edge labels");
        management.makeEdgeLabel("subClassOf").make();
    }

    /**
     * Create vertex and edge properties
     * 
     * @param management
     */

    private static void createProperties(
            final JanusGraphManagement management) {

        // Common vertex properties
        LOGGER.info("Creating JanusGraph common vertex properties");
        management.makePropertyKey("iri").dataType(String.class).make();
        management.makePropertyKey("ontologyId").dataType(Integer.class).make();
        management.makePropertyKey("latestGitWebhookId").dataType(Long.class)
                .make();
        management.makePropertyKey("vertexKey").dataType(String.class).make();
        management.makePropertyKey("vertexId").dataType(Long.class).make();

        // Common edge properties
        LOGGER.info("Creating JanusGraph common edge properties");
        management.makePropertyKey("sourceVertexKey").dataType(String.class)
                .make();
        management.makePropertyKey("sourceVertexId").dataType(Long.class)
                .make();
        management.makePropertyKey("targetVertexKey").dataType(String.class)
                .make();
        management.makePropertyKey("targetVertexId").dataType(Long.class)
                .make();

    }

    /**
     * Create vertex and edge composite indexes for exact match lookups
     * 
     * @param management
     */

    private static void createCompositeIndexes(
            final JanusGraphManagement management) {

        // Vertex-centric indexes
        LOGGER.info("Creating JanusGraph vertex-centric indexes");
        management.buildIndex("iriIndex", Vertex.class)
                .addKey(management.getPropertyKey("iri")).buildCompositeIndex();
        management.buildIndex("ontologyIdIndex", Vertex.class)
                .addKey(management.getPropertyKey("ontologyId"))
                .buildCompositeIndex();
        management.buildIndex("vertexKeyIndex", Vertex.class)
                .addKey(management.getPropertyKey("vertexKey"))
                .buildCompositeIndex();
        management.buildIndex("vertexIdIndex", Vertex.class)
                .addKey(management.getPropertyKey("vertexId"))
                .buildCompositeIndex();

        // Edge-centric indexes
        LOGGER.info("Creating JanusGraph edge-centric indexes");
        management.buildIndex("sourceVertexKeyIndex", Edge.class)
                .addKey(management.getPropertyKey("sourceVertexKey"))
                .buildCompositeIndex();
        management.buildIndex("sourceVertexIdIndex", Edge.class)
                .addKey(management.getPropertyKey("sourceVertexId"))
                .buildCompositeIndex();
        management.buildIndex("targetVertexKeyIndex", Edge.class)
                .addKey(management.getPropertyKey("targetVertexKey"))
                .buildCompositeIndex();
        management.buildIndex("targetVertexIdIndex", Edge.class)
                .addKey(management.getPropertyKey("targetVertexId"))
                .buildCompositeIndex();

    }

}
