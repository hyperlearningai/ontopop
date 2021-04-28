package ai.hyperlearning.ontology.services.api.graph.schedulers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ai.hyperlearning.ontology.services.api.graph.statics.GraphAPIStaticObjects;
import ai.hyperlearning.ontology.services.graphdb.impl.GraphDatabaseImplementation;
import ai.hyperlearning.ontology.services.utils.GlobalProperties;
import ai.hyperlearning.ontology.services.utils.TinkerGraphProperties;

/**
 * TinkerGraph Write Scheduler
 * 
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 *
 */

@Component
public class TinkerGraphWriteScheduler {
	
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(TinkerGraphWriteScheduler.class);
	
	@Autowired
	private GlobalProperties globalProperties;
	
	@Autowired
	private TinkerGraphProperties tinkerGraphProperties;
	
	@Scheduled(fixedRate = 600000, initialDelay = 30000)
	public void writeGraph() {
		
		// Get the graph database engine and write location
		GraphDatabaseImplementation graphDatabaseImplementation = 
				GraphDatabaseImplementation.valueOf(globalProperties
						.getGraphDbEngine().toUpperCase().strip());
		String tinkerGraphGraphLocation = tinkerGraphProperties
				.getGremlinTinkerGraphGraphLocation();
		if ( graphDatabaseImplementation == 
				GraphDatabaseImplementation.TINKERGRAPH && 
					!StringUtils.isBlank(tinkerGraphGraphLocation) ) {
			
			// Write the graph to disk
			try {
				
				LOGGER.debug("Writing TinkerGraph to disk at {}", 
						tinkerGraphGraphLocation);
				GraphAPIStaticObjects.getGraphDatabaseManager()
					.writeGraph(tinkerGraphGraphLocation);
				
			} catch (Exception e) {
				
				LOGGER.error("Error when writing TinkerGraph to disk at {}", 
						tinkerGraphGraphLocation, e);
				
			}
			
		}
		
	}

}
