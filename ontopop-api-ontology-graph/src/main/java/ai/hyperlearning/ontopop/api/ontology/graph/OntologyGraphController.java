package ai.hyperlearning.ontopop.api.ontology.graph;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.hyperlearning.ontopop.graph.GraphDatabaseService;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceFactory;
import ai.hyperlearning.ontopop.graph.GraphDatabaseServiceType;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Ontology Graph API Service - Graph Controller
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@RestController
@RequestMapping("/api/ontologies")
@Tag(name = "Graph API", description = "API for querying the OntoPop Graph Database")
public class OntologyGraphController {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(OntologyGraphController.class);
    
    @Autowired
    private GraphDatabaseServiceFactory graphDatabaseServiceFactory;
    
    @Value("${storage.graph.service}")
    private String storageGraphService;
    
    private GraphDatabaseServiceType graphDatabaseServiceType;
    private GraphDatabaseService graphDatabaseService;
    
    @PostConstruct
    private void postConstruct() throws IOException {
        
        // Instantiate and open the relevant graph database service
        graphDatabaseServiceType = GraphDatabaseServiceType
                .valueOfLabel(storageGraphService.toUpperCase());
        graphDatabaseService = graphDatabaseServiceFactory
                .getGraphDatabaseService(graphDatabaseServiceType);
        graphDatabaseService.openGraph();
        LOGGER.debug("Using the {} graph database service.",
                graphDatabaseServiceType);
        
    }

}
