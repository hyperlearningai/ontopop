package ai.hyperlearning.ontology.services.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Apache TinkerPop TinkerGraph properties
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

@Component
@PropertySource("classpath:graphdb-tinkergraph.properties")
@ConfigurationProperties
@Validated
public class TinkerGraphProperties {
			
	@Value("${gremlin.graph}")
	private String gremlinGraph;
	
	@Value("${gremlin.tinkergraph.edgeIdManager}")
	private String gremlinTinkerGraphEdgeIdManager;
	
	@Value("${gremlin.tinkergraph.vertexIdManager}")
	private String gremlinTinkerGraphVertexIdManager;
	
	@Value("${gremlin.tinkergraph.vertexPropertyIdManager}")
	private String gremlinTinkerGraphVertexPropertyIdManager;
	
	@Value("${gremlin.tinkergraph.graphLocation}")
	private String gremlinTinkerGraphGraphLocation;
	
	@Value("${gremlin.tinkergraph.graphFormat}")
	private String gremlinTinkerGraphGraphFormat;

	public String getGremlinGraph() {
		return gremlinGraph;
	}

	public void setGremlinGraph(String gremlinGraph) {
		this.gremlinGraph = gremlinGraph;
	}

	public String getGremlinTinkerGraphEdgeIdManager() {
		return gremlinTinkerGraphEdgeIdManager;
	}

	public void setGremlinTinkerGraphEdgeIdManager(
			String gremlinTinkerGraphEdgeIdManager) {
		this.gremlinTinkerGraphEdgeIdManager = gremlinTinkerGraphEdgeIdManager;
	}

	public String getGremlinTinkerGraphVertexIdManager() {
		return gremlinTinkerGraphVertexIdManager;
	}

	public void setGremlinTinkerGraphVertexIdManager(
			String gremlinTinkerGraphVertexIdManager) {
		this.gremlinTinkerGraphVertexIdManager = 
				gremlinTinkerGraphVertexIdManager;
	}

	public String getGremlinTinkerGraphVertexPropertyIdManager() {
		return gremlinTinkerGraphVertexPropertyIdManager;
	}

	public void setGremlinTinkerGraphVertexPropertyIdManager(
			String gremlinTinkerGraphVertexPropertyIdManager) {
		this.gremlinTinkerGraphVertexPropertyIdManager = 
				gremlinTinkerGraphVertexPropertyIdManager;
	}

	public String getGremlinTinkerGraphGraphLocation() {
		return gremlinTinkerGraphGraphLocation;
	}

	public void setGremlinTinkerGraphGraphLocation(
			String gremlinTinkerGraphGraphLocation) {
		this.gremlinTinkerGraphGraphLocation = 
				gremlinTinkerGraphGraphLocation;
	}

	public String getGremlinTinkerGraphGraphFormat() {
		return gremlinTinkerGraphGraphFormat;
	}

	public void setGremlinTinkerGraphGraphFormat(
			String gremlinTinkerGraphGraphFormat) {
		this.gremlinTinkerGraphGraphFormat = gremlinTinkerGraphGraphFormat;
	}

	@Override
	public String toString() {
		return "TinkerGraphProperties ["
				+ "gremlinGraph=" 
					+ gremlinGraph + ", "
				+ "gremlinTinkerGraphEdgeIdManager=" 
					+ gremlinTinkerGraphEdgeIdManager + ", "
				+ "gremlinTinkerGraphVertexIdManager=" 
					+ gremlinTinkerGraphVertexIdManager + ", "
				+ "gremlinTinkerGraphVertexPropertyIdManager=" 
					+ gremlinTinkerGraphVertexPropertyIdManager + ", "
				+ "gremlinTinkerGraphGraphLocation=" 
					+ gremlinTinkerGraphGraphLocation + ", "
				+ "gremlinTinkerGraphGraphFormat=" 
					+ gremlinTinkerGraphGraphFormat
				+ "]";
	}

}
