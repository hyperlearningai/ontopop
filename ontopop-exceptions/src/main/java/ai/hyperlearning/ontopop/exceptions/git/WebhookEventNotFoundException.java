package ai.hyperlearning.ontopop.exceptions.git;

/**
 * Webhook Event Model - Not Found Custom Exception
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebhookEventNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2035416804413039332L;
	
	public WebhookEventNotFoundException(long id) {
		super("Webhook with ID " + id + " not found.");
	}

}
