package ai.hyperlearning.ontopop.graph.gremlin.server.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Gremlin Server HTTP Web Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnExpression("'${storage.graph.service}'.equals('gremlin-server-http') or "
        + "'${storage.graph.service}'.equals('janusgraph-server-http')")
public class GremlinServerHttpWebClientConfig {
    
    @Value("${storage.graph.gremlin-server.url}")
    private String url;
    
    @Value("${storage.graph.gremlin-server.username}")
    private String username;
    
    @Value("${storage.graph.gremlin-server.password}")
    private String password;
    
    @Value("${web.client.codecs.maxInMemorySize}")
    private int webClientMaxInMemorySize;
    
    @Bean("gremlinServerHttpWebClient")
    public WebClient getGremlinServerHttpWebClient() {

        if ( StringUtils.isBlank(username) ) {
            
            return WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(
                                            webClientMaxInMemorySize * 1024 * 1024))
                            .build())
                    .baseUrl(url)
                    .build();
            
        } else {
            
            // HTTP Basic Authentication
            // Make sure that the relevant username and password has been
            // created in a credentials graph and defined in gremlin-server.yaml
            return WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(
                                            webClientMaxInMemorySize * 1024 * 1024))
                            .build())
                    .baseUrl(url)
                    .defaultHeaders(
                            header -> header.setBasicAuth(username, password))
                    .build();
            
        }

    }

}
