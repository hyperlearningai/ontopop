package ai.hyperlearning.ontopop.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Custom Request Logging Filter Configuration
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class RequestLoggingFilterConfiguration {
	
	@Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("Request Data: ");
        return filter;
    }

}
