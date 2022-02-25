package ai.hyperlearning.ontopop.apps.data.ontology.webprotege.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.scheduler.WebProtegeExporterSchedulerService;

/**
 * WebProtege Exporter Service - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@EnableScheduling
@SpringBootApplication
public class WebProtegeExporterSchedulerApp {
    
    @Autowired
    private WebProtegeExporterSchedulerService webProtegeExporterSchedulerService;
    
    public static void main(String[] args) {
        SpringApplication.run(WebProtegeExporterSchedulerApp.class, args);
    }
    
    @Scheduled(cron = "${plugins.webprotege.exporter.scheduler.cron}")
    public void runScheduledWebProtegeExporterService() {
        webProtegeExporterSchedulerService.runScheduledWebProtegeExporterService();
    }

}
