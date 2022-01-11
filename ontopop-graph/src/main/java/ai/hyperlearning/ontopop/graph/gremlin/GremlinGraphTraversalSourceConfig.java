package ai.hyperlearning.ontopop.graph.gremlin;

import java.io.File;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gremlin Graph Traversal Source Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnExpression(
        "'${storage.graph.service}'.equals('gremlin-graph') or "
        + "'${storage.graph.service}'.equals('tinkergraph')")
public class GremlinGraphTraversalSourceConfig {
	
	@Value("${storage.graph.gremlin-graph.configuration-filename}")
	private String gremlinConfigurationFilename;
	
	@Bean("gremlinGraphTraversalSource")
	public GraphTraversalSource getGremlinGraphTraversalSource() 
			throws ConfigurationException {
		
		// Load the Gremlin Graph configuration
		Configurations configurations = new Configurations();
		org.apache.commons.configuration2.Configuration configuration = 
				configurations.properties(
						new File(gremlinConfigurationFilename));
		
		// Generate a reusable Graph Traversal Source
		Graph graph = GraphFactory.open(configuration);
		return graph.traversal();
		
	}

}
