package ai.hyperlearning.ontology.services.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date Time Utility Methods
 *
 * @author jillur.quddus@hyperlearning.ai
 * @since 0.0.1
 */

public class DateTimeUtils {
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Get the current local date time as a LocalDateTime object
	 * @return
	 */
	
	public static LocalDateTime getCurrentLocalDateTime() {
		return LocalDateTime.now();
	}

}
