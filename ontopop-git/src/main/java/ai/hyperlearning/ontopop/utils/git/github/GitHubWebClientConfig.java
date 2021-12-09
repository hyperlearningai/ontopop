package ai.hyperlearning.ontopop.utils.git.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * GitHub Web Client Bean
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Configuration
public class GitHubWebClientConfig {
	
	private static final String BASE_URL = "https://api.github.com";
	
	@Value("${web.client.codecs.maxInMemorySize}")
	private int webClientMaxInMemorySize;
	
	@Bean
	public WebClient getWebClient() {
		
		return WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder()
			            .codecs(configurer -> configurer
			                      .defaultCodecs()
			                      .maxInMemorySize(webClientMaxInMemorySize * 1024 * 1024))
			            		.build())
				.baseUrl(BASE_URL)
				.build();
		
	}

}
