package ai.hyperlearning.ontopop.triplestore.jena;

import org.springframework.beans.factory.annotation.Value;
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
public class JenaFusekiWebClientConfig {
	
	@Value("${storage.triplestore.apache-jena.fuseki.url}")
	private String fusekiUrl;
	
	@Value("${web.client.codecs.maxInMemorySize}")
	private int webClientMaxInMemorySize;
	
	@Bean
	public WebClient fusekiWebClient() {
		
		return WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder()
			            .codecs(configurer -> configurer
			                      .defaultCodecs()
			                      .maxInMemorySize(webClientMaxInMemorySize * 1024 * 1024))
			            		.build())
				.baseUrl(fusekiUrl)
				.build();
		
	}

}
