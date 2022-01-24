package ai.hyperlearning.ontopop.triplestore.jena;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Apache Jena Fuseki Web Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
@ConditionalOnProperty(
        value = "storage.triplestore.service",
        havingValue = "apache-jena")
public class JenaFusekiWebClientConfig {

    @Value("${storage.triplestore.apache-jena.fuseki.url}")
    private String fusekiUrl;
    
    @Value("${storage.triplestore.apache-jena.fuseki.username}")
    private String fusekiUsername;
    
    @Value("${storage.triplestore.apache-jena.fuseki.password}")
    private String fusekiPassword;

    @Value("${web.client.codecs.maxInMemorySize}")
    private int webClientMaxInMemorySize;

    @Bean("fusekiWebClient")
    public WebClient getFusekiWebClient() {

        if ( StringUtils.isBlank(fusekiUsername) ) {
            
            return WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(
                                            webClientMaxInMemorySize * 1024 * 1024))
                            .build())
                    .baseUrl(fusekiUrl)
                    .build();
            
        } else {
            
            // HTTP Basic Authentication
            // Make sure that the relevant username and password has been
            // defined in ${FUSEKI-SERVER}/run/shiro.ini
            // Reference: https://jena.apache.org/documentation/fuseki2/fuseki-security.html
            return WebClient.builder()
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(
                                            webClientMaxInMemorySize * 1024 * 1024))
                            .build())
                    .baseUrl(fusekiUrl)
                    .defaultHeaders(
                            header -> header.setBasicAuth(
                                    fusekiUsername, fusekiPassword))
                    .build();
            
        }

    }

}
