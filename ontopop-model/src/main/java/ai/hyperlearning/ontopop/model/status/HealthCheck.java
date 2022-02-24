package ai.hyperlearning.ontopop.model.status;

import java.io.Serializable;

/**
 * Health Check Model
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class HealthCheck implements Serializable {

    private static final long serialVersionUID = 1006853109242443460L;
    private String name;
    private String version;
    
    public HealthCheck() {
        
    }

    public HealthCheck(String name, String version) {
        super();
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "HealthCheck ["
                + "name=" + name + ", "
                + "version=" + version
                + "]";
    }

}
