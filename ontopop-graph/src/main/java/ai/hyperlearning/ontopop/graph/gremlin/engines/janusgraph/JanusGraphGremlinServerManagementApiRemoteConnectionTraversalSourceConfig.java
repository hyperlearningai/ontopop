package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV1d0;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Janus Graph Gremlin Server Management API Graph Traversal Source Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SuppressWarnings("deprecation")
@Configuration
public class JanusGraphGremlinServerManagementApiRemoteConnectionTraversalSourceConfig {
    
    private static final List<String> IO_REGISTRIES = Arrays.asList(
            "org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry");
    private static final boolean SERIALIZE_RESULT_TO_STRING = false;
    
    @Value("${storage.graph.gremlin-server.host}")
    private String host;
    
    @Value("${storage.graph.gremlin-server.port}")
    private int port;
    
    @Value("${storage.graph.gremlin-server.username}")
    private String username;
    
    @Value("${storage.graph.gremlin-server.password}")
    private String password;
    
    @Value("${storage.graph.gremlin-server.enableSsl}")
    private boolean enableSsl;
    
    @Value("${storage.graph.gremlin-server.remoteTraversalSourceName}")
    private String remoteTraversalSourceName;
    
    @Bean("janusGraphGremlinServerManagementApiTraversalSource")
    public GraphTraversalSource getJanusGraphGremlinServerManagementApiTraversalSource() 
            throws SecurityException, IllegalArgumentException {
        
        // Configure the serializer
        final Map<String, Object> serializerSettings = new HashMap<>();
        serializerSettings.put(
                "ioRegistries", IO_REGISTRIES);
        serializerSettings.put(
                "serializeResultToString", SERIALIZE_RESULT_TO_STRING);
        GryoMessageSerializerV1d0 serializer = new GryoMessageSerializerV1d0();
        serializer.configure(serializerSettings, null);
        
        // Create the connection to the remote Gremlin server
        Cluster cluster = StringUtils.isBlank(username) ? 
                Cluster.build()
                    .addContactPoint(host)
                    .port(port)
                    .enableSsl(enableSsl)
                    .serializer(serializer)
                    .create() : 
                        Cluster.build()
                            .addContactPoint(host)
                            .port(port)
                            .credentials(username, password)
                            .enableSsl(enableSsl)
                            .serializer(serializer)
                            .create();
        
        // Generate a reusable Graph Traversal Source
        return traversal().withRemote(
                DriverRemoteConnection.using(
                        cluster, remoteTraversalSourceName));
        
    }

}
