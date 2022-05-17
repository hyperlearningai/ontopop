package ai.hyperlearning.ontopop.webprotege;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WebProtege Authentication Scheduler Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@EnableScheduling
@ConditionalOnExpression("'${plugins.webprotege.authenticator.enabled}'.equals('true') and "
        + "'${plugins.webprotege.authenticator.scheduler.enabled}'.equals('true')")
public class WebProtegeAuthSchedulerConfig {
    
    

}
