package ai.hyperlearning.ontopop.git;

/**
 * Supported Git Services
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public enum GitServiceType {

    LOCAL("LOCAL"), 
    GITHUB("GITHUB");

    private final String text;

    GitServiceType(final String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

}
