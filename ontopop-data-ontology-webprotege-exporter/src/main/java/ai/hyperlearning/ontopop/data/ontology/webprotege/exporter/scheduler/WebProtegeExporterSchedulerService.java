package ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ai.hyperlearning.ontopop.data.jpa.repositories.OntologyRepository;
import ai.hyperlearning.ontopop.data.jpa.repositories.WebProtegeWebhookRepository;
import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.function.WebProtegeExporterFunction;
import ai.hyperlearning.ontopop.model.ontology.Ontology;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeMaxRevisionNumber;
import ai.hyperlearning.ontopop.model.webprotege.WebProtegeWebhook;

/**
 * WebProtege Exporter Scheduler Service
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@EnableScheduling
@ConditionalOnExpression("'${plugins.webprotege.exporter.enabled}'.equals('true') and "
        + "'${plugins.webprotege.exporter.scheduler.enabled}'.equals('true')")
public class WebProtegeExporterSchedulerService {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeExporterSchedulerService.class);
    
    @Autowired
    private OntologyRepository ontologyRepository;
    
    @Autowired
    private WebProtegeWebhookRepository webProtegeWebhookRepository;
    
    @Autowired
    private WebProtegeExporterFunction webProtegeExporterFunction;
    
    private List<Ontology> ontologiesWithWebProtegeProjectIds = 
            new ArrayList<>();
    private Map<String, Integer> webProtegeLatestRevisionNumbersMap = 
            new HashMap<String, Integer>();
    
    /**
     * Get all ontologies that have been defined with a WebProtege project ID
     */
    
    private void getOntologiesWithWebProtegeProjectIds() {
        ontologiesWithWebProtegeProjectIds = ontologyRepository
                .findByWebProtegeProjectIdIsNotNull();
    }
    
    /**
     * Get the latest revision numbers for each WebProtege project ID
     * that OntoPop is monitoring
     */
    
    private void getLatestWebProtegeRevisionNumbers() {
        
        if ( !ontologiesWithWebProtegeProjectIds.isEmpty() 
                && webProtegeWebhookRepository.count() > 0 ) {
            List<WebProtegeMaxRevisionNumber> webProtegeMaxRevisionNumbers = 
                    webProtegeWebhookRepository
                    .getMaxRevisionNumberPerWebProtegeWebhook();
            if ( !webProtegeMaxRevisionNumbers.isEmpty() ) {
                webProtegeLatestRevisionNumbersMap = webProtegeMaxRevisionNumbers
                        .stream().collect(Collectors.toMap(
                                WebProtegeMaxRevisionNumber::getProjectId, 
                                WebProtegeMaxRevisionNumber::getRevisionNumber));
                LOGGER.debug("Latest WebProtege revision numbers: {}", 
                        webProtegeLatestRevisionNumbersMap);
            }
        }
        
    }
    
    /**
     * Run the WebProtege exporter service for each ontology where the
     * latest WebProtege revision number exceeds the latest revision
     * number that has been processed
     */
    
    @Scheduled(cron = "${plugins.webprotege.exporter.scheduler.cron}")
    public void runScheduledWebProtegeExporterService() {
        
        // Get the latest revision numbers for each WebProtege project ID
        getOntologiesWithWebProtegeProjectIds();
        getLatestWebProtegeRevisionNumbers();
        
        // Run the WebProtege exporter service if required
        if ( !webProtegeLatestRevisionNumbersMap.isEmpty() ) {
            for ( Ontology ontology : ontologiesWithWebProtegeProjectIds ) {
                int webProtegeLatestRevisionNumberAvailable = 
                        webProtegeLatestRevisionNumbersMap.get(
                                ontology.getWebProtegeProjectId());
                if ( webProtegeLatestRevisionNumberAvailable > 
                    ontology.getLatestWebProtegeRevisionNumber() ) {
                    WebProtegeWebhook webProtegeWebhook = 
                            webProtegeWebhookRepository.
                            findByWebProtegeProjectIdAndRevisionNumber(
                                    ontology.getWebProtegeProjectId(), 
                                    webProtegeLatestRevisionNumberAvailable)
                            .get(0);
                    webProtegeExporterFunction.acceptPojo(webProtegeWebhook);
                }
            }
        }
        
    }

}
