package ai.hyperlearning.ontopop.graph.gremlin.server;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.MessageTextSerializer;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gremlin Server Graph Traversal Source Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class GremlinServerTraversalSourceConfig {
	
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
	
	@Value("${storage.graph.gremlin-server.serializer.className}")
	private String serializerClassName;
	
	@Value("${storage.graph.gremlin-server.serializer.ioRegistries}")
	private String ioRegistries;
	
	@Value("${storage.graph.gremlin-server.serializer.serializeResultToString}")
	private boolean serializeResultToString;
	
	@Bean("gremlinServerTraversalSource")
	public GraphTraversalSource getGremlinServerTraversalSource() 
			throws ClassNotFoundException, NoSuchMethodException, 
			SecurityException, InstantiationException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException {
		
		// Configure the serializer
		final Map<String, Object> serializerSettings = new HashMap<>();
		serializerSettings.put(
				"ioRegistries", ioRegistries);
		serializerSettings.put(
				"serializeResultToString", serializeResultToString);
		Class<?> clazz = Class.forName(serializerClassName);
		Constructor<?> constructor = clazz.getConstructor();
		MessageTextSerializer<?> serializer = 
				(MessageTextSerializer<?>) constructor.newInstance();
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
