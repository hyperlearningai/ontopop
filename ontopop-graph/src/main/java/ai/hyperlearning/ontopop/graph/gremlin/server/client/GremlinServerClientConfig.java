package ai.hyperlearning.ontopop.graph.gremlin.server.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV2d0;
import org.apache.tinkerpop.gremlin.driver.ser.MessageTextSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gremlin Server Graph Database Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value = "storage.graph.service",
        havingValue = "gremlin-server-client")
public class GremlinServerClientConfig {

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

    @Value("${storage.graph.gremlin-server.serializer.className}")
    private String serializerClassName;

    @Value("${storage.graph.gremlin-server.serializer.serializeResultToString}")
    private boolean serializeResultToString;

    @Bean("gremlinServerClient")
    public Client getGremlinServerClient()
            throws ClassNotFoundException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        // Configure the serializer
        final Map<String, Object> serializerSettings = new HashMap<>();
        serializerSettings.put("serializeResultToString",
                serializeResultToString);
        Class<?> clazz = Class.forName(serializerClassName);
        Constructor<?> constructor = clazz.getConstructor();
        MessageTextSerializer<?> messageTextSerializer = null;
        GraphSONMessageSerializerV2d0 defaultSerializer = null;
        boolean successfulCast = false;
        try {

            // Attempt to cast the serializer as a MessageTextSerializer
            messageTextSerializer =
                    (MessageTextSerializer<?>) constructor.newInstance();
            messageTextSerializer.configure(serializerSettings, null);
            successfulCast = true;

        } catch (ClassCastException e) {

            // Revert to a default serializer
            defaultSerializer = new GraphSONMessageSerializerV2d0();
            defaultSerializer.configure(serializerSettings, null);

        }

        // Create the connection to the remote Gremlin server
        Cluster cluster = Cluster.build().addContactPoint(host).port(port)
                .credentials(username, password).enableSsl(enableSsl)
                .serializer(successfulCast ? messageTextSerializer
                        : defaultSerializer)
                .create();
        return cluster.connect();

    }

}
