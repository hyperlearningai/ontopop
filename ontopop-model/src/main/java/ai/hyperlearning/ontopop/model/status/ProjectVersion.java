package ai.hyperlearning.ontopop.model.status;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Resolve Project Version
 *
 * @author jillurquddus
 * @since 2.0.0
 */

@Component
@ConfigurationProperties(prefix = "spring.application")
public class ProjectVersion {
    
    private static final String VERSION_PLACEHOLDER = "${project.version}";
    private static final String VERSION_DEFAULT = "2.0.0";
    
    private String version;

    public String getVersion() {
        return version.equals(VERSION_PLACEHOLDER) ? 
                VERSION_DEFAULT : version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
