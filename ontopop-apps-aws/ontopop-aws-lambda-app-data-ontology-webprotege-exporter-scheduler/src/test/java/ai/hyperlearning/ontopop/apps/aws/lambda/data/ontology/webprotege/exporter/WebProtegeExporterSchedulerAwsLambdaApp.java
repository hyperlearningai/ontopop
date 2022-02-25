package ai.hyperlearning.ontopop.apps.aws.lambda.data.ontology.webprotege.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

import ai.hyperlearning.ontopop.data.ontology.webprotege.exporter.scheduler.WebProtegeExporterSchedulerService;

/**
 * AWS WebProtege Exporter Service Lambda Application - Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@ComponentScan(basePackages = {"ai.hyperlearning.ontopop"})
@EntityScan("ai.hyperlearning.ontopop.model")
@SpringBootApplication
public class WebProtegeExporterSchedulerAwsLambdaApp implements CommandLineRunner {
    
    @Autowired
    private WebProtegeExporterSchedulerService webProtegeExporterSchedulerService;

    public static void main(String[] args) {
        SpringApplication.run(
                WebProtegeExporterSchedulerAwsLambdaApp.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        webProtegeExporterSchedulerService.runScheduledWebProtegeExporterService();
    }

}
