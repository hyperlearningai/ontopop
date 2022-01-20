package ai.hyperlearning.ontopop.graph.gremlin.engines.janusgraph;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV3d0;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JanusGraph Gremlin Server HTTP Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value = "storage.graph.service",
        havingValue = "janusgraph-http")
public class JanusGraphGremlinServerHTTPClientConfig {
    
    @Value("${storage.graph.gremlin-server.host}")
    private String host;

    @Value("${storage.graph.gremlin-server.port}")
    private int port;

    @Value("${storage.graph.gremlin-server.username}")
    private String username;

    @Value("${storage.graph.gremlin-server.password}")
    private String password;
    
    @Value("${storage.graph.gremlin-server.enableSsl:true}")
    private Boolean enableSsl;
    
    private static final boolean SERIALIZE_RESULT_TO_STRING = true;
    
    @Bean("janusGraphGremlinServerHttpClient")
    public Client getJanusGraphGremlinServerHttpClient() {
        
        // Configure the serializer
        final Map<String, Object> serializerSettings = new HashMap<>();
        serializerSettings.put("serializeResultToString",
                SERIALIZE_RESULT_TO_STRING);
        GraphSONMessageSerializerV3d0 serializer = 
                new GraphSONMessageSerializerV3d0();
        serializer.configure(serializerSettings, null);
        
        // Create the connection to the remote JanusGraph Gremlin server
        Cluster cluster = null;
        if ( StringUtils.isBlank(username) ) {
            
            cluster = Cluster.build()
                    .addContactPoint(host)
                    .port(port)
                    .enableSsl(enableSsl)
                    .serializer(serializer)
                    .create();
            
        } else {
            
            cluster = Cluster.build()
                    .addContactPoint(host)
                    .port(port)
                    .credentials(username, password)
                    .enableSsl(enableSsl)
                    .serializer(serializer)
                    .create();
            
        }
        
        return cluster.connect();
        
    }

}
