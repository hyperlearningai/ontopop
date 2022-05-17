package ai.hyperlearning.ontopop.webprotege;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * WebProtege Authentication Scheduler
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Service
@ConditionalOnExpression("'${plugins.webprotege.authenticator.enabled}'.equals('true') and "
        + "'${plugins.webprotege.authenticator.scheduler.enabled}'.equals('true')")
public class WebProtegeAuthScheduler {
    
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WebProtegeAuthScheduler.class);
    
    private static final String WEBPROTEGE_DEFAULT_PROJECT_ID_ENV_KEY = 
            "WEBPROTEGE_DEFAULT_PROJECT_ID";
    
    @Autowired
    private WebProtegeDownloader webProtegeDownloader;
    
    @PostConstruct
    public void authenticateOnStartup() {
        authenticate();
    }
    
    @Scheduled(cron = "${plugins.webprotege.authenticator.scheduler.cron}")
    public void authenticateOnCronSchedule() {
        authenticate();
    }
    
    private void authenticate() {
        if (System.getenv(WEBPROTEGE_DEFAULT_PROJECT_ID_ENV_KEY) != null) {
            try {
                LOGGER.info("Checking WebProtege authentication status.");
                webProtegeDownloader.run(System.getenv(
                        WEBPROTEGE_DEFAULT_PROJECT_ID_ENV_KEY), 
                        null, null);
                LOGGER.info("WebProtege authentication status is OK.");
                LOGGER.debug("WebProtege authenticated JSESSIONID cookie "
                        + "value: {}", WebProtegeAuthSession
                            .getJSessionIdCookieValue());
            } catch (Exception e) {
                LOGGER.warn("An error was encountered when attempting to "
                        + "check the WebProtege authentication status.", e);
            }
        }
    }

}
