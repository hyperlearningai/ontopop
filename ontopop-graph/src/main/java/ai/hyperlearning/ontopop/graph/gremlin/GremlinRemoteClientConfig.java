package ai.hyperlearning.ontopop.graph.gremlin;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gremlin Remote Graph Database Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class GremlinRemoteClientConfig {
	
	@Value("${storage.graph.gremlin-remote-graph.configuration-filename}")
	private String gremlinRemoteConfigurationFilename;
	
	@Bean("gremlinRemoteGraphClient")
	public Client getClient() throws FileNotFoundException {
		Cluster cluster = Cluster.build(
				new File(gremlinRemoteConfigurationFilename)).create();
		return cluster.connect();
	}

}
