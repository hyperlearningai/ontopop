package ai.hyperlearning.ontopop.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Configuration Server Spring Boot Application
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@SpringBootApplication
@EnableConfigServer
public class ConfigServer {
	
	public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
    }

}
