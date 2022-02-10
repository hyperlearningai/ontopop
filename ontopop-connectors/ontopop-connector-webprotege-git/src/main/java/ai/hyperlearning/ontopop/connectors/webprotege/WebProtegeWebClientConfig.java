package ai.hyperlearning.ontopop.connectors.webprotege;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebProtege Web Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class WebProtegeWebClientConfig {
    
    private static final String BASE_URL = "https://webprotege.stanford.edu";
    
    @Value("${web.client.codecs.maxInMemorySize}")
    private int webClientMaxInMemorySize;
    
    @Bean("webProtegeWebClient")
    public WebClient getWebProtegeWebClient() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs()
                                .maxInMemorySize(
                                        webClientMaxInMemorySize * 1024 * 1024))
                        .build())
                .baseUrl(BASE_URL)
                .build();
    }

}
