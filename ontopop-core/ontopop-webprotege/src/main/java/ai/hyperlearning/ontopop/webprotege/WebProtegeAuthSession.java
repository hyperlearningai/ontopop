package ai.hyperlearning.ontopop.webprotege;

/**
 * WebProtege Authenticated Session
 *
 * @author jillurquddus
 * @since 2.0.0
 */

public class WebProtegeAuthSession {
    
    private WebProtegeAuthSession() {
        throw new IllegalStateException("The WebProtegeAuthSession "
                + "class cannot be instantiated.");
    }
    
    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    private static String jSessionIdCookieValue = null;

    protected static String getJSessionIdCookieValue() {
        return WebProtegeAuthSession.jSessionIdCookieValue;
    }
    
    protected static void setJSessionIdCookieValue(
            String jSessionIdCookieValue) {
        WebProtegeAuthSession.jSessionIdCookieValue = 
                jSessionIdCookieValue;
    }
    
}
