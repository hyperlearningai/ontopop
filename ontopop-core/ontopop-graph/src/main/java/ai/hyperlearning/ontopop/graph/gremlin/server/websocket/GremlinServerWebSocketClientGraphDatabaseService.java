package ai.hyperlearning.ontopop.graph.gremlin.server.websocket;

import java.io.IOException;

import org.apache.tinkerpop.gremlin.groovy.engine.GremlinExecutor;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.graph.gremlin.GremlinGraphDatabaseService;

/**
 * Gremlin Server Bytecode Graph Database Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnProperty(
        value = "storage.graph.service",
        havingValue = "gremlin-server-ws")
public class GremlinServerWebSocketClientGraphDatabaseService
        extends GremlinGraphDatabaseService {

    @Autowired(required = false)
    @Qualifier("gremlinServerWebSocketClientTraversalSource")
    protected GraphTraversalSource gremlinServerWebSocketClientTraversalSource;

    /**************************************************************************
     * GRAPH INSTANCE MANAGEMENT
     *************************************************************************/

    @Override
    public GraphTraversalSource openGraph() throws IOException {
        super.g = gremlinServerWebSocketClientTraversalSource;
        bindings.putIfAbsent("g", g);
        bindings.putIfAbsent("graph", g.getGraph());
        gremlinExecutor = GremlinExecutor.build().evaluationTimeout(15000L)
                .globalBindings(bindings).create();
        return g;
    }

}
