package ai.hyperlearning.ontopop.webprotege;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

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
    
    private static final int TASK_SCHEDULER_POOL_SIZE = 2;
    private static final String TASK_SCHEDULER_THREAD_NAME_PREFIX = 
            "OntoPopThreadPoolTaskScheduler";
    
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = 
                new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(
                TASK_SCHEDULER_POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix(
                TASK_SCHEDULER_THREAD_NAME_PREFIX);
        return threadPoolTaskScheduler;
    }

}
