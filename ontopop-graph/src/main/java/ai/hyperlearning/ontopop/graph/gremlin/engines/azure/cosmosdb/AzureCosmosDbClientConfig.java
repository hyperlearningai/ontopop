package ai.hyperlearning.ontopop.graph.gremlin.engines.azure.cosmosdb;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ser.GraphSONMessageSerializerV2d0;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Azure CosmosDB Graph Database Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class AzureCosmosDbClientConfig {
	
	@Value("${storage.graph.gremlin-server.host}")
	private String host;
	
	@Value("${storage.graph.gremlin-server.port}")
	private int port;
	
	@Value("${storage.graph.gremlin-server.username}")
	private String username;
	
	@Value("${storage.graph.gremlin-server.password}")
	private String password;
	
	private static final boolean ENABLE_SSL = true;
	private static final boolean SERIALIZE_RESULT_TO_STRING = true;
	
	@Bean("azureCosmosDbgremlinServerClient")
	public Client azureCosmosDbgremlinServerClient() {
		
		// Configure the serializer
		final Map<String, Object> serializerSettings = new HashMap<>();
		serializerSettings.put(
				"serializeResultToString", SERIALIZE_RESULT_TO_STRING);
		GraphSONMessageSerializerV2d0 serializer = 
				new GraphSONMessageSerializerV2d0();
        serializer.configure(serializerSettings, null);
		
        // Create the connection to the remote Azure CosmosDB Gremlin server
		Cluster cluster = Cluster.build()
				.addContactPoint(host)
				.port(port)
				.credentials(username, password)
				.enableSsl(ENABLE_SSL)
				.serializer(serializer)
				.create();
		return cluster.connect();
		
	}

}
