package ai.hyperlearning.ontopop.exceptions.git;

import ai.hyperlearning.ontopop.exceptions.OntoPopException;

/**
 * Git Webhook Event Model - Not Found Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class GitWebhookNotFoundException extends OntoPopException {

    private static final long serialVersionUID = 2035416804413039332L;
    private static final String CLASS_NAME = 
            GitWebhookNotFoundException.class.getSimpleName();
    
    public GitWebhookNotFoundException() {
        super(CLASS_NAME);
    }

}
