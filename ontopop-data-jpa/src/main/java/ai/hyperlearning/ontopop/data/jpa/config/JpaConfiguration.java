package ai.hyperlearning.ontopop.data.jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("ai.hyperlearning.ontopop.data.jpa.repositories")
public class JpaConfiguration {

}
